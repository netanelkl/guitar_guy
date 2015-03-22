package com.mad.guitarteacher.services;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.Intent;

import com.mad.guitarteacher.activities.ExercisePickerActivity;
import com.mad.guitarteacher.activities.ExercisePlanActivity;
import com.mad.guitarteacher.activities.IPlayingDisplayer;
import com.mad.guitarteacher.activities.MainActivity;
import com.mad.guitarteacher.connect.UserProfile;
import com.mad.guitarteacher.dataContract.GeneralUserInformationFile;
import com.mad.guitarteacher.practice.ExerciseDoneReport;
import com.mad.guitarteacher.practice.ExercisePlan;
import com.mad.guitarteacher.practice.ExerciseStage;
import com.mad.guitarteacher.practice.ExerciseStageOption;
import com.mad.guitarteacher.practice.ExerciseTypes;
import com.mad.guitarteacher.practice.ExercisesManager;
import com.mad.guitarteacher.practice.Acts.DisplayInformationAct;
import com.mad.guitarteacher.practice.Scenarios.ExerciseScenario;
import com.mad.guitarteacher.practice.Scenarios.OnScenarioDoneListener;
import com.mad.guitarteacher.utils.Definitions;
import com.mad.lib.R;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.lib.utils.ErrorHandler;

/**
 * This class will provide the game logical manager.
 * 
 * It will operate the drawer and the Scenario to manage the game. This is
 * basically the logical side of GameActivity.
 * 
 * @author Nati
 * 
 */
public class GameManager implements IGameManager,
		OnScenarioDoneListener
{

	private GameState							m_GameState	=
																	GameState.Running;

	private WeakReference<IPlayingDisplayer>	m_GameDrawer;

	private ExerciseScenario					m_Scenario	=
																	null;

	private ExercisePlan						m_Plan		=
																	null;

	/**
	 * Creates a new game manager.
	 */
	public GameManager()
	{
	}

	@Override
	public void nextExercise(final Context context)
	{
		// First, get the current plan we are enrolled in :P
		Intent intent =
				new Intent(context, ExercisePlanActivity.class);
		intent.putExtra(Definitions.Intents.INTENT_PLAN_ID,
				m_Plan.getId());
		intent.putExtra(Definitions.Intents.INTENT_PLAN_ACTION,
				Definitions.Intents.PLAN_ACTION_EXERCISE_DONE);

		context.startActivity(intent);
		m_GameDrawer.get().destroy();

	}

	@Override
	public void onBackPressed(Context context)
	{
		switch (m_GameState)
		{
			case Ended:
				break;
			case Paused:
				resumeExercise(context);
				break;
			case Running:

				// If we are in the first act and it's an information display.
				// We won't pause the exercise but rather exit the act.
				if ((m_Scenario.getCurrentActIndex() == 0)
						&& (m_Scenario.getCurrentAct() instanceof DisplayInformationAct))
				{
					// Just go back to where we were.
					m_GameDrawer.get().destroy();
				}
				else
				{
					pauseExercise(context);
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void restartExercise(final Context context)
	{
		m_Scenario.pause();
		m_GameState = GameState.Running;
		m_Scenario.playScenario(m_GameDrawer.get(), this);
	}

	@Override
	public void switchToExercises(final Context context)
	{
		m_Scenario.pause();
		m_GameState = GameState.Ended;
		Intent goToNextActivity =
				new Intent(	context.getApplicationContext(),
							ExercisePickerActivity.class);

		context.startActivity(goToNextActivity);
		m_GameDrawer.get().destroy();
	}

	@Override
	public void switchToHome(final Context context)
	{
		m_Scenario.pause();
		m_GameState = GameState.Ended;

		Intent goToNextActivity =
				new Intent(	context.getApplicationContext(),
							MainActivity.class);
		goToNextActivity
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(goToNextActivity);
		m_GameDrawer.get().destroy();
	}

	@Override
	public void resumeExercise(Context context)
	{
		m_GameState = GameState.Running;
		m_GameDrawer.get().resumeGame();
		m_Scenario.resume();
	}

	@Override
	public void pauseExercise(Context context)
	{
		if (m_GameState != GameState.Ended)
		{
			m_Scenario.pause();
			m_GameState = GameState.Paused;
			m_GameDrawer.get().pauseGame();
		}
	}

	@Override
	public void startScenario(	String strStageId,
								String strExerciseOptionId,
								int nPlanId,
								IPlayingDisplayer displayer)
	{
		m_GameDrawer =
				new WeakReference<IPlayingDisplayer>(displayer);

		ExercisesManager manager =
				AppLibraryServiceProvider.getInstance().get(
						R.service.exercises_manager);

		if (nPlanId == ExercisePlan.PLAN_ID_INVALID)
		{
			ErrorHandler
					.HandleError(new IllegalStateException());
			return;
		}

		ExerciseStage stage = manager.getStage(strStageId);
		m_Plan = manager.getPracticeScheduler().getPlan(nPlanId);
		if (stage == null)
		{
			// TODO: Report error
			ErrorHandler.HandleError(new NullPointerException());
			return;
		}

		ExerciseStageOption exerciseOption =
				stage.getOption(strExerciseOptionId);

		if (exerciseOption == null)
		{
			ErrorHandler.HandleError(new NullPointerException());
			return;
		}

		m_Scenario = exerciseOption.getExerciseScenario();
		m_Scenario.playScenario(m_GameDrawer.get(), this);
	}

	@Override
	public void onScenarioDone(final ExerciseDoneReport report)
	{

		m_GameState = GameState.Ended;

		ExerciseStageOption stageOption =
				report.getStageOption();

		boolean fShowStars = true;
		ExercisesManager exercisesManager =
				AppLibraryServiceProvider.getInstance().get(
						R.service.exercises_manager);

		if (stageOption.getExerciseScenario().getScenarioType() == ExerciseTypes.eExerciseTypes_Tuning)
		{
			exercisesManager.getPracticeScheduler()
					.setWasTuningCompleted(true);
			fShowStars = false;
		}
		else
		{
			UserProfile activeProfile =
					exercisesManager.getActiveProfile();
			GeneralUserInformationFile userInformation =
					activeProfile.getUserInformation();
			// historyFile = activeProfile.getUserHistory().
			userInformation.addScore(stageOption.getId(), report
					.getInfo().Score);
			if (stageOption.getParent().getID().equals(
					"FirstTime"))
			{

				userInformation.FinishedIntroduction = true;
			}
			userInformation.saveState();
		}

		m_GameDrawer.get().endExercise(report, fShowStars);
	}

	@Override
	public GameState getGameState()
	{
		return m_GameState;
	}

}
