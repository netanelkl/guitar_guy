package com.mad.guitarteacher.practice.Acts;

import com.mad.guitarteacher.activities.IPlayingDisplayer;
import com.mad.guitarteacher.practice.Scenarios.ExerciseScenario;
import com.mad.guitarteacher.services.TotalPointsManager;
import com.mad.lib.R;
import com.mad.lib.utils.AppLibraryServiceProvider;

public abstract class UserPlayActBase extends ActBase
{
	protected final ExerciseScenario	m_Parent;

	UserPlayActBase(ExerciseScenario parent)
	{
		m_Parent = parent;
	}

	@Override
	public int getDifficulty()
	{
		return 1;
	}

	protected void addTotalPoints(int nPoints)
	{
		TotalPointsManager pointsManager =
				AppLibraryServiceProvider.getInstance().get(
						R.service.total_points_manager);
		pointsManager.addPoints(nPoints);
		m_Displayer.updateTotalPoints(pointsManager
				.getTotalPoints());
	}

	@Override
	public void beginPlayingActImpl(IPlayingDisplayer displayer)
	{
		// Lets make sure this is the default mode.
		displayer.setFretVisibility(true);
	}

}
