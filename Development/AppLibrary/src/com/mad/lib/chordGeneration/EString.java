package com.mad.lib.chordGeneration;

@SuppressWarnings("unused")
public class EString
{
	public static final int	Sixth			= 0;
	public static final int	Fifth			= EString.Sixth + 1;
	public static final int	Fourth			= EString.Fifth + 1;
	public static final int	Third			= EString.Fourth + 1;
	public static final int	Second			= EString.Third + 1;
	public static final int	First			= EString.Second + 1;

	public static final int	FirstString		= EString.Sixth;
	public static final int	LastString		= EString.First;

	public static final int	NumberOfStrings	= LastString + 1;
}
