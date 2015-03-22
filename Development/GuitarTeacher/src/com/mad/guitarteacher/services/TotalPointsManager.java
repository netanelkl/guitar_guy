package com.mad.guitarteacher.services;

import com.mad.guitarteacher.dataContract.GeneralUserInformationFile;
import com.mad.guitarteacher.practice.ExercisesManager;
import com.mad.lib.R;
import com.mad.lib.utils.AppLibraryServiceProvider;

/**
 * The total points manager manages all the logic that has to do with user
 * points.
 * 
 * @author Tom
 * 
 */
public class TotalPointsManager
{
	/**
	 * Called on end of act.
	 * 
	 * @param score
	 *            - Score of the act.
	 * @param difficulty
	 *            - Difficulty of the act.
	 */
	public void addPoints(int nPoints)
	{
		ExercisesManager exercisesManager =
				AppLibraryServiceProvider.getInstance().get(
						R.service.exercises_manager);
		GeneralUserInformationFile userInformation =
				exercisesManager.getActiveProfile()
						.getUserInformation();

		userInformation.TotalPoints += nPoints;
		userInformation.saveState();
	}

	/**
	 * Called on end of exercise.
	 * 
	 * @param score
	 *            - Score of the exercise.
	 * @param difficulty
	 *            - Difficulty of the exercise.
	 */
	public void exerciseEnded(int score, int difficulty)
	{
		addPoints(9);
	}

	/**
	 * Get the total points of the current game.
	 * 
	 * @return
	 */
	public int getTotalPoints()
	{

		ExercisesManager exercisesManager =
				AppLibraryServiceProvider.getInstance().get(
						R.service.exercises_manager);
		return exercisesManager.getActiveProfile()
				.getUserInformation().TotalPoints;
	}
}
