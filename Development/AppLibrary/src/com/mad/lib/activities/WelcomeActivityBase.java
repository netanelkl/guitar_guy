package com.mad.lib.activities;

import java.security.InvalidParameterException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.mad.lib.R;
import com.mad.lib.activities.listeners.OnFadeOutDoneListener;
import com.mad.lib.app.ApplicationBase;
import com.mad.lib.display.utils.AnimatorFactory;
import com.mad.lib.display.utils.DisplayHelper;
import com.mad.lib.utils.ErrorHandler;
import com.mad.lib.utils.bitmaps.BitmapsStore;

public class WelcomeActivityBase extends LayoutActivityBase
{
	private static final String	TAG							=
																	"PROFILING";
	private static final String	STATE_IS_WAITING_LOGIN		=
																	"mad.views.isWaitingLogin";
	private static final int	OVERSHOOT_ANIMATION_LENGTH	=
																	500;
	protected static final int	STATE_WAITING_LOGIN			= 0;
	private static final int	STATE_DONE					= 1;
	private static final int	STATE_NONE					= 2;

	protected ImageView			m_logo;
	private Intent				m_nextIntent;
	public boolean				m_fDisplayConnectionOptions	=
																	true;
	public boolean				m_fIsFadeInStarted			=
																	false;

	protected int				m_ReturningState			=
																	STATE_NONE;

	/**
	 * 
	 */
	private void enteringActivityCauseHandler()
	{
		int nCurrentState = m_ReturningState;
		switch (nCurrentState)
		{
			case STATE_WAITING_LOGIN:
			{
				break;
			}
			case STATE_DONE:
			{
				finish();
				break;
			}
			case STATE_NONE:
			{
				enteringActivityCauseHandler_None();
				break;
			}
			default:
			{
				ErrorHandler
						.HandleError(new InvalidParameterException());
			}
		}
	}

	protected void enteringActivityCauseHandler_None()
	{
		triggerButtonsAnimation();
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		enteringActivityCauseHandler();
	}

	/**
	 * Change to the main activity.
	 */
	protected void goToMainActivity()
	{
		m_ReturningState = STATE_DONE;
		Log.d(TAG, "Before startActvitiy"
				+ Long.toString(System.nanoTime()));
		WelcomeActivityBase.this.startActivity(m_nextIntent);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_IS_WAITING_LOGIN, m_ReturningState);
	}

	@Override
	protected void fadeOut(final OnFadeOutDoneListener onFadeOutDoneListener)
	{
		AnimationListener currListener = new AnimationListener()
		{

			@Override
			public void onAnimationStart(Animation animation)
			{
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				onFadeOutDoneListener.onFadeOutDone();
			}
		};
		animateScreen(true, currListener);
	}

	protected void animateScreen(	boolean fAway,
									AnimationListener currListener)
	{
		animateLogo(fAway, currListener);
	}

	/**
	 * Set all the common animation paratmers for the welcom screen animation.
	 * 
	 * @param animation
	 */
	protected void setWelcomeScreenAnimationProperties(Animation animation)
	{
		animation.setDuration(OVERSHOOT_ANIMATION_LENGTH);
		animation.setFillAfter(true);
		animation
				.setInterpolator(new AccelerateDecelerateInterpolator());
	}

	protected void onCreate(final Bundle savedInstanceState,
							Class<? extends ActivityBase> moveToClass)
	{
		super.onCreate(savedInstanceState);
		if (!s_fIsManagerInitialized)
		{
			return;
		}
		m_fDisplayConnectionOptions =
				((ApplicationBase) getApplication())
						.isSupportingLogin();

		m_nextIntent =
				new Intent(WelcomeActivityBase.this, moveToClass);

		initViews();

		// Scale all image views.
		scaleImageViews();

		BitmapsStore store = BitmapsStore.ofType(moveToClass);
		store.preloadBitmaps(getResources());
	}

	protected void initViews()
	{

		m_logo =
				(ImageView) findViewById(R.id.welcome_activity_logo);
	}

	private class OnLogoShownListener implements
			AnimationListener
	{

		private static final int	TIME_LOGO_ON_SCREEN	= 1000;

		@Override
		public void onAnimationStart(Animation animation)
		{

		}

		@Override
		public void onAnimationEnd(Animation animation)
		{
			Handler handler = new Handler();
			handler.postDelayed(new Runnable()
			{

				@Override
				public void run()
				{
					fadeOut(m_OnReadyToSwitchToMainActivity);
				}
			}, TIME_LOGO_ON_SCREEN);
		}

		@Override
		public void onAnimationRepeat(Animation animation)
		{

		}

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);

		if (!s_fIsManagerInitialized)
		{
			return;
		}

		// triggerButtonsAnimation();
	}

	/**
	 * 
	 */
	public void triggerButtonsAnimation()
	{
		m_fIsFadeInStarted = true;
		findViewById(android.R.id.content).getViewTreeObserver()
				.addOnGlobalLayoutListener(
						new OnGlobalLayoutListener()
						{

							@Override
							public void onGlobalLayout()
							{
								findViewById(
										android.R.id.content)
										.getViewTreeObserver()
										.removeOnGlobalLayoutListener(
												this);

								AnimationListener listener =
										null;
								if (!m_fDisplayConnectionOptions)
								{
									listener =
											new OnLogoShownListener();
								}
								animateScreen(false, listener);
							}
						});
	}

	private void animateLogo(	final boolean fAway,
								final AnimationListener listener)
	{
		final View[] arViews = { m_logo };
		final int[] arDirections = { AnimatorFactory.ALPHA };

		animateElementsAppearance(fAway, listener, arViews,
				arDirections);
	}

	protected void animateElementsAppearance(	final boolean fAway,
												final AnimationListener listener,
												final View[] arViews,
												final int[] arDirections)
	{
		for (int i = 0; i < arViews.length; i++)
		{
			final int nI = i;
			final View viewCurrent = arViews[i];
			final int nDirection = arDirections[i];
			viewCurrent.setVisibility(View.VISIBLE);
			Animation anim =
					AnimatorFactory.createAnimation(viewCurrent,
							fAway, nDirection);

			setWelcomeScreenAnimationProperties(anim);

			viewCurrent.setAnimation(anim);

			// This is because the animationset is not
			// working for some reason
			// and Im too lazy to fix it now.
			if (nI == 0)
			{
				anim.setAnimationListener(listener);
			}
			// animSet.addAnimation(anim);
			viewCurrent.startAnimation(anim);
		}
	}

	/**
	 * Scale all image views.
	 */
	protected void scaleImageViews()
	{
		DisplayHelper.scaleImageView(m_logo);
		DisplayHelper.scaleMargin(m_logo);
	}

	@Override
	protected View createRootView(ViewGroup parent)
	{
		return LayoutInflater.from(this).inflate(
				R.layout.welcome_activity_base, null);
	}

	public final OnFadeOutDoneListener	m_OnReadyToSwitchToMainActivity	=
																				new OnFadeOutDoneListener()
																				{

																					@Override
																					public void onFadeOutDone()
																					{
																						goToMainActivity();
																					}
																				};
}
