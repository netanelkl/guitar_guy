package com.mad.tunerlib.chordGeneration;

import com.mad.lib.chordGeneration.EString;
import com.mad.lib.musicalBase.Notes;
import com.mad.lib.musicalBase.OctavedNote;
import com.mad.lib.utils.ErrorHandler;
import com.mad.tunerlib.musicalBase.FretFingerPair;

public class FingerPosition
{
	public int		String;
	public int		Fret;

	static int[]	s_arFretDifference	= { 5, 5, 5, 4, 5,
			FretFingerPair.NUM_FRETS	};

	public static int sameNoteFretDifferenceOnString(	final int eCurrentString,
														final int eNextString)
	{

		int nJumpString, nMul = 1;
		if (eNextString < eCurrentString)
		{
			nJumpString = eNextString;
			nMul = -1;
		}
		else
		{
			nJumpString = eCurrentString;
		}

		if ((nJumpString < EString.Sixth)
				|| (nJumpString > EString.LastString))
		{
			Exception ex = new IndexOutOfBoundsException();
			ErrorHandler.HandleError(ex);
			return 0;
		}
		return s_arFretDifference[nJumpString] * nMul;

	}

	public static FingerPosition getFirstPosition()
	{
		return new FingerPosition(EString.Sixth, 0);

	}

	public FingerPosition(final FingerPosition other)
	{
		String = other.String;
		Fret = other.Fret;
	}

	public FingerPosition(final int eString, final int nFret)
	{
		String = eString;
		Fret = nFret;
	}

	public boolean advanceFrets(final int nNumberOfFrets)
	{
		if (nNumberOfFrets < 0)
		{
			ErrorHandler
					.HandleError(new IllegalStateException());
		}
		if (nNumberOfFrets == 0)
		{
			return true;
		}

		if (IsLastPosition())
		{
			return PreviousOctave();
		}

		int nNumberOfFretsToAdvance = nNumberOfFrets;

		while (nNumberOfFretsToAdvance != 0)
		{
			// If we are
			int nNextString =
					nNumberOfFretsToAdvance > 0 ? String + 1
							: String - 1;
			int nSameNotesDifference =
					sameNoteFretDifferenceOnString(String,
							nNextString);
			int nCurrFretsToAdvance =
					Math.min(Math.abs(nNumberOfFretsToAdvance),
							nSameNotesDifference);
			if (nSameNotesDifference > 0)
			{
				if (((Fret + nCurrFretsToAdvance) >= nSameNotesDifference)
						|| ((Fret + nCurrFretsToAdvance) < 0))
				{
					if ((nNextString < 0)
							|| (nNextString > EString.LastString))
					{
						break;
					}

					Fret =
							(Fret + nCurrFretsToAdvance + nSameNotesDifference)
									% nSameNotesDifference;
					String = nNextString;

				}
				else
				{
					Fret += nCurrFretsToAdvance;
				}
			}
			nNumberOfFretsToAdvance -= nCurrFretsToAdvance;
		}

		return true;
	}

	public boolean Equals(final FingerPosition other)
	{
		return (String == other.String) && (Fret == other.Fret);
	}

	public int getAbsouluteFretOffset()
	{
		int result = 0;
		for (int nGuitarString = EString.FirstString; nGuitarString < String; nGuitarString++)
		{
			result +=
					sameNoteFretDifferenceOnString(
							nGuitarString + 1, nGuitarString);
		}

		result += Fret;

		return result;
	}

	boolean isBefore(final FingerPosition other)
	{
		if (String == other.String)
		{
			return Fret < other.Fret;
		}

		if (String < other.String)
		{
			int nFretToCheck =
					other.Fret
							+ ((other.String - String) * Notes.NumberOfNotes);

			return Fret < nFretToCheck;
		}
		else
		{
			int nFretToCheck =
					Fret
							+ ((String - other.String) * Notes.NumberOfNotes);

			return nFretToCheck < other.Fret;
		}
	}

	boolean IsFirstPosition()
	{
		return this.Equals(FingerPosition.getFirstPosition());
	}

	boolean IsFirstPosition(final FingerPosition pos)
	{
		return pos.IsFirstPosition();
	}

	boolean isFretValid(final int nFret)
	{
		if ((nFret >= com.mad.lib.utils.Definitions.NUMBER_OF_FRETS)
				|| (nFret < 0))
		{
			return false;
		}

		return true;
	}

	boolean IsLastPosition()
	{
		return (Fret == com.mad.lib.utils.Definitions.NUMBER_OF_FRETS)
				&& (String == EString.NumberOfStrings);
	}

	boolean IsLastPosition(final FingerPosition pos)
	{
		return pos.IsLastPosition();
	}

	public boolean isValid()
	{
		if (!isFretValid(Fret))
		{
			return false;
		}

		if ((String < EString.FirstString)
				|| (String > EString.LastString))
		{
			return false;
		}

		return true;
	}

	public boolean NextOctave()
	{
		return advanceFrets(Notes.NumberOfNotes);
	}

	public boolean NextString()
	{
		if (String == EString.LastString)
		{
			return false;
		}

		int nFret =
				Fret
						+ sameNoteFretDifferenceOnString(String,
								String + 1);

		if (!isFretValid(nFret))
		{
			return false;
		}
		String++;
		Fret = nFret;

		return true;
	}

	public boolean PreviousOctave()
	{
		return advanceFrets(-1 * Notes.NumberOfNotes);
	}

	public boolean previousString()
	{
		if (String == EString.FirstString)
		{
			return false;
		}

		int nFret =
				Fret
						+ sameNoteFretDifferenceOnString(String,
								String - 1);

		if (!isFretValid(nFret))
		{
			return false;
		}

		Fret = nFret;
		String--;
		return true;
	}

	public OctavedNote getOctavedNote()
	{
		int fretsToAdd = 0;
		for (int i = 1; i < String; i++)
		{
			fretsToAdd +=
					FingerPosition
							.sameNoteFretDifferenceOnString(
									i - 1, i);
		}

		return new OctavedNote(fretsToAdd + Fret
				+ (Notes.E + (2 * Notes.NumberOfNotes)));
	}

};
