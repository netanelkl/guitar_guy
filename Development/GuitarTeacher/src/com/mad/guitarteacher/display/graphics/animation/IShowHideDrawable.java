package com.mad.guitarteacher.display.graphics.animation;

import com.mad.guitarteacher.display.graphics.DisplayState;

/**
 * A simple interface drawables enabling show/hide.
 * 
 * @author Nati
 * 
 */
public interface IShowHideDrawable
{
	public DisplayState getDisplayState();

	/**
	 * Show the Drawable on the screen.
	 * 
	 * @param listener
	 */
	public void show(OnShownListener listener);

	/**
	 * Hides the drawable.
	 * 
	 * @param fViolent
	 *            Whether or not to skip the animation when hiding.
	 * @param listener
	 *            The listener to call when the animation is done.
	 */
	void hide(boolean fViolent, OnHiddenListener listener);
}
