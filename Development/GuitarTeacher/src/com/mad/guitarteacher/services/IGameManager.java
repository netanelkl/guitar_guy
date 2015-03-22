package com.mad.guitarteacher.services;

import android.content.Context;

import com.mad.guitarteacher.activities.IPlayingDisplayer;

/**
 * The logical manager of the current game played and showed in the
 * IPlayingDisplayer.
 * 
 * @author Nati
 * 
 */
public interface IGameManager
{
	/**
	 * Holds the state of the current game.
	 */
	enum GameState
	{
		Running,
		Paused,
		Ended
	};

	/**
	 * The entry point to the game.
	 * 
	 * It should retrieve information about the current scenario to be played,
	 * activate the playing displayer accordingly.
	 * 
	 * @param strStageId
	 *            The stage to be played's id.
	 * @param strExerciseOptionId
	 *            The chosen optionId
	 * @param nPlanId
	 *            The plan Id. Note that either this or stage+option combination
	 *            should be supplied.
	 * @param displayer
	 *            The displayer to be run.
	 */
	public void startScenario(	String strStageId,
								String strExerciseOptionId,
								int nPlanId,
								IPlayingDisplayer displayer);

	/**
	 * Should initiate a transition to the next exercise. This should be called
	 * after the IPlayingDisplayer hid the current stage.
	 * 
	 * @param context
	 */
	public void nextExercise(Context context);

	/**
	 * Pauses the scenario and plays it from the start.
	 * 
	 * @param context
	 */
	public void restartExercise(Context context);

	/**
	 * Resumes a paused scenario. Should resume both the scenario and the
	 * displayer.
	 * 
	 * @param context
	 */
	public void resumeExercise(Context context);

	/**
	 * Pauses the scenario. Shold pause both the scenario and the displayer.
	 * 
	 * @param context
	 */
	public void pauseExercise(Context context);

	/**
	 * Ends the scenario and switches to the exercisePicker activity.
	 * 
	 * @param context
	 */
	public void switchToExercises(Context context);

	/**
	 * Ends the scenario and switches to the main screen.
	 * 
	 * @param context
	 */
	public void switchToHome(Context context);

	/**
	 * Retrieves the state of the play.
	 * 
	 * @return
	 */
	public GameState getGameState();

	/**
	 * handles logic of back button pressed.
	 * 
	 * @param context
	 */
	public void onBackPressed(Context context);

}
