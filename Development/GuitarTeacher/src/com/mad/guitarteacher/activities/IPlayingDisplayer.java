package com.mad.guitarteacher.activities;

import com.mad.guitarteacher.practice.ExerciseDoneReport;
import com.mad.lib.display.pager.OnDisplayInformationDone;
import com.mad.lib.display.pager.PagerPageCollection;
import com.mad.lib.musicalBase.IHandPositioning;

/**
 * An interface, provided by an activity, that allows displaying a single
 * handPositioning to be played.
 * 
 * @author Nati
 * 
 */
public interface IPlayingDisplayer
{
	/**
	 * Display an information snippet.
	 * 
	 * @param title
	 *            The title of the message <i>for Example: The G Chord</i>
	 * @param message
	 *            The message to be displayed. Currently displayed on one
	 *            screen. Consider changing.
	 * @param onDoneListener
	 *            The listener to be called when the user is done viewing the
	 *            information.
	 */
	void displayInformation(PagerPageCollection stageInfo,
							OnDisplayInformationDone onDoneListener);

	void destroy();

	/**
	 * Update the total points in the game.
	 * 
	 * @param totalPoints
	 *            - The total points value.
	 */
	public void updateTotalPoints(int totalPoints);

	/**
	 * Display a handPositioning position.
	 * 
	 * This should be could by an Act, possibly expecting the user to play it.
	 * This is act is considered immediate, and the caller shouldn't wait for
	 * anything to be done.
	 * 
	 * @param handPositioningToHold
	 *            The handPositioning to display for the user to hold.
	 * @param handPositioningToPlay
	 *            The handPositioning to display for the user to play.
	 */
	void displayNotes(	IHandPositioning handPositioningToHold,
						IHandPositioning handPositioningToPlay);

	/**
	 * Notifies the user that the exercise has ended and prepare for activity
	 * exit.
	 */
	public void endExercise(ExerciseDoneReport report,
							boolean fShowStars);

	/**
	 * Sets the current act state.
	 * 
	 * Sets the current task to be done.
	 * 
	 * @param strText
	 *            The string to display.
	 */
	public void setCurrentActState(String strText);

	/**
	 * Sets the current scenario name.
	 * 
	 * Sets the current scenario name to be displayed wherever.
	 * 
	 * @param strText
	 *            The string to display.
	 */
	public void setScenarioName(String strText);

	/**
	 * Sets whether or not to show frets.
	 * 
	 * @param fShowFrets
	 *            True if the frets should be displayed. False otherwise.
	 */
	public void setFretVisibility(boolean fShowFrets);

	/**
	 * Sets whether or not to display a waiting for play display.
	 * 
	 * Display some sort of a playing icon/notification to the user that we are
	 * waiting him to play.
	 * 
	 * @param fEnable
	 *            true if we should display the info, false otherwise.
	 */
	public void setWaitingPlay(boolean fEnable);

	/**
	 * Notify the user that the user played the wrong stuff.
	 */
	void wrongNotePlayed(IHandPositioning playedFingering);

	/**
	 * Resumes the current game.
	 */
	void resumeGame();

	/**
	 * Pauses the current game and brings up the pause screen.
	 */
	void pauseGame();

	/**
	 * Notifies the user that the notes has played correctly.
	 */
	void setNotesPlayedCorrectly();

	/**
	 * Resets the screen to the state where no fret or string is shown.
	 */
	void resetScreen();

	/**
	 * Display the tuner gauge.
	 * 
	 * @param fShow
	 *            - If true show the tuner gauge, if false hide.
	 * @param sfBarValue
	 *            - The value to display on the bar.
	 */
	public void displayTunerGauge(	final boolean fShow,
									final float sfBarValue);
}
