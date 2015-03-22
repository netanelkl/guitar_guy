package com.mad.guitarteacher.display.graphics.fretMarks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;

import com.mad.guitarteacher.R;
import com.mad.lib.display.utils.DisplayHelper;

/**
 * A class to create a non revolving circle.
 * 
 * @author Tom
 * 
 */
public class NonPlayCircle extends Circle
{
	static
	{
		// TODO: Change bitmap.

	}

	/**
	 * Create a new instance of the NonPlayCircle class.
	 * 
	 * @param pntCenter
	 */
	public NonPlayCircle(Context context, final PointF pntCenter)
	{
		super(pntCenter);

		if (s_bmpRevolvingCircle == null)
		{
			s_bmpRevolvingCircle =
					DisplayHelper
							.getScaledBitmap(
									context.getResources(),
									R.drawable.game_numbers_rotating_circle);
		}
	}

	@Override
	protected android.animation.ValueAnimator createAnimationImpl()
	{
		return null;
	};

	/**
	 * The bitmap for the revolving circle.
	 */
	static Bitmap	s_bmpRevolvingCircle	= null;

	@Override
	protected Bitmap getBitmap()
	{
		return s_bmpRevolvingCircle;
	}
}
