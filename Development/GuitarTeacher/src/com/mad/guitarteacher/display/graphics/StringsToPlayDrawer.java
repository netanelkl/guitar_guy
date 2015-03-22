package com.mad.guitarteacher.display.graphics;

import android.app.Activity;
import android.graphics.Color;

import com.mad.guitarteacher.display.graphics.animation.AnimatedDrawableCollection;
import com.mad.lib.display.graphics.GraphicsPoint;

public class StringsToPlayDrawer extends
		AnimatedDrawableCollection
{
	public StringsToPlayDrawer()
	{

	}

	@Override
	public void clear()
	{
		hide(true, null);
	}

	/**
	 * Initialize the object. create the drawables.
	 * 
	 * @param context
	 *            The context.
	 */
	public void initialize(final Activity context)
	{
		// m_arStringsMarks =
		// new StringToPlayMark[GuitarStrings.NUM_STRINGS];

		// Get the scale.
		GraphicsPoint pntScale = GraphicsPoint.getScalePoint();

		// Create the strings.
		for (int i = 0; i < GuitarStrings.NUM_STRINGS; i++)
		{
			StringToPlayMark drawableString =
					new StringToPlayMark(	context,
											(int) (GuitarStrings.STRING_START_OFFSET_X / pntScale.y),
											(int) (GuitarStrings.STRING_LENGTH / pntScale.x),
											(int) ((GuitarStrings.STRING_START_OFFSET_Y + (GuitarStrings.STRINGS_DISTANCE * i)) / pntScale.y),
											(int) (GuitarStrings.STRING_HEIGHTS[i] / pntScale.y));

			add(drawableString);
		}
	}

	/**
	 * Displays a string to play mark and makes it flash.
	 * 
	 * @param nStringToPlay
	 *            The index of the string to mark.
	 */
	public synchronized void showPlayString(final int nStringToPlay)
	{
		StringToPlayMark mark =
				(StringToPlayMark) m_arCollection
						.get(nStringToPlay);
		mark.setColor(Color.YELLOW);
		mark.show(null);
	}

	/**
	 * Hides a flashing string mark.
	 * 
	 * @param nStringToPlay
	 *            The index of the string to mark.
	 */
	public void hidePlayString(	boolean fViolent,
								final int nStringToPlay)
	{
		StringToPlayMark mark =
				(StringToPlayMark) m_arCollection
						.get(nStringToPlay);
		// Check if the string mark is shown.
		if ((mark.getDisplayState() != DisplayState.Hidden)
				&& (mark.getDisplayState() != DisplayState.Hiding))
		{
			// If the string mark is shown, change it's color to green (to
			// indicate it was played correctly) and hide it.
			if (!fViolent)
			{
				mark.setColor(Color.GREEN);
			}
			mark.hide(fViolent);
		}
	}
}
