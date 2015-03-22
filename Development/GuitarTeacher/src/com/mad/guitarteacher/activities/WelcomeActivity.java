package com.mad.guitarteacher.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mad.guitarteacher.R;
import com.mad.guitarteacher.connect.UserProfileConnectorBase;
import com.mad.guitarteacher.connect.UserProfileConnectorBase.OnSessionOpen;
import com.mad.guitarteacher.connect.UserProfilesManager;
import com.mad.lib.activities.WelcomeActivityBase;
import com.mad.lib.activities.listeners.OnFadeOutDoneListener;
import com.mad.lib.display.utils.AnimatorFactory;
import com.mad.lib.display.utils.DisplayHelper;
import com.mad.lib.utils.AppLibraryServiceProvider;

public class WelcomeActivity extends WelcomeActivityBase
{
	ImageView				m_btnFacebookLogin;
	ImageView				m_btnGoogleLogin;
	ImageView				m_btnContinue;
	protected ProgressBar	m_LoadingView;
	protected boolean		m_fIsLoadingRunning;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState, MainActivity.class);

		setListeners(savedInstanceState);
	}

	@Override
	protected void initViews()
	{
		super.initViews();
		m_btnContinue = (ImageView) findViewById(R.id.skip);
		m_btnGoogleLogin = (ImageView) findViewById(R.id.google);
		m_btnFacebookLogin =
				(ImageView) findViewById(R.id.facebook);
		m_LoadingView = (ProgressBar) findViewById(R.id.loading);
	}

	@Override
	public void onActivityResult(	int requestCode,
									int resultCode,
									Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		UserProfilesManager manager =
				AppLibraryServiceProvider.getInstance().get(
						R.service.user_profiles_manager);
		manager.onActivityResult(this, requestCode, resultCode,
				data);
	}

	/**
	 * Set listeneres foor all the buttons.
	 * 
	 * @param savedInstanceState
	 */
	protected void setListeners(final Bundle savedInstanceState)
	{
		m_btnContinue.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				v.setOnClickListener(null);
				UserProfilesManager manager =
						AppLibraryServiceProvider
								.getInstance()
								.get(R.service.user_profiles_manager);
				UserProfileConnectorBase connector =
						manager.getActiveSession(UserProfilesManager.INTERNAL_SESSION);
				connector.connect(WelcomeActivity.this);
			}
		});

		m_btnFacebookLogin
				.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						v.setOnClickListener(null);
						onFacebookLogin();
					}
				});
		m_btnGoogleLogin
				.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						v.setOnClickListener(null);
						onGoogleLogin();

						switchToLoading();

					}
				});
	}

	private void switchToLoading()
	{
		fadeOut(new OnFadeOutDoneListener()
		{

			@Override
			public void onFadeOutDone()
			{
				m_LoadingView.setVisibility(View.VISIBLE);
				m_fIsLoadingRunning = true;
			}
		});
	}

	/**
	 * When loging in to google.
	 */
	private void onGoogleLogin()
	{
		UserProfilesManager manager =
				AppLibraryServiceProvider.getInstance().get(
						R.service.user_profiles_manager);
		UserProfileConnectorBase googleSession =
				manager.getActiveSession(UserProfilesManager.GOOGLE_SESSON);
		m_ReturningState = STATE_WAITING_LOGIN;
		googleSession.connect(this);
	}

	@Override
	protected void scaleImageViews()
	{
		super.scaleImageViews();
		DisplayHelper.scaleImageView(m_btnContinue);
		DisplayHelper.scaleImageView(m_btnFacebookLogin);
		DisplayHelper.scaleImageView(m_btnGoogleLogin);
		DisplayHelper.scaleMargin(m_btnContinue);
	}

	@Override
	protected void animateScreen(	boolean fAway,
									AnimationListener currListener)
	{
		animateAllButtons(fAway, currListener);
	}

	@Override
	protected void enteringActivityCauseHandler_None()
	{
		if (!s_fIsManagerInitialized)
		{
			return;
		}
		UserProfilesManager manager =
				AppLibraryServiceProvider
						.getInstance()
						.get(com.mad.lib.R.service.user_profiles_manager);
		if (manager.attemptBackgroundConnecting(this,
				new OnSessionOpen()
				{

					@Override
					public void onSessionEstablished(UserProfileConnectorBase session)
					{
						// Only if the animation process
						// hasn't started yet, should we
						// change the buttons to be
						// faded in/out.
						if (!m_fIsFadeInStarted)
						{
							m_fDisplayConnectionOptions = false;
							goToMainActivity();

						}
						else
						{
							if (m_fIsLoadingRunning)
							{
								setProgressBarIndeterminateVisibility(false);
								goToMainActivity();
							}
							else
							{
								fadeOut(m_OnReadyToSwitchToMainActivity);
							}
						}

					}

					@Override
					public void onConnectionFailed(UserProfileConnectorBase session)
					{
						if (m_fIsLoadingRunning)
						{
							m_LoadingView
									.setVisibility(View.GONE);
							triggerButtonsAnimation();
						}
					}
				}))
		{
			// goToMainActivity();
		}
		else
		{
			triggerButtonsAnimation();
		}
	}

	@Override
	protected View createRootView(ViewGroup parent)
	{
		return LayoutInflater.from(this).inflate(
				R.layout.welcome_activity, null);
	}

	/**
	 * Function to run when login in to facebook.
	 */
	private void onFacebookLogin()
	{
		UserProfilesManager manager =
				AppLibraryServiceProvider.getInstance().get(
						R.service.user_profiles_manager);
		UserProfileConnectorBase connector =
				manager.getActiveSession(UserProfilesManager.FACEBOOK_SESSON);
		m_ReturningState = STATE_WAITING_LOGIN;
		connector.connect(this);
		switchToLoading();
	}

	private void animateAllButtons(	final boolean fAway,
									final AnimationListener listener)
	{
		final View[] arViews =
				{ m_btnContinue, m_btnGoogleLogin,
						m_btnFacebookLogin, m_logo };
		final int[] arDirections =
				{ AnimatorFactory.BOTTOM, AnimatorFactory.RIGHT,
						AnimatorFactory.LEFT,
						AnimatorFactory.ALPHA };

		animateElementsAppearance(fAway, listener, arViews,
				arDirections);
	}
}
