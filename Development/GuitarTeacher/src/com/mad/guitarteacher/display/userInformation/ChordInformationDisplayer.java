package com.mad.guitarteacher.display.userInformation;

import com.mad.lib.display.pager.PagerPageCollection;
import com.mad.lib.musicalBase.Notes;
import com.mad.lib.musicalBase.OctavedNote;
import com.mad.lib.musicalBase.chords.Chord;

/**
 * Class to provide information on a chord.
 * 
 * @author Tom
 * 
 */
public class ChordInformationDisplayer extends
		PagerPageCollection
{
	/**
	 * Create a new instance of the ChordInformationDisplayer.
	 * 
	 * @param chord
	 *            - Chord to display information of.
	 */
	public ChordInformationDisplayer(Chord chord)
	{
		super();

		String title =
				"Introducing the " + chord.getName() + " chord";
		String toDisplay =
				"You will now learn to play the "
						+ chord.getName() + " chord";

		addPage(title, toDisplay);

		String strNotes = "";

		for (OctavedNote note : chord.getComposingNotes())
		{
			strNotes += note.toString() + " ";
		}

		toDisplay =
				"The "
						+ chord.getName()
						+ " chord is compunded of the following notes: "
						+ strNotes;

		addPage(title, toDisplay);

		String strComposingIntervals = "0-";

		OctavedNote last = null;

		// Go through the notes.
		for (OctavedNote note : chord.getComposingNotes())
		{
			if (last != null)
			{
				// Get the interval.
				int interval =
						note.getAbsoluteIndex()
								- last.getAbsoluteIndex();

				// Rotate the interval cyclic.
				interval =
						((interval + Notes.NumberOfNotes) % Notes.NumberOfNotes);

				strComposingIntervals += interval + "-";
			}
			last = note;
		}

		toDisplay =
				"It is a "
						+ chord.getChordType().toString()
						+ " chord, meaning that its composing intervals are "
						+ strComposingIntervals;

		addPage(title, toDisplay);

		toDisplay =
				"Let's play the strings that "
						+ "compose the chord one by one.";

		addPage(title, toDisplay);

		// TODO: Picture of hand with finger numbers on it.
		toDisplay =
				"A revolving circle indicates the fret and string to"
						+ " be played and the number represents the finger.";

		addPage(title, toDisplay);
	}
}
