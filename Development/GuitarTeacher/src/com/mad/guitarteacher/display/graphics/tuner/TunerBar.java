package com.mad.guitarteacher.display.graphics.tuner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.mad.guitarteacher.R;
import com.mad.guitarteacher.display.graphics.DisplayState;
import com.mad.lib.display.graphics.GraphicsPoint;
import com.mad.guitarteacher.display.graphics.animation.FloatLinearAnimator;
import com.mad.lib.display.utils.DisplayHelper;

/**
 * This class represents the tuner bar in the tuner gauge.
 * 
 * @author Tom
 * 
 */
public class TunerBar extends FloatLinearAnimator
{
	Bitmap						m_bitmapBar;
	private static final int	ANIMATION_DURATION	= 300;

	/*
	 * The center of the gauge. The bar will move MOVING_SPACE from it in either
	 * direction.
	 */
	GraphicsPoint				m_pntCenterPosition	=
															new GraphicsPoint();
	private float				m_sfMovingSpace;

	/**
	 * Create a new instance of the tuner bar.
	 * 
	 * @param sfMinValue
	 *            - Min value of the bar.
	 * @param sfMaxValue
	 *            - Max value of the bar.
	 * @param nAnimationDuration
	 *            - Duration of the animation.
	 * @param context
	 *            - The current context.
	 */
	public TunerBar(Context context,
					float sfMiddleGaugeX,
					float sfMiddleGaugeY)
	{
		super(-1, 1, ANIMATION_DURATION);

		// keep the center position.

		// Load the image.
		Bitmap bmpBar =
				DisplayHelper.getScaledBitmap(
						context.getResources(),
						R.drawable.game_gauge_bar, false);
		m_pntCenterPosition.set(
				sfMiddleGaugeX - (bmpBar.getWidth() / 2),
				sfMiddleGaugeY - (bmpBar.getHeight() / 2));
		m_bitmapBar = bmpBar;

	}

	public void setMovingSpace(float sfMovingSpace)
	{
		m_sfMovingSpace = sfMovingSpace;
	}

	public void shiftPosition(float sfNewPosition)
	{
		m_sfStartValue = m_sfFloatLinearValue;
		m_sfEndingValue = sfNewPosition;
		// Create new point animation.
		if (m_anim != null)
		{
			m_anim.cancel();
			m_anim = null;
		}
		show();
	}

	@Override
	public void draw(Canvas canvas)
	{
		if (m_DisplayState != DisplayState.Hidden)
		{
			canvas.drawBitmap(m_bitmapBar,

			m_pntCenterPosition.x
					+ (m_sfFloatLinearValue * m_sfMovingSpace),
					m_pntCenterPosition.y, null);
		}
	}
}
