package com.mad.lib.musicalBase;

public abstract class FretFingerPairBase implements
		IHandPositioning, Comparable<FretFingerPairBase>
{
	/**
	 * The index of the fret to be played.
	 */
	// TODO: It has a finite number of values so why not enum?
	// Java enums lose their numerical value. Here the number is important,
	// Any alternative?
	public abstract int getFret();

	/**
	 * The index of the string to be played.
	 */
	// TODO: It has a finite number of values so why not enum?
	// Java enums lose their numerical value. Here the number is important,
	// Any alternative?
	public abstract int getString();

	public abstract int getAbsoluteNeckIndex();

	/**
	 * The index of the finger to be played.
	 */
	// TODO: It has a finite number of values so why not enum?
	// Java enums lose their numerical value. Here the number is important,
	// Any alternative?
	public abstract int getFinger();

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof FretFingerPairBase)
		{
			FretFingerPairBase other = (FretFingerPairBase) o;

			return (other.getFinger() == this.getFinger())
					&& (other.getString() == this.getString())
					&& (other.getFret() == this.getFret());
		}

		return false;
	};

	@Override
	public int compareTo(FretFingerPairBase another)
	{
		return getString() - another.getString();
	}
}
