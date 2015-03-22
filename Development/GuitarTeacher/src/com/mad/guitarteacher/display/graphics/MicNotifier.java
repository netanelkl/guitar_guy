package com.mad.guitarteacher.display.graphics;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Rect;

import com.mad.guitarteacher.R;
import com.mad.guitarteacher.display.graphics.animation.HangingBitmapDrawable;
import com.mad.guitarteacher.display.graphics.animation.RectEvaluator;
import com.mad.lib.display.graphics.GraphicsPoint;
import com.mad.lib.display.utils.DisplayHelper;

/**
 * A drawable for the pause button.
 * 
 * @author Nati
 * 
 */
public class MicNotifier extends HangingBitmapDrawable
{
	private static final float	SF_SCALE_RATIO				=
																	1.1f;
	private static final float	MARGIN						= 20;
	private static final int	SCALING_ANIMATION_DURATION	=
																	1000;

	/**
	 * Creates a pause button.
	 * 
	 * @param context
	 *            The context.
	 * @param pntInitialPosition
	 *            Top left position of the button.
	 * @param pointScale
	 *            The screen scale.
	 */
	public MicNotifier(	final Context context,
						final PointF pntInitialPosition)
	{
		super(	BitmapFactory.decodeResource(context
						.getResources(), R.drawable.game_mic),
				pntInitialPosition,
				HangingDirection.Right,
				MARGIN / GraphicsPoint.getScalePoint().x,
				MARGIN / GraphicsPoint.getScalePoint().y,
				true);

	}

	@Override
	protected ValueAnimator createAnimationImpl()
	{
		ValueAnimator anim = super.createAnimationImpl();
		anim.addListener(new AnimatorListener()
		{
			private final Rect	m_rectOriginal	=
														new Rect(m_rectBitmap);

			@Override
			public void onAnimationStart(Animator animation)
			{
			}

			@Override
			public void onAnimationRepeat(Animator animation)
			{
			}

			@Override
			public void onAnimationEnd(Animator animation)
			{
				Rect rectEnd = new Rect(m_rectOriginal);
				DisplayHelper.scaleRectangle(m_rectOriginal,
						rectEnd, SF_SCALE_RATIO);
				ObjectAnimator animBitmap =
						ObjectAnimator
								.ofObject(m_rectBitmap, "",
										new RectEvaluator(),
										m_rectOriginal, rectEnd)
								.setDuration(
										SCALING_ANIMATION_DURATION);
				animBitmap
						.setRepeatCount(ObjectAnimator.INFINITE);
				animBitmap.setRepeatMode(ObjectAnimator.REVERSE);
				animBitmap.start();
			}

			@Override
			public void onAnimationCancel(Animator animation)
			{
			}
		});

		return anim;
	}
}
