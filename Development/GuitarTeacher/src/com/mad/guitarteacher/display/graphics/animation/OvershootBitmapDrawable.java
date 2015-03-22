package com.mad.guitarteacher.display.graphics.animation;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.animation.OvershootInterpolator;

/**
 * A class meant to animate drawables showing/hiding with an overshoot animation
 * on a bitmap.
 * 
 * @author Nati
 * 
 */
public class OvershootBitmapDrawable extends
		AnimatedBitmapDrawable
{
	/**
	 * The length of the animation.
	 */
	public static final int	ANIMATION_LENGTH	= 500;

	/**
	 * Creates a new OvershootBitmapDrawable.
	 * 
	 * @param bmpImage
	 *            The bitmap of the drawable.
	 * @param pntPosition
	 *            The position in which to draw. It's position relative to the
	 *            drawable is decided by the fIsCenter parameter.
	 * @param pntScale
	 *            The screen scale factor.
	 * @param fIsCenter
	 *            A boolean indicating whether the positing is in the center of
	 *            the object of the top left.
	 */
	public OvershootBitmapDrawable(	final Bitmap bmpImage,
									final PointF pntPosition,
									final boolean fIsCenter)
	{
		super(bmpImage, pntPosition, fIsCenter);
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
	public OvershootBitmapDrawable(	final Context context,
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
	public OvershootBitmapDrawable(	final Context context,
									final int nResId,
									final PointF pntPosition,
									final boolean fIsCenter)
	{
		super(context, nResId, pntPosition, fIsCenter);
	}

	@Override
	protected ValueAnimator createAnimationImpl()
	{
		ValueAnimator animator = null;
		int nCenterX;
		int nCenterY;
		Rect rectEnd;
		nCenterX =
				(int) (m_pntInitialPosition.x + (m_bmpImage
						.getWidth() / 2));
		nCenterY =
				(int) (m_pntInitialPosition.y + (m_bmpImage
						.getHeight() / 2));
		rectEnd =
				new Rect(	(int) m_pntInitialPosition.x,
							(int) m_pntInitialPosition.y,
							(int) (m_pntInitialPosition.x + m_bmpImage
									.getWidth()),
							(int) (m_pntInitialPosition.y + m_bmpImage
									.getHeight()));
		Rect rectStart =
				new Rect(nCenterX, nCenterY, nCenterX, nCenterY);

		animator =
				ObjectAnimator.ofObject(m_rectBitmap, "",
						new RectEvaluator(), rectStart, rectEnd)
						.setDuration(ANIMATION_LENGTH);
		animator.setInterpolator(new OvershootInterpolator());
		return animator;
	}

}
