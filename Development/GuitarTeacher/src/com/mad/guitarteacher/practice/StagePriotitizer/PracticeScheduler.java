package com.mad.guitarteacher.practice.StagePriotitizer;

import java.io.IOException;
import java.util.HashMap;

import android.content.Context;
import android.util.SparseArray;

import com.mad.guitarteacher.dataContract.GeneralUserInformationFile;
import com.mad.guitarteacher.dataContract.UserHistoryInformationFile;
import com.mad.guitarteacher.practice.ExerciseDoneReport;
import com.mad.guitarteacher.practice.ExercisePlan;
import com.mad.guitarteacher.practice.ExerciseStage;
import com.mad.guitarteacher.practice.ExerciseStageOption;
import com.mad.guitarteacher.practice.ExercisesManager;
import com.mad.guitarteacher.practice.Scenarios.OnScenarioDoneListener;
import com.mad.lib.R;
import com.mad.lib.utils.AppLibraryServiceProvider;

/**
 * A class responsible for Scheduling the next exercises for the user.
 * 
 * The list should be updated when exercises are done and when the app starts.
 * 
 * @author Nati
 * 
 */
public class PracticeScheduler implements OnScenarioDoneListener
{
	public static final int					INVALID_PLAN				=
																				-1;
	private static final int				PLAN_STAGES					=
																				3;
	private final IStagePrioritizer			m_pPrioritizer;
	private final SparseArray<ExercisePlan>	m_Plans						=
																				new SparseArray<ExercisePlan>();
	private int								m_nCreatedPlans				=
																				0;

	/**
	 * If true then the tuning exercise is presented before any other.
	 */
	private boolean							m_fWasTuningNotCompleted	=
																				true;

	/**
	 * Creates a new instance of the PracticeScheduler object.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException
	 */
	public PracticeScheduler(	final Context context,
								final IStagePrioritizer pPrioritizer,
								final UserHistoryInformationFile history)	throws InstantiationException,
																			IllegalAccessException,
																			IOException
	{
		// TODO: Get the path to the user information file.

		m_pPrioritizer = pPrioritizer.getClass().newInstance();

		m_pPrioritizer.initialize(history.Reports);

	}

	/**
	 * @param stage
	 * @return
	 */
	public ExercisePlan createPlan(final ExerciseStage stage)
	{
		ExercisesManager manager =
				AppLibraryServiceProvider.getInstance().get(
						R.service.exercises_manager);
		GeneralUserInformationFile fileUserInformation =
				manager.getActiveProfile().getUserInformation();
		HashMap<String, Integer> stageScores =
				fileUserInformation.BestScores;

		ExercisePlan plan;

		if (stage == null)
		{
			plan =
					m_pPrioritizer.createGeneralStagePlan(
							stageScores, manager.getLevelMap()
									.getFields());

		}
		else
		{
			plan =
					m_pPrioritizer.createStagePlan(stageScores,
							stage, PLAN_STAGES);
		}

		if (fileUserInformation.FinishedIntroduction == false)
		{
			addFirstTimeIntroductionExercise(manager, plan);
		}
		else
		{
			// Check if it is the first time that the plan is built.
			if (m_fWasTuningNotCompleted)
			{
				addTuningExercise(manager, plan);
			}
		}

		plan.setId(m_nCreatedPlans);
		m_Plans.append(m_nCreatedPlans++, plan);
		return plan;
	}

	public void setWasTuningCompleted(boolean fWasTuningCompleted)
	{
		m_fWasTuningNotCompleted = !fWasTuningCompleted;
	}

	/**
	 * Add the tuning scenario to the plan.
	 * 
	 * @param exercisesManager
	 *            - The exercises manager.
	 * @param plan
	 *            - The plan to add to.
	 */
	protected void addTuningExercise(	ExercisesManager exercisesManager,
										ExercisePlan plan)
	{
		// Get the tuning option
		ExerciseStageOption option =
				(ExerciseStageOption) exercisesManager.getStage(
						"Tuning").getOptions().toArray()[0];

		// Add the tuning option as first!
		plan.add(0, option);
	}

	/**
	 * Add the tuning scenario to the plan.
	 * 
	 * @param exercisesManager
	 *            - The exercises manager.
	 * @param plan
	 *            - The plan to add to.
	 */
	protected void addFirstTimeIntroductionExercise(ExercisesManager exercisesManager,
													ExercisePlan plan)
	{
		// Get the tuning option
		ExerciseStageOption option =
				(ExerciseStageOption) exercisesManager.getStage(
						"FirstTime").getOptions().toArray()[0];
		ExerciseStageOption toRemove =
				(ExerciseStageOption) exercisesManager.getStage(
						"Introduction").getOptions().toArray()[0];

		plan.remove(toRemove);
		// Add the tuning option as first!
		plan.add(0, option);
	}

	/**
	 * Return an array of all the planned options for the current stage.
	 * 
	 * This will compute the planned based on the score of the user on them.
	 * 
	 * TODO: Make sure this array is either cloned or marked read only somehow.
	 * 
	 * @return ExerciseStage[] - Planned stages.
	 */
	public ExercisePlan getPlan(final int nId)
	{
		if (nId == INVALID_PLAN)
		{
			return null;
		}

		ExercisePlan plan = m_Plans.get(nId);

		return plan;
	}

	@Override
	public void onScenarioDone(final ExerciseDoneReport report)
	{
		m_pPrioritizer.update(report);
	}

}
