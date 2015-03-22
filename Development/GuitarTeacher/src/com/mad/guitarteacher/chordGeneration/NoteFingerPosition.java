package com.mad.guitarteacher.chordGeneration;

import com.mad.lib.musicalBase.Notes;
import com.mad.tunerlib.chordGeneration.FingerPosition;

public class NoteFingerPosition
{
	public FingerPosition	fingerPosition;
	public int				Note;

	public NoteFingerPosition(FingerPosition cPosition, int eNote)
	{
		fingerPosition = new FingerPosition(cPosition);
		Note = eNote;
	}

	public NoteFingerPosition(NoteFingerPosition other)
	{
		fingerPosition =
				new FingerPosition(other.fingerPosition);
		Note = other.Note;
	}

	public boolean advanceFrets(int frets)
	{
		if (!fingerPosition.advanceFrets(frets))
		{
			return false;
		}

		if (frets > 0)
		{
			if ((fingerPosition.Fret + FingerPosition
					.sameNoteFretDifferenceOnString(
							fingerPosition.String,
							fingerPosition.String + 1)) >= 0)
			{
				if (!fingerPosition.NextString())
				{
					return fingerPosition.PreviousOctave();
				}
			}
		}
		else
		{
			if ((fingerPosition.Fret % FingerPosition
					.sameNoteFretDifferenceOnString(
							fingerPosition.String,
							fingerPosition.String - 1)) == 0)
			{
				// Can fail.
				fingerPosition.previousString();
			}
		}

		int nCurrNote = Note;
		int nNumOfNotes = Notes.NumberOfNotes;

		// Advance notes
		Note =
				((nNumOfNotes + ((nCurrNote + frets) % nNumOfNotes)) % nNumOfNotes);

		return true;
	}

	public boolean nextString()
	{
		return fingerPosition.NextString();
	}

	public boolean previousString()
	{
		return fingerPosition.previousString();
	}
};
