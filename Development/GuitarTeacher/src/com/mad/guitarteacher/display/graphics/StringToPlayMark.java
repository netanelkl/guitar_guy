package com.mad.guitarteacher.display.graphics;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.mad.guitarteacher.display.graphics.animation.FloatLinearAnimator;

/**
 * A drawable for the total points star thing in the right top of the screen.
 * 
 * @author Nati
 * 
 */
public class StringToPlayMark extends FloatLinearAnimator
		implements AnimatorUpdateListener
{
	/**
	 * The the rect to show
	 */
	private final Rect			m_Rect;

	private final Paint			m_Paint					=
																new Paint();

	private static final int	FADE_ANIMATION_DURATION	= 1000;

	@Override
	protected ValueAnimator createAnimationImpl()
	{
		ValueAnimator anim;
		if (m_DisplayState == DisplayState.Hiding)
		{
			m_sfEndingValue = m_sfFloatLinearValue;
			m_sfStartValue = 0;
			anim = super.createAnimationImpl();
			anim.addUpdateListener(this);
		}
		else
		{
			anim = super.createAnimationImpl();
			anim.setRepeatCount(ValueAnimator.INFINITE);
			anim.setRepeatMode(ValueAnimator.REVERSE);
			anim.addUpdateListener(this);
		}
		return anim;
	}

	@Override
	public void onAnimationUpdate(ValueAnimator animation)
	{
		m_Paint.setAlpha((int) (m_sfFloatLinearValue * 256));
	}

	/**
	 * Creates the TotalPointsDrawer (star and value indicator) at the given
	 * location.
	 * 
	 * @param context
	 *            The context.
	 * @param nLeft
	 *            The distance from the left of the screen.
	 * @param nTop
	 *            The distance from the top of the screen.
	 * @param nRight
	 *            The distance from the right of the screen.
	 * @param nBottom
	 *            The distance from the bottom of the screen.
	 */
	public StringToPlayMark(final Context context,
							int nLeft,
							int nRight,
							int nTop,
							int nStringHeight)
	{
		super(0.2f, 0.6f, FADE_ANIMATION_DURATION);

		// Create the rect.
		m_Rect =
				new Rect(	nLeft,
							nTop - (nStringHeight / 2),
							nRight,
							nTop + (nStringHeight / 2));
		m_Paint.setColor(Color.YELLOW);
		m_fInvalidate = true;
	}

	public void setColor(int color)
	{
		m_Paint.setColor(color);
	}

	@Override
	public void draw(final Canvas canvas)
	{
		if (m_DisplayState != DisplayState.Hidden)
		{
			canvas.drawRect(m_Rect, m_Paint);
		}
	}

	@Override
	public void hide()
	{
		hide(false);
	}

	public void hide(boolean fViolent)
	{
		m_anim.cancel();
		m_anim = null;

		super.hide();
	}
}
