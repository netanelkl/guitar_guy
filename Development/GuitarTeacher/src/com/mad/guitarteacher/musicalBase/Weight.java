package com.mad.guitarteacher.musicalBase;

/**
 * This class represents the musical weight x/y.
 * 
 * @author Tom
 */
public class Weight
{
	private byte m_bDenominator;
	private byte m_bNumerator;

	/**
	 * Create a new instance of the weight class.
	 * @param numerator - Numerator of the musical 
	 * 					  weight (x from the x/y).
	 * @param denominator - Denominator of the 
	 * 						musical weight (y from the x/y).
	 */
	public Weight(byte numerator, byte denominator)
	{
		m_bDenominator = denominator;
		m_bNumerator = numerator;
	}
	
	/**
	 * The musical weight denominator.
	 * @return The musical weight denominator (y from x/y).
	 */
	public byte getDenominator()
	{
		return m_bDenominator;
	}

	/**
	 * The musical weight numerator.
	 * @return The musical weight numerator (x from the x/y).
	 */
	public byte getNumerator()
	{
		return m_bNumerator;
	}
}
