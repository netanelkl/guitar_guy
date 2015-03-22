package com.mad.tunerlib.chordGeneration;

import com.mad.lib.chordGeneration.EString;

/**
 * This class is representing a position on the guitar.
 * 
 * @author Tom
 * 
 */
public class ScalePosition
{
	int[][]	POSITION;

	int		m_stringJump;

	public int getIndex(FingerPosition fingerPosition)
	{
		return getIndex(fingerPosition.String,
				fingerPosition.Fret);
	}

	public int getIndex(int string, int fret)
	{
		if (string > EString.LastString)
		{
			return -1;
		}
		if (fret >= m_stringJump)
		{
			return -1;
		}
		return POSITION[string][fret];
	}

	/**
	 * Create a new instance of the scale position.
	 * 
	 * @param arPositions
	 *            - Position of the hand.
	 * @param nStringJump
	 *            - The most frets that are different notes on a string before
	 *            the note can be played on a different string.
	 */
	public ScalePosition(int[][] arPositions, int nStringJump)
	{
		POSITION = arPositions.clone();
		m_stringJump = nStringJump;
	}

	/**
	 * Create a new instance of the scale position.
	 * 
	 * @param arIntervals
	 *            - Intervals of the key.
	 */
	public ScalePosition(int[] arIntervals)
	{
		int lastString = EString.Sixth;
		m_stringJump = 0;

		// Get the max jump of a string.
		for (int string = EString.Fifth; string < EString.First; string++)
		{
			int nCurrJump =
					FingerPosition
							.sameNoteFretDifferenceOnString(
									string, lastString);

			if (nCurrJump > m_stringJump)
			{
				m_stringJump = nCurrJump;
			}

			lastString = string;
		}

		POSITION =
				new int[EString.NumberOfStrings][m_stringJump];

		// Go through the intervals.
		FingerPosition currPosition =
				FingerPosition.getFirstPosition();

		// Signal the first finger.
		POSITION[currPosition.String][currPosition.Fret] =
				Fingers.First;

		int nIntervalIndex = 0;
		int nPlacedIndex = Fingers.First;

		int nCurrentIndex = 0;
		int nLastIndex = 0;
		int nFretsToAdvance =
				arIntervals[nIntervalIndex % arIntervals.length];

		// While we still have string.
		while (currPosition.advanceFrets(nFretsToAdvance))
		{
			int nDifferenceForNextString =
					FingerPosition
							.sameNoteFretDifferenceOnString(
									currPosition.String,
									currPosition.String + 1);

			// Check if we need to advance a string.
			while ((currPosition.Fret + nDifferenceForNextString) >= 0)
			{
				// Check if we are done.
				if (currPosition.String == EString.First)
				{
					return;
				}

				// Advance the string.
				currPosition.Fret += nDifferenceForNextString;
				currPosition.String++;

				nDifferenceForNextString =
						FingerPosition
								.sameNoteFretDifferenceOnString(
										currPosition.String,
										currPosition.String + 1);

				nPlacedIndex = 0;
			}

			nIntervalIndex++;
			nLastIndex = nCurrentIndex;
			nCurrentIndex = currPosition.Fret % 5;

			nPlacedIndex =
					placeFinger(currPosition, nPlacedIndex,
							nCurrentIndex, nLastIndex);

			// Get number of frets to advance.
			nFretsToAdvance =
					arIntervals[nIntervalIndex
							% arIntervals.length];
		}
	}

	/**
	 * @param currPosition
	 * @param nPlacedIndex
	 * @param nCurrentIndex
	 * @param nLastIndex
	 * @return
	 */
	private int placeFinger(FingerPosition currPosition,
							int nPlacedIndex,
							int nCurrentIndex,
							int nLastIndex)
	{
		// Place the finger.
		if (nCurrentIndex == 0)
		{
			nPlacedIndex = Fingers.First;
		}
		else if (nCurrentIndex == 1)
		{
			if (nPlacedIndex == Fingers.First)
			{
				nPlacedIndex = Fingers.Second;
			}
			else
			{
				nPlacedIndex = Fingers.First;
			}
		}
		else if (nCurrentIndex == 2)
		{
			nPlacedIndex = Fingers.Third;
		}
		else if (nCurrentIndex == 3)
		{
			if (nPlacedIndex == Fingers.First)
			{
				if ((nCurrentIndex - nLastIndex) > 2)
				{
					nPlacedIndex = Fingers.Fourth;
				}
				else
				{
					nPlacedIndex = Fingers.Third;
				}
			}
			else if (nPlacedIndex == Fingers.Third)
			{
				nPlacedIndex = Fingers.Fourth;
			}
			else
			{
				nPlacedIndex = Fingers.Third;
			}
		}
		else if (nCurrentIndex == 4)
		{
			nPlacedIndex = Fingers.Fourth;
		}
		else
		{
			// TODO: Error.
		}

		POSITION[currPosition.String][currPosition.Fret] =
				nPlacedIndex;
		return nPlacedIndex;
	}
}
