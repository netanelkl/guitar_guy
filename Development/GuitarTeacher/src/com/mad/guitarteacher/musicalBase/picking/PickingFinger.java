package com.mad.guitarteacher.musicalBase.picking;

import com.mad.guitarteacher.musicalBase.GuitarString;

/**
 * This class represents a picking finger.
 * 
 * @author Tom
 * 
 */
public class PickingFinger
{
	public PickingFinger(int finger, GuitarString string)
	{
		mFinger = finger;
		mString = new GuitarString(string);
	}

	public PickingFinger(int finger, int string)
	{
		mFinger = finger;
		mString = new GuitarString(string);
	}

	/**
	 * The index of the string to be played.
	 */
	// TODO: It has a finite number of values so why not enum?
	// Java enums lose their numerical value. Here the number is important,
	// Any alternative?
	private final GuitarString	mString;

	public int getFinger()
	{
		return mFinger;
	}

	/**
	 * The index of the finger to be played.
	 */
	private final int	mFinger;

	public GuitarString getString()
	{
		return mString;
	}

}
