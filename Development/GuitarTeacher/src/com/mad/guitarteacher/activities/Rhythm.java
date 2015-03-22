package com.mad.guitarteacher.activities;

import java.util.ArrayList;

/**
 * This class represents rhythm.
 * @author Tom
 *
 */
public class Rhythm
{
	ArrayList<Integer> m_arRhythmIntervals;
	
	public Rhythm()
	{
		m_arRhythmIntervals = new ArrayList<Integer>();
	}
	
	public ArrayList<Integer> getRhythm()
	{
		return m_arRhythmIntervals;
	}
	
	/**
	 * Add a rhytm scaled value.
	 * @param rhythmScale - Scaled value.
	 * 
	 * example: if scaled value = 8 then the beat will be
	 * 			of one eight of a note.
	 * 			if the array will be: 2 4 4 then the rhythm will
	 * 			be: half note quarter note quarter note.
	 */
	public void add(int rhythmScale)
	{
		m_arRhythmIntervals.add(rhythmScale);
	}
}

