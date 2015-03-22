package com.mad.lib.musicalBase;

import java.util.ArrayList;

/**
 * Displaying a hand position.
 * 
 * @author Tom
 * 
 */
public interface IHandPositioning
{
	/**
	 * Get the finger position to show.
	 * 
	 * @return
	 */
	// TODO: Should be always the size of the number of strings, which can vary
	// in a guitar!!!!
	public ArrayList<FretFingerPairBase> getFingerPositions();

	/**
	 * Number of the strings that the guitar has.
	 * 
	 * this is here because this class can be read from file. be careful because
	 * a hand position which was made for a 7 string guitar, trying to be played
	 * on a mandolin will surely cause errors.
	 */
	public int getNumberOfStrings();
}
