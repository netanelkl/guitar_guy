package com.mad.tunerlib.musicalBase;

import java.util.ArrayList;

import com.mad.lib.musicalBase.FretFingerPairBase;
import com.mad.lib.musicalBase.IHandPositioning;
import com.mad.lib.musicalBase.OctavedNote;

public class BasicHandPositioning implements IHandPositioning
{
	private ArrayList<FretFingerPairBase>	m_arBasicFingerPositions;

	@SuppressWarnings("unchecked")
	public BasicHandPositioning(IHandPositioning other)
	{
		m_arBasicFingerPositions =
				(ArrayList<FretFingerPairBase>) other
						.getFingerPositions().clone();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof BasicHandPositioning)
		{
			BasicHandPositioning other =
					(BasicHandPositioning) o;
			for (FretFingerPairBase fretFingerPair : other
					.getFingerPositions())
			{
				if (!getFingerPositions().contains(
						fretFingerPair))
				{
					return false;
				}
			}

			return true;
		}

		return false;
	};

	/**
	 * Create a new instance of the BasicHandPositioning object.
	 * 
	 * @param positions
	 *            - Fret finger pair list.
	 */
	@SuppressWarnings("unchecked")
	public BasicHandPositioning(ArrayList<FretFingerPairBase> positions)
	{
		m_arBasicFingerPositions =
				(ArrayList<FretFingerPairBase>) positions
						.clone();
	}

	/**
	 * Returns the finger positions.
	 * 
	 * @return FretFingerPair[] array of finger fret positions and finger.
	 */
	@Override
	public ArrayList<FretFingerPairBase> getFingerPositions()
	{
		return m_arBasicFingerPositions;
	}

	public static BasicHandPositioning load(OctavedNote e)
	{
		// TODO: Implement.
		return null;
	}

	/**
	 * Loads a list of strings played together.
	 * 
	 * For example: A42 will play the 0 Fret (A) on the 4th string with the
	 * second finger, while A4132 will play A on the 4th and 3rd string on the
	 * 1st and 2nd fingers.
	 * 
	 * @param strFingering
	 */
	public boolean load(String strFingering)
	{
		int nLength = strFingering.length();
		if ((nLength != 3) && (nLength != 5))
		{
			return false;
		}

		// Parse fret.
		int nFret = asciiLetterIndex(strFingering.charAt(0));

		int nString, nFinger = -1;

		m_arBasicFingerPositions =
				new ArrayList<FretFingerPairBase>(nLength - 1);

		for (int i = 0; i < ((nLength - 1) / 2); i++)
		{
			// Parse string
			if (((nString =
					asciiToInteger(strFingering
							.charAt((i * 2) + 1))) != -1)
					&& ((nFinger =
							asciiToInteger(strFingering
									.charAt((i * 2) + 2))) != -1))
			{
				return false;
			}

			m_arBasicFingerPositions
					.add(new FretFingerPair(nFret,
											nString,
											nFinger));

		}
		return true;
	}

	private static int asciiToInteger(char chFret)
	{
		if ((chFret < '0') || (chFret > '9'))
		{
			return -1;
		}
		return chFret - '0';
	}

	private static int asciiLetterIndex(char chFret)
	{
		if ((chFret > 'A') && (chFret < 'Z'))
		{
			return chFret - 'A';
		}
		else if ((chFret > 'a') && (chFret < 'z'))
		{
			return chFret - 'a';
		}
		else
		{
			return -1;
		}
	}

	@Override
	public int getNumberOfStrings()
	{
		// TODO Auto-generated method stub
		return 6;
	}
}
