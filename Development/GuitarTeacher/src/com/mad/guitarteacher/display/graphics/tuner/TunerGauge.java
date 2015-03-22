package com.mad.guitarteacher.display.graphics.tuner;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.mad.guitarteacher.R;
import com.mad.lib.display.graphics.GraphicsPoint;
import com.mad.guitarteacher.display.graphics.animation.AnimatedBitmapDrawable;
import com.mad.guitarteacher.display.graphics.animation.AnimatedDrawableCollection;
import com.mad.guitarteacher.display.graphics.animation.OnHiddenListener;
import com.mad.guitarteacher.display.graphics.animation.OnShownListener;
import com.mad.lib.display.utils.DisplayHelper;

/**
 * A class represents a tuner gauge for the tuner act.
 * 
 * @author Tom
 * 
 */
public class TunerGauge extends AnimatedDrawableCollection
{
	TunerBar				m_tunerBar;
	AnimatedBitmapDrawable	m_BG;
	private final int		PADDING_BG	= 36;

	/**
	 * Create a new instance of the TunerGauge object.
	 * 
	 * @param context
	 *            - The current context.
	 */
	public TunerGauge(Context context, final PointF pntPosition)
	{

		final Bitmap bmpBG =
				DisplayHelper.getScaledBitmap(context
						.getResources(),
						R.drawable.game_gauge_bg);
		add(m_BG =
				new AnimatedBitmapDrawable(	bmpBG,
											pntPosition,
											true)
				{
					@Override
					protected ValueAnimator createAnimationImpl()
					{
						m_rectBitmap.set((int) pntPosition.x,
								(int) pntPosition.y,
								(int) (pntPosition.x + bmpBG
										.getWidth()),
								(int) (pntPosition.y + bmpBG
										.getHeight()));
						// disable the animation.
						return null;
						// return super.createAnimationImpl();
					}
				});

		// The tuner bar will be positioned at the center.
		m_tunerBar =
				new TunerBar(context, pntPosition.x
						+ (bmpBG.getWidth() / 2), pntPosition.y
						+ (bmpBG.getHeight() / 2));

		// Define the width we're allowed to move from the center.
		GraphicsPoint pntScale = GraphicsPoint.getScalePoint();
		m_tunerBar
				.setMovingSpace((((bmpBG.getWidth() / 2) * pntScale.x) - PADDING_BG)
						/ pntScale.x);
		add(m_tunerBar);
	}

	public void shiftPosition(float sfNewPosition)
	{
		m_tunerBar.shiftPosition(sfNewPosition);
	}

	/**
	 * Hide the drawable from the screen.
	 */
	@Override
	public void hide(	boolean fViolent,
						final OnHiddenListener listener)
	{
		super.hide(fViolent, listener);
		m_tunerBar.hide(fViolent, listener);
	}

	@Override
	public void show(final OnShownListener listener)
	{
		super.show(listener);
		m_tunerBar.show(listener);
	}

	@Override
	public void draw(Canvas canvas)
	{
		super.draw(canvas);
		m_tunerBar.draw(canvas);
	}
}
