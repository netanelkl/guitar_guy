package com.mad.lib.musicalBase.chords;

import com.mad.lib.R;
import com.mad.lib.chordGeneration.IChordGenerator;
import com.mad.lib.musicalBase.IHandPositioning;
import com.mad.lib.musicalBase.MusicalObject;
import com.mad.lib.musicalBase.Notes;
import com.mad.lib.musicalBase.OctavedNote;
import com.mad.lib.musicalBase.keyConverters.EKeyConverterType;
import com.mad.lib.musicalBase.keyConverters.IKeyConverter;
import com.mad.lib.musicalBase.keyConverters.KeyConverterFactory;
import com.mad.lib.utils.AppLibraryServiceProvider;

public class Chord extends MusicalObject
{

	/**
	 * Data members.
	 */
	private OctavedNote[]	m_arComposingNotes;
	public ChordParameters	m_ChordParameters;

	public EChordType getChordType()
	{
		return m_ChordParameters.getChordType();
	}

	/**
	 * Creates a new instance of the Chord object.
	 */
	public Chord(ChordParameters chordParameters)
	{
		m_ChordParameters = chordParameters;

		// Lazy loading.
		if (m_arComposingNotes == null)
		{
			int nFirstNote = m_ChordParameters.getRootNote();

			int nAddedNotesForGuitar = -Notes.C;
			if (nFirstNote < Notes.E)
			{
				nAddedNotesForGuitar += Notes.NumberOfNotes;
			}

			EKeyConverterType eConverterType =
					EChordType
							.getKeyTypeForChordType(m_ChordParameters
									.getChordType());

			KeyConverterFactory factory =
					AppLibraryServiceProvider.getInstance().get(
							R.service.key_converter_factory);
			IKeyConverter keyConverter =
					factory.getConverter(eConverterType);

			int[] noteTable =
					keyConverter.getNoteTable(nFirstNote);

			// Now add the notes.
			m_arComposingNotes =
					new OctavedNote[] {
							new OctavedNote(nAddedNotesForGuitar
									+ nFirstNote + noteTable[0]),
							new OctavedNote(nAddedNotesForGuitar
									+ nFirstNote + noteTable[2]),
							new OctavedNote(nAddedNotesForGuitar
									+ nFirstNote + noteTable[4]) };
		}
	}

	/**
	 * Create a new instance of a chord.
	 * 
	 * @param nFirstNote
	 *            - The first note of the chord.
	 * @param eChordType
	 *            - The chord type.
	 */
	public Chord(int nFirstNote, EChordType eChordType)
	{
		this(new ChordParameters(nFirstNote, eChordType));
	}

	/*
	 * Creates a new chord object based on the chord's name.
	 */
	public static Chord parse(String name)
	{

		Chord chord = new Chord(ChordParameters.parse(name));

		return chord;
	}

	/**
	 * Get the array of notes composing the chord.
	 * 
	 * @return ENote[] notes composing the chord.
	 */
	public final OctavedNote[] getComposingNotes()
	{
		return m_arComposingNotes;
	}

	/**
	 * Get the name of the chord.
	 * 
	 * @return String name of the chord.
	 */
	@Override
	public String getName()
	{
		return String.valueOf(OctavedNote
				.getName(m_ChordParameters.getRootNote())
				+ m_ChordParameters.getChordType().toString());
	}

	IHandPositioning	m_BasicHandPositioning;

	/**
	 * Returns the handPositioning for the chord.
	 * 
	 * @return Fingering, handPositioning for the chord.
	 */
	// TODO: Should make this function receive parameters: position, and base
	// note.
	// [Kidder 19 Apr 2013 - 15:33:50]: What we want is to keep all information
	// we need on the Chord.
	// That it would be specific enough to know everything.
	@Override
	public IHandPositioning getHandPositioning(int lastScale)
	{
		IChordGenerator generator =
				AppLibraryServiceProvider.getInstance().get(
						R.service.chord_generator);
		if (m_BasicHandPositioning == null)
		{
			// multiply the notes, to allow them to be played more than once.
			// This is how chords are usually played on the guitar.
			final int nMultiply = 4;

			// Convert all the Integers to ints
			int[] notes =
					new int[m_arComposingNotes.length
							* nMultiply];
			for (int nMulIndex = 0; nMulIndex < nMultiply; nMulIndex++)
			{
				for (int i = 0; i < m_arComposingNotes.length; i++)
				{
					notes[i
							+ (nMulIndex * m_arComposingNotes.length)] =
							m_arComposingNotes[i]
									.getIntervalIndex()
									+ (nMulIndex * Notes.NumberOfNotes);
				}
			}

			// Get the guitar hand for the chord.
			m_BasicHandPositioning =
					generator
							.getFingerPosition(notes, lastScale);
		}

		return m_BasicHandPositioning;
	}
}
