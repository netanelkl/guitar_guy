package com.mad.guitarteacher.display.utils;

import android.view.animation.Interpolator;

public class ReverseInterpolator implements Interpolator
{
	@Override
	public float getInterpolation(final float paramFloat)
	{
		return Math.abs(paramFloat - 1f);
	}
}
