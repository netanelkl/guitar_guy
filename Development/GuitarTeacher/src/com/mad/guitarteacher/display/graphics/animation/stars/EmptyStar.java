package com.mad.guitarteacher.display.graphics.animation.stars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;

import com.mad.guitarteacher.R;
import com.mad.lib.display.utils.DisplayHelper;

public class EmptyStar extends Star
{
	private static Bitmap	s_bmpEmptyStar;

	public static void initialize(final Context context)
	{
		if (s_bmpEmptyStar == null)
		{
			s_bmpEmptyStar =
					DisplayHelper.getScaledBitmap(context
							.getResources(),
							R.drawable.game_score_empty_star);
		}

	}

	public EmptyStar(final PointF pntPosition)
	{
		super(s_bmpEmptyStar, pntPosition);
	}
}
