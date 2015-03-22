package com.mad.guitarteacher.display.graphics.fretMarks;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.mad.guitarteacher.display.graphics.DisplayState;

/**
 * A single fret finger mark to play.
 * 
 * This is the Drawable to draw a circle with the number of the finger and a
 * revolving circle around it.
 * 
 * @author Nati, Tom
 * 
 */
public class PlayFretFingerMarking extends FretFingerMarking
{
	/**
	 * The revolving circle for this mark.
	 */
	private final Circle	m_Circle;

	/**
	 * Create a new instance of the PlayFretFingerMarking class.
	 * 
	 * @param finger
	 * @param pointDest
	 */
	public PlayFretFingerMarking(	Context context,
									final int finger,
									final PointF pointDest)
	{
		super(finger, pointDest);

		m_Circle = new RevolvingCircle(context, pointDest);

		m_Circle.show();
	}

	@Override
	public void draw(final Canvas canvas)
	{
		if (m_DisplayState != DisplayState.Hidden)
		{
			super.draw(canvas);
			m_Circle.draw(canvas);
		}
	}

	@Override
	public boolean update()
	{
		return super.update() || m_Circle.update();
	}
}
