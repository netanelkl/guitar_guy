package com.mad.lib.musicalBase.keyConverters;

import com.mad.lib.musicalBase.Notes;

public abstract class BaseKeyConverter implements IKeyConverter
{
	/**
	 * Note table.
	 */
	int[][]		m_arKeyTable;
	final int	STARTING_NOTE	= Notes.G - Notes.A;	// G
	final int	ENDING_NOTE		= Notes.C - Notes.A;	// C

	/**
	 * The quarta ring. Contains the notes to upvalue, in order of the # scales.
	 * Notes jumps in quartas.
	 */
	int[]		QUARTA_RING		= {
								// Whole notes from A to F
			5,
			// Whole notes from A to C
			2,
			// Whole notes from A to G
			6,
			// Whole notes from A to D
			3,
			// Whole notes from A to A
			0,
			// Whole notes from A to E
			4,
			// Whole notes from A to B
			1					};

	/*
	 * { (Notes.F - Notes.C) % (Notes.NumberOfNotes), (Notes.C - Notes.C) %
	 * (Notes.NumberOfNotes), (Notes.G - Notes.C) % (Notes.NumberOfNotes),
	 * (Notes.D - Notes.C) % (Notes.NumberOfNotes), (Notes.A - Notes.C) %
	 * (Notes.NumberOfNotes), (Notes.E - Notes.C) % (Notes.NumberOfNotes),
	 * (Notes.B - Notes.C) % (Notes.NumberOfNotes), };
	 */

	/**
	 * Create a new key converter object.
	 */
	public BaseKeyConverter()
	{
		int nNumOfNotes = getNumberOfNotes();
		m_arKeyTable = new int[Notes.NumberOfNotes][nNumOfNotes];

		// int nCurrentKey = STARTING_NOTE;
		// int nCurrentRingIndex = 0;
		int[] notesForKey = getIntervals();
		for (int i = 0; i < nNumOfNotes; i++)
		{
			m_arKeyTable[STARTING_NOTE][i] = notesForKey[i];
			m_arKeyTable[ENDING_NOTE][i] = notesForKey[i];
		}
		// TODO: We missed the actual logic of this class again... we need to
		// rethink
		// this part.
		return;
		// Go through the keys.
		// while(true)
		// {
		// // Note in note ring.
		// int nNoteToUpvalue = QUARTA_RING[nCurrentRingIndex];
		//
		// // Advance the current note.
		// m_arKeyTable[nCurrentKey][nNoteToUpvalue]++;
		//
		// // Go to the next key. (Quinta).
		// // TODO: Put this somewhere.
		// final int QUINTA = 4; // Zero base so 4 instead of 5.
		// int nNextKey = nCurrentKey + m_arNotesForKey[QUINTA];
		// nNextKey %= Notes.NumberOfNotes;
		//
		// // Check if wrap around.
		// if(nNextKey == ENDING_NOTE)
		// {
		// // Bye bye.
		// break;
		// }
		//
		// // Get the next note in the QUARTA RING to upvalue.
		// nCurrentRingIndex++;
		// nCurrentRingIndex %= nNumOfNotes;
		//
		// // Copy all the note values to the next key.
		// for (int i = 0; i < m_arKeyTable[nNextKey].length; i++)
		// {
		// m_arKeyTable[nNextKey][i] = m_arKeyTable[nCurrentKey][i];
		// }
		//
		// nCurrentKey = nNextKey;
		// }

	}

	@Override
	public int getStepNote(final int nStep)
	{
		int nOcatave = nStep % Notes.NumberOfNotes;

		int nNote = getIntervals()[nStep];

		// Add the number of ocatves.
		return nNote + (Notes.NumberOfNotes * nOcatave);
	}
}
