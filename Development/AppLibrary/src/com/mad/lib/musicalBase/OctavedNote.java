package com.mad.lib.musicalBase;

import java.security.InvalidParameterException;

import com.mad.lib.R;
import com.mad.lib.chordGeneration.IChordGenerator;
import com.mad.lib.utils.AppLibraryServiceProvider;

public class OctavedNote extends MusicalObject
{
	private final int					m_nNoteIndex;
	private final int					m_nOctaveIndex;
	private int							m_nOptionalPosition;
	private static String[]				s_arName			=
																	new String[] {
			"A", "A#", "B", "C", "C#", "D", "D#", "E", "F",
			"F#", "G", "G#"										};
	private static final OctavedNote[]	s_openStringNotes	= {
			new OctavedNote(Notes.E, 0),
			new OctavedNote(Notes.A, 0),
			new OctavedNote(Notes.D, 1),
			new OctavedNote(Notes.G, 1),
			new OctavedNote(Notes.B, 1),
			new OctavedNote(Notes.E, 2),					};

	public static OctavedNote[] getOpenStringNotes()
	{
		return s_openStringNotes;
	}

	/**
	 * Returns the name of the note.
	 * 
	 * @param nIndex
	 *            The index of the note.
	 * 
	 * @return The name of the note.
	 */
	public static String getName(int nIndex)
	{
		return s_arName[nIndex];
	}

	@Override
	public String toString()
	{
		return getName();
	};

	public OctavedNote(int nAbsoluteIndex)
	{
		m_nNoteIndex =
				(nAbsoluteIndex + Notes.C) % Notes.NumberOfNotes;
		m_nOctaveIndex = nAbsoluteIndex / Notes.NumberOfNotes;
	}

	/*
	 * Gets the absolute index (0 being C on the lowest guitar octave).
	 */
	public int getAbsoluteIndex()
	{
		return (((m_nNoteIndex - Notes.C) + Notes.NumberOfNotes) % Notes.NumberOfNotes)
				+ (m_nOctaveIndex * Notes.NumberOfNotes);
	}

	/*
	 * Gets the absolute index, from the Lowest Guitar E note.
	 */
	public int getIntervalIndex()
	{
		int nOctave = m_nOctaveIndex;

		/**
		 * If we are still in the last octave, we should remember that it's
		 * higher than the E before it. We do this by temporarily increasing the
		 * octave of the notes before C.
		 */
		if (m_nNoteIndex < Notes.C)
		{
			nOctave++;
		}

		return (m_nNoteIndex + (nOctave * Notes.NumberOfNotes))
				- Notes.E;
	}

	public OctavedNote(int nNote, int nOctave)
	{
		if ((nOctave == 0) && (nNote >= Notes.C)
				&& (nNote < Notes.E))
		{
			throw new InvalidParameterException();
		}
		m_nNoteIndex = nNote;
		m_nOctaveIndex = nOctave;
	}

	public OctavedNote(	int nNote,
						int nOctave,
						int nOptionalPosition)
	{
		this(nNote, nOctave);
		m_nOptionalPosition = nOptionalPosition;
	}

	/**
	 * Returns the note of the current octaved note.
	 * 
	 * @return ENote the objects note.
	 */
	public int getNote()
	{
		return m_nNoteIndex;
	}

	/**
	 * Returns the octave of the current octaved note.
	 * 
	 * @return EOctave the objects octave.
	 */
	public int getOctave()
	{
		return m_nOctaveIndex;
	}

	// TODO: I dont get this function.
	public int getOptionalString()
	{
		return m_nOptionalPosition;
	}

	/**
	 * Should return the name of the note
	 * 
	 * @return the name of the note.
	 */
	@Override
	public String getName()
	{
		return s_arName[m_nNoteIndex];
	}

	/**
	 * Get hand positioning based on last played note.
	 * 
	 * @param sLastPosition
	 *            - Last played note.
	 * @return Hand positioning.
	 */
	@Override
	public IHandPositioning getHandPositioning(int lastScale)
	{
		IChordGenerator generator =
				AppLibraryServiceProvider.getInstance().get(
						R.service.chord_generator);
		// Get the note finger position.
		// The get finger position gets intervals from lowest E so we translate
		// it.
		return generator.getFingerPosition(
				new int[] { getIntervalIndex() }, lastScale);

	}
}
