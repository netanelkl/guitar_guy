package com.mad.lib.musicalBase;

public class Notes
{
	public static final int		A							= 0;
	public static final int		ASharp						=
																	A + 1;
	public static final int		BBemol						=
																	ASharp;
	public static final int		B							=
																	ASharp + 1;
	public static final int		CBemol						= B;
	public static final int		C							=
																	B + 1;
	public static final int		BSharp						= C;
	public static final int		CSharp						=
																	C + 1;
	public static final int		DBemol						=
																	CSharp;
	public static final int		D							=
																	CSharp + 1;
	public static final int		DSharp						=
																	D + 1;
	public static final int		EBemol						=
																	DSharp;
	public static final int		E							=
																	DSharp + 1;
	public static final int		FBemol						= E;
	public static final int		F							=
																	E + 1;
	public static final int		ESharp						= F;
	public static final int		FSharp						=
																	F + 1;
	public static final int		GBemol						=
																	FSharp;
	public static final int		G							=
																	FSharp + 1;
	public static final int		GSharp						=
																	G + 1;
	public static final int		ABemol						=
																	GSharp;
	public static final int		NumberOfNotes				=
																	ABemol + 1;

	private static final int	FIRST_GUITAR_NOTE			= 20;
	public static final int		MIDDLE_A_NOTE				= 49;
	public static final int		NUM_GUITAR_NOTES			= 51;
	public static final int		FIRST_ABSOLUTE_GUITAR_INDEX	=
																	(FIRST_GUITAR_NOTE - Notes.E)
																			+ Notes.C;

}
