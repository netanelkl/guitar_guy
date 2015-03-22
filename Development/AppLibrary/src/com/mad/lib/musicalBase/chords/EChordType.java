package com.mad.lib.musicalBase.chords;

import com.mad.lib.musicalBase.keyConverters.EKeyConverterType;

// TODO: Shouldnt we have a class that will "figure out"
// the type from the notes?
public enum EChordType
{
	maj,
	min,
	aug,
	dim;

	// TODO: Should this stay static or should we just let the
	// enum values have a key converter value as a member and
	// put it as input on the ctor.
	public static EKeyConverterType getKeyTypeForChordType(EChordType eChordType)
	{
		// TODO: Validate chord type.
		switch (eChordType)
		{
			case aug:
				return EKeyConverterType.Augmented;
			case dim:
				return EKeyConverterType.Diminished;
			case maj:
				return EKeyConverterType.Major;
			case min:
				return EKeyConverterType.Minor;
			default:
				throw new EnumConstantNotPresentException(	EChordType.class,
															eChordType
																	.toString());
		}
	}
}
