package com.mad.guitarteacher.display.graphics.fretMarks;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;

import com.mad.guitarteacher.R;
import com.mad.lib.display.utils.DisplayHelper;

/**
 * A class to create the revolving circle.
 * 
 * It uses a basic FloatLinearAnimator to create an animation rotating the
 * circle.
 * 
 * @author Nati
 * 
 */
public class RevolvingCircle extends Circle
{

	public RevolvingCircle(	Context context,
							final PointF pntCenter)
	{
		super(pntCenter);

		if (s_bmpRevolvingCircle == null)
		{
			s_bmpRevolvingCircle =
					DisplayHelper
							.getScaledBitmap(
									context.getResources(),
									R.drawable.game_numbers_rotating_circle,
									true);
		}
	}

	@Override
	protected ValueAnimator createAnimationImpl()
	{
		ValueAnimator animator = super.createAnimationImpl();
		animator.setRepeatCount(ObjectAnimator.INFINITE);

		return animator;
	}

	/**
	 * The bitmap for the revolving circle.
	 */
	static Bitmap	s_bmpRevolvingCircle;

	@Override
	protected Bitmap getBitmap()
	{
		return s_bmpRevolvingCircle;
	}
}
