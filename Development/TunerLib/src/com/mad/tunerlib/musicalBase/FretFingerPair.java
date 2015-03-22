package com.mad.tunerlib.musicalBase;

import java.util.ArrayList;

import com.mad.lib.chordGeneration.EString;
import com.mad.lib.musicalBase.FretFingerPairBase;
import com.mad.lib.musicalBase.Notes;
import com.mad.lib.musicalBase.OctavedNote;
import com.mad.tunerlib.chordGeneration.FingerPosition;

public class FretFingerPair extends FretFingerPairBase
{
	// Including an open fret.
	public static final int	NUM_FRETS	= 26;

	/**
	 * We should make this non-constant, to support non-human players ;)
	 */
	public static final int	NUM_FINGERS	= 4;

	/**
	 * The index of the fret to be played.
	 */
	// TODO: It has a finite number of values so why not enum?
	// Java enums lose their numerical value. Here the number is important,
	// Any alternative?
	public int				Fret;

	/**
	 * The index of the string to be played.
	 */
	// TODO: It has a finite number of values so why not enum?
	// Java enums lose their numerical value. Here the number is important,
	// Any alternative?
	public int				String;

	/**
	 * The index of the finger to be played.
	 */
	// TODO: It has a finite number of values so why not enum?
	// Java enums lose their numerical value. Here the number is important,
	// Any alternative?
	public int				Finger;

	public FretFingerPair()
	{

	}

	public FretFingerPair(int absouluteIndex)
	{
		// TODO: The right side should be a constant.
		absouluteIndex -= (Notes.E + (Notes.NumberOfNotes));

		if (absouluteIndex < 0)
		{
			Fret = -1;
			String = -1;
		}

		String = EString.Sixth;
		Fret = absouluteIndex;
		while (true)
		{
			if (String == EString.First)
			{
				return;
			}

			int difference =
					FingerPosition
							.sameNoteFretDifferenceOnString(
									String, String + 1);

			if (difference < Fret)
			{
				Fret -= difference;
				String++;
			}
			else
			{
				break;
			}
		}
	}

	public FretFingerPair(FretFingerPair other)
	{
		this.Finger = other.Finger;
		this.String = other.String;
		this.Fret = other.Fret;
	}

	public FretFingerPair(	int nFinger,
							FingerPosition fingerPosition)
	{
		this.Finger = nFinger;
		this.String = fingerPosition.String;
		this.Fret = fingerPosition.Fret;

	}

	public FretFingerPair(int nFinger, int nString, int nFret)
	{
		this.Finger = nFinger;
		this.String = nString;
		this.Fret = nFret;

	}

	@Override
	public int getAbsoluteNeckIndex()
	{
		int fretsToAdd = 0;
		for (int i = 1; i <= String; i++)
		{
			fretsToAdd +=
					FingerPosition
							.sameNoteFretDifferenceOnString(
									i - 1, i);
		}

		return fretsToAdd + Fret;
	}

	@Override
	public ArrayList<FretFingerPairBase> getFingerPositions()
	{
		ArrayList<FretFingerPairBase> result =
				new ArrayList<FretFingerPairBase>();
		result.add(this);
		return result;
	}

	public OctavedNote getOctavedNote()
	{
		int absoulteIndex = Fret;

		for (int stringIndex = String; stringIndex > 1; stringIndex--)
		{
			absoulteIndex +=
					FingerPosition
							.sameNoteFretDifferenceOnString(
									stringIndex - 1, stringIndex);
		}

		// TODO: The right side should be a constant.
		absoulteIndex += (Notes.E + (Notes.NumberOfNotes));

		return new OctavedNote(absoulteIndex);
	}

	@Override
	public int getNumberOfStrings()
	{
		return EString.NumberOfStrings;
	}

	@Override
	public int getFret()
	{
		return Fret;
	}

	@Override
	public int getString()
	{
		return String;
	}

	@Override
	public int getFinger()
	{
		return Finger;
	}
}
