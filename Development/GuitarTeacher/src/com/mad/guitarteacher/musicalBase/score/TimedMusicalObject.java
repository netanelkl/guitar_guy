package com.mad.guitarteacher.musicalBase.score;

import com.mad.lib.musicalBase.MusicalObject;

/**
 * This class represents a timed note.
 * 
 * @author Tom
 * 
 */
public class TimedMusicalObject
{
	/**
	 * The number of beats this note should be played for.s
	 */
	private final int			m_nBeats;

	/**
	 * The actual musical base object. Should be either OctavedNote or Chord.
	 */
	private final MusicalObject	m_MusicNote;

	/**
	 * Create a new instance of the timed note object.
	 * 
	 * @param timeSpan
	 *            - Time span to set for the note.
	 * @param nAbsoluteIndex
	 */
	public TimedMusicalObject(	MusicalObject musicalObject,
								int nBeats)
	{
		m_nBeats = nBeats;
		m_MusicNote = musicalObject;
	}

	/**
	 * @return the m_MusicNote
	 */
	public MusicalObject getMusicalObject()
	{
		return m_MusicNote;
	}

	/**
	 * @return the m_nBeats
	 */
	public int getBeats()
	{
		return m_nBeats;
	}
}
