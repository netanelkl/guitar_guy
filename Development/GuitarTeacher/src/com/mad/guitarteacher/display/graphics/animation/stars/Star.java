package com.mad.guitarteacher.display.graphics.animation.stars;

import android.graphics.Bitmap;
import android.graphics.PointF;

import com.mad.guitarteacher.display.graphics.animation.OvershootBitmapDrawable;

public class Star extends OvershootBitmapDrawable
{

	public Star(final Bitmap bmpImage, final PointF pntPosition)
	{
		super(bmpImage, pntPosition, false);
	}

}
