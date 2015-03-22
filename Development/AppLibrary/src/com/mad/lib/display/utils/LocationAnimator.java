package com.mad.lib.display.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

public class LocationAnimator
{
	private final ValueAnimator	m_animator;

	public LocationAnimator(final View viewToAnimate,
							final float fromY,
							final float toY)
	{
		// Create new point animation.
		m_animator =
				ObjectAnimator.ofFloat(viewToAnimate,
						"TranslationY", fromY, toY);

		// // Defaults duration to 300.
		m_animator.setDuration(300);

	}

	public void setDuration(final long milisecs)
	{
		m_animator.setDuration(milisecs);
	}

	public void start()
	{
		m_animator.start();
	}
}
