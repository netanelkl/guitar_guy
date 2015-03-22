package com.mad.lib.tuning;

import com.mad.lib.musicalBase.Notes;

/**
 * Class for receiving the closest "Whole" note to a certain frequency.
 * 
 * @author Tom
 * 
 */
public class FrequencyNormalizer
{
	private static FrequencyNormalizer	s_DefaultNormalizer;

	/**
	 * Inner frequency of the middle A.
	 */
	double								m_middleAFrequency;

	/**
	 * Array of most frequencies.
	 */
	float[]								m_arFrequencies;

	/**
	 * Create a new instance of the FrequencyNormalizer object.
	 * 
	 * @param middleAFrequency
	 *            - The frequency of the middle A.
	 * @param middleAIndex
	 *            - The index of the middle A in the array of possible input
	 *            indexes.
	 */
	public FrequencyNormalizer(	final double middleAFrequency,
								final int middleAIndex)
	{
		m_middleAFrequency = middleAFrequency;

		m_arFrequencies = new float[middleAIndex * 2];

		double twelveRootTwo = Math.pow(2, 1.0 / 12);

		// Create the frequencies.
		for (int i = 0; i < m_arFrequencies.length; i++)
		{
			m_arFrequencies[i] =
					(float) (m_middleAFrequency * Math.pow(
							twelveRootTwo, i - middleAIndex));
		}
	}

	public static FrequencyNormalizer createDefaultNormalizer()
	{
		if (s_DefaultNormalizer == null)
		{
			s_DefaultNormalizer =
					new FrequencyNormalizer(440,
											Notes.MIDDLE_A_NOTE
													- Notes.FIRST_ABSOLUTE_GUITAR_INDEX);
		}
		return s_DefaultNormalizer;
	}

	/**
	 * Get the closest normalized frequency.
	 * 
	 * @param frequency
	 * @return closest normalized frequency.
	 */
	public synchronized float getClosestNoteNormalizedFrequency(final double frequency)
	{
		return m_arFrequencies[getClosestNote(frequency)];
	}

	public int getClosestNote(final double frequency)
	{
		int nFreqIndex;
		for (nFreqIndex = 1; nFreqIndex < m_arFrequencies.length; nFreqIndex++)
		{
			double currFrequency = m_arFrequencies[nFreqIndex];

			if (currFrequency > frequency)
			{
				break;
			}
		}

		if (nFreqIndex >= m_arFrequencies.length)
		{
			return m_arFrequencies.length - 1;
		}

		// Now we have the two frequencies.
		int lowerFreqIndex = nFreqIndex - 1, higherFreqIndex =
				nFreqIndex;
		double lowerFreqDifference =
				frequency - m_arFrequencies[lowerFreqIndex];
		double higherFreqDifference =
				m_arFrequencies[higherFreqIndex] - frequency;

		// Now check which is closer.
		if (higherFreqDifference > lowerFreqDifference)
		{
			return lowerFreqIndex;
		}
		else
		{
			return higherFreqIndex;
		}
	}

	/**
	 * 
	 */
	public float getFrequencyByNoteIndex(final int nAbsoluteNoteIndex)
	{
		if ((nAbsoluteNoteIndex < 0)
				|| (nAbsoluteNoteIndex >= m_arFrequencies.length))
		{
			return 0;
		}

		// We are adding Notes.C because our array is set for global generic
		// indexes, not
		// absolute indexes. Those differ by Notes.C from the absoluteIndex.
		return m_arFrequencies[nAbsoluteNoteIndex];
	}
}
