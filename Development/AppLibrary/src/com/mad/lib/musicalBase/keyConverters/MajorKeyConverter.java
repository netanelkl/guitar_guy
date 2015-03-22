package com.mad.lib.musicalBase.keyConverters;

import com.mad.lib.musicalBase.Notes;

public class MajorKeyConverter extends BaseKeyConverter
{

	/**
	 * The note sequence in that key (major).
	 */
	static int[]	m_arNotesForKey	= { Notes.A - Notes.A,
			Notes.B - Notes.A, Notes.CSharp - Notes.A,
			Notes.D - Notes.A, Notes.E - Notes.A,
			Notes.FSharp - Notes.A, Notes.GSharp - Notes.A, };

	/**
	 * Create a new key converter object.
	 */
	public MajorKeyConverter()
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
		// TODO: For now fixed value becaus always returns the same intervals
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
		return EKeyConverterType.Major;
	}
}
