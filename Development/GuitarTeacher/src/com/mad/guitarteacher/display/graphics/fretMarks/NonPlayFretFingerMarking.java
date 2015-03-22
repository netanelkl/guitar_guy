package com.mad.guitarteacher.display.graphics.fretMarks;

import android.graphics.Canvas;
import android.graphics.PointF;

import com.mad.guitarteacher.display.graphics.DisplayState;

/**
 * A single non play fret finger mark.
 * 
 * @author Tom
 * 
 */
public class NonPlayFretFingerMarking extends FretFingerMarking
{
	/**
	 * Create a new instance of the non play fret marking class.
	 * 
	 * @param finger
	 * @param pointDest
	 */
	public NonPlayFretFingerMarking(final int finger,
									final PointF pointDest)
	{
		super(finger, pointDest);
	}

	@Override
	public void draw(final Canvas canvas)
	{
		if (m_DisplayState != DisplayState.Hidden)
		{
			super.draw(canvas);
		}
	}

	@Override
	public boolean update()
	{
		return super.update();
	}
}
