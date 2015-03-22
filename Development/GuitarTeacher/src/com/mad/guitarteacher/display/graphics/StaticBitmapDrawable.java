package com.mad.guitarteacher.display.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.mad.guitarteacher.display.graphics.animation.IShowHideDrawable;
import com.mad.guitarteacher.display.graphics.animation.OnHiddenListener;
import com.mad.guitarteacher.display.graphics.animation.OnShownListener;
import com.mad.lib.display.utils.DisplayHelper;

/**
 * A simple static bitmap drawable.
 * 
 * This supports a basic hide and show methods.
 * 
 * @author Nati
 * 
 */
public class StaticBitmapDrawable extends GraphicsDrawableBase
		implements IShowHideDrawable
{

	/**
	 * The bitmap for the current drawable.
	 */
	private final Bitmap	m_bmpImage;

	/**
	 * The display state of the drawable.
	 */
	protected DisplayState	m_DisplayState			=
															DisplayState.Hidden;

	/**
	 * The current position of the drawable.
	 */
	protected PointF		m_pntCurrentPosition	=
															new PointF();

	/**
	 * Creates a new StaticBitmapDrawable.
	 * 
	 * @param bmpImage
	 *            The bitmap used to create the drawable.
	 * @param sfLeft
	 *            The left position to draw.
	 * @param sfTop
	 *            Top position to draw.
	 */
	public StaticBitmapDrawable(final Bitmap bmpImage,
								final float sfLeft,
								final float sfTop,
								boolean fShown,
								boolean fIsCenter)
	{
		m_pntCurrentPosition.set(sfLeft, sfTop);
		setInitialPoint(m_pntCurrentPosition, fIsCenter);
		m_bmpImage = bmpImage;
		m_DisplayState =
				fShown ? DisplayState.Shown
						: DisplayState.Hidden;
	}

	/**
	 * Creates a new StaticBitmapDrawable.
	 * 
	 * @param context
	 *            The context.
	 * @param nResId
	 *            The resId of the bitmap.
	 * @param sfLeft
	 *            The left position to draw.
	 * @param sfTop
	 *            Top position to draw.
	 * @param pntScale
	 *            The screen scaling from original bitmaps.
	 */
	public StaticBitmapDrawable(final Context context,
								final int nResId,
								final float sfLeft,
								final float sfTop,
								boolean fShown,
								boolean fIsCenter)
	{
		m_pntCurrentPosition.set(sfLeft, sfTop);
		setInitialPoint(m_pntCurrentPosition, fIsCenter);
		m_bmpImage =
				DisplayHelper.getScaledBitmap(context
						.getResources(), nResId, true);

		m_DisplayState =
				fShown ? DisplayState.Shown
						: DisplayState.Hidden;
	}

	@Override
	public void draw(final Canvas canvas)
	{
		if (m_DisplayState != DisplayState.Hidden)
		{
			super.draw(canvas);
			canvas.drawBitmap(m_bmpImage,
					m_pntCurrentPosition.x,
					m_pntCurrentPosition.y, null);
		}

	}

	@Override
	public DisplayState getDisplayState()
	{
		return m_DisplayState;
	}

	public void hide()
	{
		hide(false, null);
	}

	/**
	 * Hide the drawable from the screen.
	 */
	@Override
	public void hide(	boolean fViolent,
						final OnHiddenListener listener)
	{
		m_DisplayState = DisplayState.Hidden;
		m_fInvalidate = true;
	}

	public void show()
	{
		show(null);
	}

	@Override
	public void show(final OnShownListener listener)
	{
		m_DisplayState = DisplayState.Shown;
		m_fInvalidate = true;
	}

	/**
	 * a layout method to order the bitmap's destined position according to the
	 * given params.
	 * 
	 * @param pntPosition
	 *            The position on which to draw.
	 * @param pntScale
	 *            The screen scaling to use.
	 * @param fIsCenter
	 *            A boolean indicating whether or not to draw it on the center.
	 */
	private void setInitialPoint(	final PointF pntPosition,
									final boolean fIsCenter)
	{
		if (fIsCenter)
		{
			// Calc the position to draw
			m_pntCurrentPosition.set((pntPosition.x)
					- (m_bmpImage.getWidth() / 2),
					(pntPosition.y)
							- (m_bmpImage.getHeight() / 2));

		}
		else
		{
			m_pntCurrentPosition.set((pntPosition.x),
					pntPosition.y);
		}
	}

}
