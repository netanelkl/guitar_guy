package com.mad.guitarteacher.display.graphics.animation;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

/**
 * A simple animated drawable with a need for a linear advancing number (float).
 * 
 * This can be used in many scenarios like rotating, shifting or scaling where
 * the animation is pretty similar, but the drawing based on the value is
 * different.
 * 
 * @author Nati
 * 
 */
public abstract class FloatLinearAnimator extends
		AnimatedDrawable
{
	/**
	 * The animation duration.
	 */
	private final int	m_nAnimationDuration;

	/**
	 * The current value of the animator.
	 */
	protected float		m_sfFloatLinearValue	= 0;

	/**
	 * The min and max of the animator value.
	 */
	protected float		m_sfStartValue, m_sfEndingValue;

	/**
	 * Creates a new FloatLinearAnimator.
	 * 
	 * @param sfMinValue
	 *            The min value of the animator value.
	 * @param sfMaxValue
	 *            The max value of the animator value.
	 * @param nAnimationDuration
	 *            The duration of the animation.
	 */
	public FloatLinearAnimator(	final float sfMinValue,
								final float sfMaxValue,
								final int nAnimationDuration)
	{
		m_sfStartValue = sfMinValue;
		m_sfEndingValue = sfMaxValue;
		m_nAnimationDuration = nAnimationDuration;
	}

	@Override
	protected ValueAnimator createAnimationImpl()
	{
		// TODO Auto-generated
		ValueAnimator animator = null;

		animator =
				ObjectAnimator.ofFloat(this, "FloatLinearValue",
						m_sfStartValue, m_sfEndingValue)
						.setDuration(m_nAnimationDuration);
		animator.setInterpolator(new LinearInterpolator());

		return animator;
	}

	@SuppressWarnings("unused")
	public void setFloatLinearValue(final float sfValue)
	{
		m_sfFloatLinearValue = sfValue;
	}
};
