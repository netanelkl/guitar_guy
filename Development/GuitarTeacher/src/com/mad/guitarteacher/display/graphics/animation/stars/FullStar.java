package com.mad.guitarteacher.display.graphics.animation.stars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;

import com.mad.guitarteacher.R;
import com.mad.lib.display.utils.DisplayHelper;

public class FullStar extends Star
{
	private static Bitmap	s_bmpFullStar;

	public static int getStarHeight()
	{
		return s_bmpFullStar.getHeight();
	}

	public static int getStarWidth()
	{
		return s_bmpFullStar.getWidth();
	}

	public static void staticInit(final Context context)
	{
		if (s_bmpFullStar == null)
		{
			// Create a scaled one.
			s_bmpFullStar =
					DisplayHelper.getScaledBitmap(context
							.getResources(),
							R.drawable.game_score_full_star);
		}

	}

	public FullStar(final PointF pntPosition)
	{
		super(s_bmpFullStar, pntPosition);
	}

}
