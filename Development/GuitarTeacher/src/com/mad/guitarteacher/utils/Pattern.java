package com.mad.guitarteacher.utils;

import java.util.ArrayList;

/**
 * This class represents a recurring pattern.
 * 
 * @author Tom
 * 
 * @param <patternUnit>
 */
public abstract class Pattern<patternUnit>
{
	int						m_patternIndex;
	int						m_patternRoundsCounter;

	// The pattern.
	ArrayList<patternUnit>	m_pattern;

	/**
	 * Create a new pattern object.
	 */
	protected Pattern()
	{
		m_patternIndex = 0;
		m_patternRoundsCounter = 0;

		m_pattern = new ArrayList<patternUnit>();
	}

	/**
	 * Get the next pattern object.
	 * 
	 * @return pattern object.
	 */
	public patternUnit getNext()
	{
		patternUnit result = m_pattern.get(m_patternIndex);

		m_patternIndex++;

		// Check if a whole round of the pattern went by.
		if (m_patternIndex == m_pattern.size())
		{
			m_patternIndex = 0;
			m_patternRoundsCounter++;
		}

		return result;
	}

	/**
	 * Get the index of the current object in the pattern.
	 * 
	 * @return Index of the current pattern object.
	 */
	public int getPatternIndex()
	{
		return m_patternIndex;
	}

	/**
	 * Get the number of times the pattern was advanced back to it's start.
	 * 
	 * @return Number of pattern rounds.
	 */
	public int getPatternRounds()
	{
		return m_patternRoundsCounter;
	}
}
