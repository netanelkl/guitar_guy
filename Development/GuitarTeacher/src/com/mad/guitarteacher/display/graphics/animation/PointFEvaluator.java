package com.mad.guitarteacher.display.graphics.animation;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

public class PointFEvaluator implements TypeEvaluator<PointF>
{
	public PointF evaluate(float fraction, PointF startValue, PointF endValue)
	{
		PointF startPoint = (PointF) startValue;
		PointF endPoint = (PointF) endValue;
		return new PointF(
				startPoint.x + fraction * (endPoint.x - startPoint.x),
				startPoint.y + fraction * (endPoint.y - startPoint.y));
	}
}
