package com.mad.lib.musicalBase.chords;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChordParameters
{

	// TODO: Missing stuff like, position,
	// a boolean indicator for bare',
	// and additional notes, like the seventh if
	// the user wants it to be added to the chord.
	/**
	 * Members
	 */
	private final int			m_nRootNote;

	private final EChordType	m_eChordType;

	public static ChordParameters parse(String name)
	{
		String notes = "^([CDEFGAB])";
		String accidentals = "(#+|b+)?";
		String chords = "(maj|min|sus|aug|dim)?";
		// String conversion = "({6}|{4/6})?";
		String addedNotesPattern = "(/d+)?";

		String regex =
				notes + accidentals + chords + addedNotesPattern;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(name);
		System.out.println("regex is " + regex);
		if (matcher.find())
		{

			String note = matcher.group(1);

			// Make sure base note found.
			if ((note == null) || (note.length() != 1))
			{
				return null;
			}

			// Convert to integer note index.
			int nNoteIndex = note.charAt(0) - 'A';
			// get the proper half tone index: B and E only have one half tone.
			nNoteIndex *= 2;
			if (nNoteIndex > 8)
			{
				nNoteIndex -= 2;
			}
			else if (nNoteIndex > 2)
			{
				nNoteIndex -= 1;
			}

			// calc the incline.
			int nIncline = 0;
			String strIncline = matcher.group(2);
			if (strIncline != null)
			{
				if (strIncline.charAt(0) == '#')
				{
					nIncline += strIncline.length();
				}
				if (strIncline.charAt(0) == 'b')
				{
					nIncline -= strIncline.length();
				}
			}
			nNoteIndex += nIncline;

			// Add the chord type.
			String strChordType = matcher.group(3);
			EChordType eChordType = EChordType.maj;
			if (strChordType != null)
			{
				if (strChordType.equalsIgnoreCase("aug"))
				{
					eChordType = EChordType.aug;
				}
				else if (strChordType.equalsIgnoreCase("dim"))
				{
					eChordType = EChordType.dim;
				}
				else if (strChordType.equalsIgnoreCase("min"))
				{
					eChordType = EChordType.min;
				}
			}

			// String addedNotes = matcher.group(4);
			// TODO: add those.

			return new ChordParameters(nNoteIndex, eChordType);
		}
		return null;
	}

	public ChordParameters(	int nRootNoteIndex,
							EChordType eChordType)
	{
		m_nRootNote = nRootNoteIndex;
		m_eChordType = eChordType;
	}

	/**
	 * Returns the Root Note for the chord.
	 * 
	 * @return ENote, The root note of the chord.
	 */
	public int getRootNote()
	{
		return m_nRootNote;
	}

	public EChordType getChordType()
	{
		return m_eChordType;
	}
}
