package com.mad.lib.utils;

public class HelperMethods
{
	static char[]	s_arSplitChars	= new char[] { '|', ',' };

	public static String[] parseParameters(String params)
	{

		return parseParameters(params, 0);
	}

	public static String[] parseParameters(	String params,
											int nSeperationLevel)
	{

		if ((params == null)
				|| (nSeperationLevel >= s_arSplitChars.length))
		{
			return null;
		}

		return params.split("\\"
				+ s_arSplitChars[nSeperationLevel]);
	}

	static int[]	s_arPows	= new int[] { 1, 10, 100, 1000,
			10000				};

	public static TimeSpan dateDiff(long start, long end)
	{
		return new TimeSpan(start, end);
	}

	public static float round(float sfFloat, int digits)
	{
		// Avoiding pow calcs.
		int multi;
		if (digits < s_arPows.length)
		{
			multi = s_arPows[digits];
		}
		else
		{
			multi = (int) Math.pow(10, digits);
		}

		return (float) Math.round(sfFloat * multi) / multi;
	}
}
