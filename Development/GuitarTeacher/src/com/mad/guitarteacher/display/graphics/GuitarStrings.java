/**
 * This file contains important constants about the layout of the guitar strings on the game activity.
 * 
 * @author Guy Kaplan
 * @since 18.02.2014
 */
package com.mad.guitarteacher.display.graphics;

import com.mad.lib.chordGeneration.EString;

/**
 * @author Guy
 * 
 */
public class GuitarStrings
{

	/**
	 * The distance between each strings on the Y axis.
	 */
	public static final int		STRINGS_DISTANCE		= 87;

	/**
	 * The number of the displayed strings on the guitar.
	 */
	public static final int		NUM_STRINGS				=
																EString.NumberOfStrings;
	/**
	 * The length of the strings on the X axis.
	 */
	public static final int		STRING_LENGTH			= 1546;

	/**
	 * The height of the strings (the length on the Y axis).
	 */
	public static final int[]	STRING_HEIGHTS			= { 18,
			12, 10, 8, 6, 4							};

	/**
	 * The distance from the left of the screen to the start of the strings.
	 */
	public static final int		STRING_START_OFFSET_X	= 0;

	/**
	 * The distance from the top of the screen to the start of the strings.
	 */
	public static final int		STRING_START_OFFSET_Y	= 319;

}
