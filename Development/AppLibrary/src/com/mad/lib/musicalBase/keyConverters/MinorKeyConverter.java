package com.mad.lib.musicalBase.keyConverters;

import com.mad.lib.musicalBase.Notes;

public class MinorKeyConverter extends BaseKeyConverter
{

	/**
	 * The note sequence in that key (major).
	 */
	static int[]	m_arNotesForKey	= { Notes.A - Notes.A,
			Notes.B - Notes.A, Notes.C - Notes.A,
			Notes.D - Notes.A, Notes.E - Notes.A,
			Notes.F - Notes.A, Notes.G - Notes.A, };

	/**
	 * Create a new key converter object.
	 */
	public MinorKeyConverter()
	{
		super();
	}

	@Override
	public int[] getIntervals()
	{
		return m_arNotesForKey;
	}

	@Override
	public int[] getNoteTable(final int nKey)
	{
		// TODO: For now fixed value because always returns the same intervals
		// (from the start note).
		return m_arKeyTable[Notes.C - Notes.A];
	}

	@Override
	public int getNumberOfNotes()
	{
		return 7;
	}

	@Override
	public EKeyConverterType getType()
	{
		return EKeyConverterType.Minor;
	}
}
