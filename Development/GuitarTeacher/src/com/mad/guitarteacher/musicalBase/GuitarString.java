package com.mad.guitarteacher.musicalBase;

/**
 * This class represents a guitar string. 
 * @author Tom
 *
 */
public class GuitarString
{
	int mString;
	
	// Value for the bass note of a chord.
	public final static int BASS_STRING = 776;
	
	/**
	 * Get the guitar string value.
	 * @return guitar string.
	 */
	public int getString()
	{
		return mString;
	}
	
	/**
	 * Copy constructor.
	 * @param string - Another guitar string.
	 */
	public GuitarString(GuitarString string)
	{
		mString = string.mString;
	}
	
	/**
	 * Create a new instance of the GuitarString
	 * @param string - string value.
	 */
	public GuitarString(int string)
	{
		// TODO: Validate input
		mString = string;
	}

}
