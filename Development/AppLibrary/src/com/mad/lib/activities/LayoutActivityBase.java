package com.mad.lib.activities;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.mad.lib.R;
import com.mad.lib.activities.listeners.OnFadeOutDoneListener;

/**
 * This is the base activity for all of our activities.
 * 
 * It will make sure that all of our services are initialized.
 * 
 * @author Nati
 * 
 */
public abstract class LayoutActivityBase extends ActivityBase
{
	/**
	 * The view holding the xml root. This is used for the fading in/out.
	 */
	private ViewGroup			m_viewForeground;

	private static final int	DURATION_FADE_IN	= 200;

	@Override
	protected abstract View createRootView(final ViewGroup parent);

	/**
	 * This method should be used to fade out the gui in preparation for
	 * activity transition.
	 */
	protected void fadeOut(final OnFadeOutDoneListener listener)
	{
		AnimationListener animationListener =
				new AnimationListener()
				{

					@Override
					public void onAnimationEnd(final Animation animation)
					{
						if (listener != null)
						{
							listener.onFadeOutDone();
						}
					}

					@Override
					public void onAnimationRepeat(final Animation animation)
					{

					}

					@Override
					public void onAnimationStart(final Animation animation)
					{

					}
				};

		fadeOut(animationListener);
	}

	/**
	 * This method should be used to fade out the gui in preparation for
	 * activity transition.
	 */
	protected void fadeOut(AnimationListener fadeOutAnimator)
	{
		final Animation anim =
				AnimationUtils.loadAnimation(this,
						android.R.anim.fade_out);
		anim.setDuration(DURATION_FADE_IN);
		anim.setFillAfter(true);
		anim.setAnimationListener(fadeOutAnimator);
		m_viewForeground.startAnimation(anim);
	}

	// protected void addView(View v)
	// {
	// m_viewForeground.addView(v);
	// }
	protected ViewGroup getRootView()
	{
		return m_viewForeground;
	}

	// protected void addView(View v, ViewGroup.LayoutParams params)
	// {
	// m_viewForeground.addView(v, params);
	// }
	@Override
	public void onBackPressed()
	{
		fadeOut(new OnFadeOutDoneListener()
		{

			@Override
			public void onFadeOutDone()
			{
				LayoutActivityBase.super.onBackPressed();
			}
		});
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (!s_fIsManagerInitialized)
		{
			return;
		}
		setContentView(R.layout.activity_header);

		ViewGroup root =
				(ViewGroup) findViewById(android.R.id.content);

		root.setBackground(s_imgBackground);

		m_viewForeground =
				(ViewGroup) findViewById(R.id.foreground);
		m_viewForeground
				.addView(createRootView(m_viewForeground));
	}

	/*******************************************************
	 * HIDING_SCREEN_MEMBERS
	 ********************************************************/

	@Override
	protected void onResume()
	{
		super.onResume();
		if (!s_fIsManagerInitialized)
		{
			return;
		}

		Animation anim =
				AnimationUtils.loadAnimation(this,
						android.R.anim.fade_in);
		anim.setDuration(DURATION_FADE_IN);
		anim.setFillAfter(true);
		m_viewForeground.startAnimation(anim);
		// We want to fade in stuff only if we have what to fade in.
		// if (m_arViewsToFade.size() == 0)
		// {
		// return;
		// }

		// Disappear all views.
		// for (View currentView : m_arViewsToFade)
		// {
		// currentView.startAnimation(anim);
		// }

	}
}
