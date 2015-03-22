package com.mad.guitarteacher.display.graphics.fretMarks;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;

import com.mad.guitarteacher.display.graphics.animation.FloatLinearAnimator;

/**
 * A class to create a circle.
 * 
 * @author Tom
 * 
 */
public abstract class Circle extends FloatLinearAnimator
{
	public Circle(final PointF pntCenter)
	{
		super(0, 1f, 3000);
		m_pntInitialPosition.set(pntCenter);
	}

	@Override
	public void draw(final Canvas canvas)
	{
		Matrix matrix = new Matrix();
		matrix.reset();

		// We first bring the image to a circle (instead of an ellipse
		// because of scaling.
		// matrix.postScale(pntScale.x, pntScale.y);

		Bitmap bitmap = getBitmap();

		if (bitmap == null)
		{
			// TODO: ERROR
			return;
		}

		// Centers image
		matrix.postTranslate((-bitmap.getWidth()) / 2,
				(-bitmap.getHeight()) / 2);

		// Rotate.
		matrix.postRotate(360 * m_sfFloatLinearValue);

		// Bring it back to be small.
		// matrix.postScale(1 / pntScale.x, 1 / pntScale.y);

		matrix.postTranslate(m_pntInitialPosition.x, m_pntInitialPosition.y);
		canvas.drawBitmap(bitmap, matrix, null);
	}

	protected abstract Bitmap getBitmap();
}
