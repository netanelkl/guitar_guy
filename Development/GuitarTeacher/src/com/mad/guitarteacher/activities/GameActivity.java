package com.mad.guitarteacher.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.mad.guitarteacher.display.views.GameDrawer;
import com.mad.guitarteacher.display.views.GameSurfaceView;
import com.mad.guitarteacher.practice.ExercisePlan;
import com.mad.guitarteacher.services.GameManager;
import com.mad.guitarteacher.services.IGameManager;
import com.mad.guitarteacher.utils.Definitions;
import com.mad.lib.activities.ActivityBase;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class GameActivity extends ActivityBase

{

	/*******************************************************
	 * VIEWS
	 ********************************************************/

	private GameDrawer		m_GameDrawer;
	private GameSurfaceView	m_GameView;
	private IGameManager	m_GameManager;

	/*******************************************************
	 * LOGIC_MEMBERS
	 ********************************************************/

	@Override
	protected void onPause()
	{
		super.onPause();
		m_GameManager.pauseExercise(this);

		// m_GameDrawer = null;
		// m_GameView = null;
	}

	@Override
	protected void onStop()
	{
		super.onStop();
	}

	@Override
	public void onBackPressed()
	{
		m_GameManager.onBackPressed(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if (m_GameView == null)
		{
			m_GameView = new GameSurfaceView(this);
			m_GameDrawer = new GameDrawer(this);
			m_GameView.setDrawingManager(m_GameDrawer);
			m_GameDrawer.initialize(this, m_GameView,
					m_GameManager);
			m_GameView.setBackground(s_imgBackground);
			setContentView(m_GameView);
			Intent intent = getIntent();

			startScenario(intent);
		}
		else
		{
			m_GameManager.resumeExercise(this);
		}
	}

	private void startScenario(Intent intent)
	{
		String strStageId =
				intent.getStringExtra(Definitions.Intents.INTENT_EXERCISE_ID);
		String strExerciseOptionId =
				intent.getStringExtra(Definitions.Intents.INTENT_OPTION_ID);
		int nPlanId =
				intent.getIntExtra(
						Definitions.Intents.INTENT_PLAN_ID,
						ExercisePlan.PLAN_ID_INVALID);

		// This builds the plan and stage.
		// TODO: Here should'nt we just give the Tuning Scenario
		// a 'TuningDrawer' instead of adding functions to the
		// basic drawer?ë
		m_GameManager.startScenario(strStageId,
				strExerciseOptionId, nPlanId, m_GameDrawer);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (!s_fIsManagerInitialized)
		{
			return;
		}
		m_GameManager = new GameManager();
	}

	@Override
	protected View createRootView(final ViewGroup parent)
	{
		parent.setKeepScreenOn(true);
		// m_GameDrawer = new BasicGameDrawer(this);
		// m_GameView = new GameSurfaceView(this, m_GameDrawer);
		// m_LastIntent = getIntent();
		// startScenario(m_LastIntent);
		return m_GameView;
	}

	/*******************************************************
	 * HIDING_SCREEN_MEMBERS
	 ********************************************************/

	@Override
	public void onConfigurationChanged(final Configuration newConfig)
	{
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if (m_GameDrawer != null)
		{
			m_GameDrawer.onConfigurationChanged(this, newConfig);
		}
	}

	@Override
	protected void onNewIntent(final Intent intent)
	{
		super.onNewIntent(intent);
		startScenario(intent);
	}

}
