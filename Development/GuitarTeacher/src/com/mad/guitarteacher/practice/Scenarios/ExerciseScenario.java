package com.mad.guitarteacher.practice.Scenarios;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;

import com.mad.guitarteacher.activities.IPlayingDisplayer;
import com.mad.guitarteacher.dataContract.ExerciseDoneReportInfo;
import com.mad.guitarteacher.practice.ExerciseDoneReport;
import com.mad.guitarteacher.practice.ExerciseStageOption;
import com.mad.guitarteacher.practice.ExerciseTypes;
import com.mad.guitarteacher.practice.Acts.ActBase;
import com.mad.guitarteacher.practice.Acts.OnActDoneListener;
import com.mad.guitarteacher.practice.Acts.UserPlayActBase;
import com.mad.guitarteacher.services.TotalPointsManager;
import com.mad.lib.R;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.lib.utils.TimeSpan;

/**
 * A scenario representing a stage's acts.
 * 
 * This is the base class class for all ExerciseScenarios. NOTE: All subclasses
 * should implement a static ctor and register their types to the factory.
 * 
 * @author Nati
 * 
 */
public abstract class ExerciseScenario implements
		OnActDoneListener
{
	public ExerciseScenario(ExerciseTypes eScenarioType)
	{
		m_eScenarioType = eScenarioType;
	}

	private final ExerciseTypes			m_eScenarioType;

	/**
	 * The list of acts composing the scenario.
	 */
	private final ArrayList<ActBase>	m_arScript					=
																			new ArrayList<ActBase>();
	WeakReference<IPlayingDisplayer>	m_Displayer					=
																			null;
	/**
	 * Contains information of whether or not the current act is running.
	 */
	private boolean						m_fIsCurrentlyPlaying;

	private long						m_lStartTime;

	/**
	 * The current act to be played / that's being played.
	 */
	private int							m_nCurrentAct;

	private OnScenarioDoneListener		m_OnExerciseDoneListener	=
																			null;

	private ExerciseStageOption			m_Parent;

	private boolean						m_fIsInSimulationMode		=
																			false;

	private int							m_nSimulationInitatorAct;

	/**
	 * Pass to the next act. This should be called by the OnActDoneListener.
	 * 
	 * TODO: Consider changing this to be called by the act itself. Makes more
	 * sense.
	 */
	public void actDone()
	{
		m_nCurrentAct++;
	}

	/**
	 * Adds an act.
	 * 
	 * The act should probably be added on the ctor of the subclass.
	 * 
	 * @param ActBase
	 *            actToAdd - An act to add to the scenario.
	 * 
	 * @return boolean - true if succeeded.
	 */
	protected void addAct(final ActBase actToAdd)
	{
		m_arScript.add(actToAdd);

	}

	/**
	 * Tells the current act to pause.
	 */
	public void pause()
	{
		if (m_fIsCurrentlyPlaying)
		{

			ActBase actCurrent = m_arScript.get(m_nCurrentAct);
			actCurrent.pause();
			m_fIsCurrentlyPlaying = false;
		}
	}

	/**
	 * Returns the current act of the scenario.
	 * 
	 * @return Act - The current act of the scenario.
	 */
	public ActBase getCurrentAct()
	{
		if (m_nCurrentAct >= m_arScript.size())
		{
			return null;
		}

		return m_arScript.get(m_nCurrentAct);
	}

	public int getCurrentActIndex()
	{
		return m_nCurrentAct;
	}

	public ExerciseDoneReport getExerciseReport()
	{
		int nTotalScore = 0;
		int nTotalExercises = 0;
		for (ActBase act : m_arScript)
		{
			int nScore = act.getScore();
			if (nScore != ActBase.NO_SCORE)
			{
				nTotalScore += nScore;
				nTotalExercises++;
			}
		}
		if (nTotalExercises > 0)
		{
			nTotalScore /= nTotalExercises;
		}

		return new ExerciseDoneReport(	m_Parent,
										new ExerciseDoneReportInfo(	m_Parent.getParent()
																			.getID(),
																	nTotalScore,
																	new TimeSpan(	m_lStartTime,
																					new Date()
																							.getTime())));
	}

	/**
	 * Initializes the scenario. The base point for initializing the object.
	 * 
	 * TODO: Possibly check here if the object's class is registered in the
	 * stageFactory.
	 * 
	 * @param Parameters
	 *            The parameters used to create the unique scenario.
	 */
	public boolean init(final String[] arParams,
						final ExerciseStageOption parent)
	{
		m_Parent = parent;
		return true;
	}

	@Override
	public void onActDone(	final ActBase act,
							boolean fSimulationInitiator)
	{
		if (fSimulationInitiator)
		{
			m_fIsInSimulationMode = true;
			m_nSimulationInitatorAct = m_nCurrentAct;
		}
		m_nCurrentAct++;
		ActBase newAct = getCurrentAct();

		if (m_fIsInSimulationMode)
		{
			onSimulationActDone(act, newAct);
		}
		else
		{
			onNonSimulationActDone(act, newAct);
		}
	}

	private void onNonSimulationActDone(final ActBase actDone,
										ActBase newAct)
	{
		// Update at the end of the exercise
		TotalPointsManager pointsManager =
				AppLibraryServiceProvider.getInstance().get(
						R.service.total_points_manager);

		pointsManager.addPoints(actDone.getScore()
				* actDone.getDifficulty());

		if (newAct == null)
		{
			m_fIsCurrentlyPlaying = false;
			ExerciseDoneReport report = getExerciseReport();
			// Update at the end of the exercise
			pointsManager.exerciseEnded(report.getInfo().Score,
					1);
			m_OnExerciseDoneListener.onScenarioDone(report);
		}
		else
		{
			newAct.beginPlayingAct(m_Displayer.get(), this);
		}
		int totalPoints = pointsManager.getTotalPoints();

		m_Displayer.get().updateTotalPoints(totalPoints);
	}

	private void onSimulationActDone(	final ActBase actDone,
										ActBase newAct)
	{
		// If no more acts, or the new acts, not play acts, go back to the
		// initiating act.
		if ((newAct == null)
				|| !(newAct instanceof UserPlayActBase))
		{
			m_fIsCurrentlyPlaying = false;
			m_fIsInSimulationMode = false;
			m_nCurrentAct = m_nSimulationInitatorAct;
			ActBase actSimulatorInitiator = getCurrentAct();
			actSimulatorInitiator.beginPlayingAct(m_Displayer
					.get(), this);
		}
		else
		{
			newAct.beginPlayingAct(m_Displayer.get(), this);
		}
	}

	public void playScenario(	final IPlayingDisplayer displayer,
								final OnScenarioDoneListener listener)
	{
		if (displayer != null)
		{
			m_Displayer =
					new WeakReference<IPlayingDisplayer>(displayer);
		}
		else
		{
			m_Displayer = null;
		}

		reset();
		ActBase currentAct = getCurrentAct();
		TotalPointsManager pointsManager =
				AppLibraryServiceProvider.getInstance().get(
						R.service.total_points_manager);
		m_Displayer.get().updateTotalPoints(
				pointsManager.getTotalPoints());
		m_Displayer.get().setScenarioName(m_Parent.getName());
		if (currentAct != null)
		{
			m_fIsCurrentlyPlaying = true;
			m_OnExerciseDoneListener = listener;
			currentAct.beginPlayingAct(displayer, this);
		}
		else
		{
			ExerciseDoneReport report = getExerciseReport();
			listener.onScenarioDone(report);
		}
	}

	/**
	 * Resets the exercise scenario.
	 */
	public void reset()
	{
		// First, cancel the running act, and then reset the current running act
		// to be in
		if (m_fIsCurrentlyPlaying)
		{

			m_arScript.get(m_nCurrentAct).cancelPlaying();
		}

		m_fIsCurrentlyPlaying = false;
		m_fIsInSimulationMode = false;
		m_nSimulationInitatorAct = -1;
		m_nCurrentAct = 0;
	}

	public void setParent(final ExerciseStageOption stage)
	{
		m_Parent = stage;
	}

	/**
	 * Resumes the current scenario.
	 */
	public void resume()
	{
		if (!m_fIsCurrentlyPlaying)
		{
			ActBase actCurrent = m_arScript.get(m_nCurrentAct);
			actCurrent.resume();
			m_fIsCurrentlyPlaying = true;
		}
	}

	public ExerciseTypes getScenarioType()
	{
		return m_eScenarioType;
	}

	/**
	 * @return the m_fIsInSimulationMode
	 */
	public boolean isInSimulationMode()
	{
		return m_fIsInSimulationMode;
	}

}
