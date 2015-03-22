package com.mad.guitarteacher.display.graphics.animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.mad.guitarteacher.display.graphics.DisplayState;
import com.mad.lib.display.graphics.GraphicsPoint;
import com.mad.lib.display.utils.DisplayHelper;

/**
 * A class meant to animate a bitmap showing/hiding with an animation on a
 * bitmap.
 * 
 * @author Nati
 * 
 */
public abstract class AnimatedBitmapDrawable extends
		AnimatedDrawable
{

	/**
	 * The bitmap of the drawable.
	 */
	protected Bitmap		m_bmpImage;

	/**
	 * The position on which to draw the drawable.
	 */
	protected final Rect	m_rectBitmap	= new Rect();

	protected Paint			m_paintBitmap;

	/**
	 * Creates a new OvershootBitmapDrawable.
	 * 
	 * @param bmpImage
	 *            The bitmap of the drawable.
	 * @param pntPosition
	 *            The position in which to draw. It's position relative to the
	 *            drawable is decided by the fIsCenter parameter.
	 * @param fIsCenter
	 *            A boolean indicating whether the positing is in the center of
	 *            the object of the top left.
	 */
	public AnimatedBitmapDrawable(	final Bitmap bmpImage,
									final PointF pntPosition,
									final boolean fIsCenter)
	{
		this(bmpImage, pntPosition, fIsCenter, false);
	}

	public AnimatedBitmapDrawable(	final Bitmap bmpImage,
									final PointF pntPosition,
									final boolean fIsCenter,
									final boolean fLoadOriginalDimensions)
	{
		m_bmpImage = bmpImage;
		GraphicsPoint pntOriginal =
				new GraphicsPoint(	m_bmpImage.getWidth(),
									m_bmpImage.getHeight());
		if (fLoadOriginalDimensions)
		{
			DisplayHelper.measureScaledDimensions(pntOriginal,
					pntOriginal, true);
		}

		setInitialPoint(pntPosition, pntOriginal.x,
				pntOriginal.y, fIsCenter);
	}

	/**
	 * Creates a new OvershootBitmapDrawable.
	 * 
	 * @param context
	 *            The context.
	 * @param resId
	 *            The resId of the bitmap of the drawable.
	 * @param pntPosition
	 *            The position in which to draw. It's position relative to the
	 *            drawable is top-left.
	 * @param pntScale
	 *            The screen scale factor.
	 */
	public AnimatedBitmapDrawable(	final Context context,
									final int resId,
									final PointF pntPosition)
	{
		this(context, resId, pntPosition, false);
	}

	/**
	 * Creates a new OvershootBitmapDrawable.
	 * 
	 * @param context
	 *            The context.
	 * @param nResId
	 *            The resId of the bitmap of the drawable.
	 * @param pntPosition
	 *            The position in which to draw. It's position relative to the
	 *            drawable is decided by the fIsCenter parameter.
	 * @param pntScale
	 *            The screen scale factor.
	 * @param fIsCenter
	 *            A boolean indicating whether the positing is in the center of
	 *            the object of the top left.
	 */
	public AnimatedBitmapDrawable(	final Context context,
									final int nResId,
									final PointF pntPosition,
									final boolean fIsCenter)
	{
		// Create a scaled one.
		m_bmpImage =
				DisplayHelper.getScaledBitmap(context
						.getResources(), nResId);

		setInitialPoint(pntPosition, m_bmpImage.getWidth(),
				m_bmpImage.getHeight(), fIsCenter);
	}

	@Override
	public void draw(final Canvas canvas)
	{
		if (m_DisplayState != DisplayState.Hidden)
		{
			super.draw(canvas);
			// Draw the actual menu (Rect).
			canvas.drawBitmap(m_bmpImage, null, m_rectBitmap,
					m_paintBitmap);
		}
	}

	public float getHeight()
	{
		return m_rectBitmap.height();
	}

	public float getWidth()
	{
		return m_rectBitmap.width();
	}

	@Override
	public boolean handleTouched(	final Context context,
									final int nTouchX,
									final int nTouchY)
	{ // Check if the touch event happened inside the rect.
		if ((nTouchX >= (m_pntInitialPosition.x))
				&& (nTouchX <= (m_pntInitialPosition.x + m_bmpImage
						.getWidth()))
				&& (nTouchY >= (m_pntInitialPosition.y))
				&& (nTouchY <= (m_pntInitialPosition.y + m_bmpImage
						.getHeight())))
		{
			return true;
		}

		return false;
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
									final float sfWidth,
									final float sfHeight,
									final boolean fIsCenter)
	{

		if (fIsCenter)
		{
			// Calc the position to draw
			m_pntInitialPosition.set((pntPosition.x)
					- (sfWidth / 2), (pntPosition.y)
					- (sfHeight / 2));

		}
		else
		{
			m_pntInitialPosition.set((pntPosition.x),
					pntPosition.y);
		}
		m_rectBitmap.set((int) m_pntInitialPosition.x,
				(int) m_pntInitialPosition.y,
				(int) (m_pntInitialPosition.x + sfWidth),
				(int) (m_pntInitialPosition.y + sfHeight));
		setBounds(m_rectBitmap);
	}
}
