package com.mad.guitarteacher.musicalBase;

import java.util.ArrayList;

/**
 * This class represents a picking finger.
 * @author Tom
 *
 */
public class PickingFinger
{
	public PickingFinger()
	{
		Strings = new ArrayList<Integer>();
	}
	
	/**
	 * The index of the string to be played.
	 */
	// TODO: It has a finite number of values so why not enum?
	// Java enums lose their numerical value. Here the number is important,
	// Any alternative?
	public ArrayList<Integer> Strings;

	/**
	 * The index of the finger to be played.
	 */
	// TODO: It has a finite number of values so why not enum?
	// Java enums lose their numerical value. Here the number is important,
	// Any alternative?
	public int				Finger;
	
}
