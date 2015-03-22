package com.mad.tunerlib.recognition;

/**
 * This inner class gives information about detection of a note.
 */
public class SingleChordDetectionInfo
{
	/**
	 * The type of the note.
	 */
	public String	Chord;

	/**
	 * The duration of time that to chord was played.
	 */
	public float	Duration;

	/**
	 * The probability that that's the note that was being played and that it
	 * was played correctly.
	 */
	public float	Probability;

	public float	Time;

	public SingleChordDetectionInfo()
	{
	}

	public SingleChordDetectionInfo(final float sfDuration,
									final String strChord)
	{
		Duration = sfDuration;
		Chord = strChord;
	}

	/**
	 * Clear this object's information.
	 */
	public void clear()
	{
		Chord = null;
	}

	/**
	 * Returns whether there was a chord found.
	 * 
	 * @return
	 */
	public boolean isEmpty()
	{
		return Chord == null;
	}

	/*
	 * TODO: Add the octave!
	 */
}
