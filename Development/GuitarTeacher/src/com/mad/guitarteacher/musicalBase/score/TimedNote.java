package com.mad.guitarteacher.musicalBase.score;

import com.mad.lib.musicalBase.IHasHandPositioning;

/**
 * This class represents a timed note.
 * 
 * @author Tom
 * 
 */
public class TimedNote
{
	/**
	 * The number of beats this note should be played for.s
	 */
	private final int					m_nBeats;

	/**
	 * The actual musical base object. Should be either OctavedNote or Chord.
	 */
	private final IHasHandPositioning	m_MusicNote;

	/**
	 * Create a new instance of the timed note object.
	 * 
	 * @param timeSpan
	 *            - Time span to set for the note.
	 * @param nAbsoluteIndex
	 */
	public TimedNote(	IHasHandPositioning musicalObject,
						int nBeats)
	{
		m_nBeats = nBeats;
		m_MusicNote = musicalObject;
	}

	/**
	 * @return the m_MusicNote
	 */
	public IHasHandPositioning getMusicNote()
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
