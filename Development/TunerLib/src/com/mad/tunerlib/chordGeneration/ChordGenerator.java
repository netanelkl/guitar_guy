package com.mad.tunerlib.chordGeneration;

import java.util.ArrayList;

import com.mad.lib.R;
import com.mad.lib.chordGeneration.IChordGenerator;
import com.mad.lib.musicalBase.IHandPositioning;
import com.mad.lib.musicalBase.chords.Chord;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.lib.utils.ErrorHandler;

/**
 * This class generates chords, it is the chord generator.
 * 
 * @author Tom
 * 
 */
public class ChordGenerator implements IChordGenerator
{

	/**
	 * Create a new instance of the ChordGenerator object.
	 */
	public ChordGenerator()
	{
	}

	@Override
	public IHandPositioning getChordHandPositioning(final Chord chord,
													final int lastScale)
	{
		int[] lstNotes = { 4, 7, 12, 16, 19, 24, 28 };
		IHandPositioning cHand =
				getFingerPosition(lstNotes, lastScale);
		return cHand;
	}

	// public IHandPositioning getFingerPosition(int nStartingNote, int[]
	// lstNotes)
	// {
	// return getFingerPosition(nStartingNote, lstNotes, null);
	// }

	// public IHandPositioning getFingerPosition(int nStartingNote,
	// int[] lstNotes,
	// FingerPosition lastPosition)
	// {
	// NoteFingerPosition posStart = getNextPositionForNote(nStartingNote,
	// lastPosition);
	//
	// int nFretOffset = posStart.fingerPosition.getAbsouluteFretOffset();
	//
	// for (int i : lstNotes)
	// {
	// i += nFretOffset;
	// }
	//
	// return getFingerPosition(lstNotes, lastPosition);
	// }

	public IHandPositioning getFingerPosition(	final int[] lstNotes,
												final FingerPosition lastPosition,
												final int lastScale)
	{
		// Get the first position for the starting note.
		ArrayList<FingerPosition> fingerPositions =
				new ArrayList<FingerPosition>();

		/*
		 * FingerPosition currPos; FingerPosition posStart = new
		 * FingerPosition(Globals.s_posFirstGuitarPosition);
		 * 
		 * // Now get all the rest. for (int nNoteIndex = 0; nNoteIndex <
		 * lstNotes.length; nNoteIndex++) { // Get the next position.W currPos =
		 * new FingerPosition(posStart);
		 * 
		 * if (!currPos.advanceFrets(lstNotes[nNoteIndex])) { break; // throw
		 * fingerPositions.add(currPos); }
		 */
		FingerPosition currPos =
				FingerPosition.getFirstPosition();

		// Now get all the rest.
		for (int nNoteIndex = 0, nNotesToAdvance = lstNotes[0]; nNoteIndex < lstNotes.length; nNoteIndex++)
		{
			// Get the next position.W
			if (nNoteIndex > 0)
			{
				nNotesToAdvance =
						lstNotes[nNoteIndex]
								- lstNotes[nNoteIndex - 1];
			}
			if (!currPos.advanceFrets(nNotesToAdvance))
			{
				break;
			}
			fingerPositions.add(new FingerPosition(currPos));
		}

		GuitarPositionManager positionManager =
				AppLibraryServiceProvider.getInstance().get(
						R.service.position_manager);

		ArrayList<IHandPositioning> fingerPositionMaps =
				positionManager.getFingersForPostions(
						fingerPositions, lastScale);

		if ((fingerPositionMaps == null)
				|| (fingerPositionMaps.size() == 0))
		{
			ErrorHandler.HandleError(new NullPointerException());
			return null;
		}

		return fingerPositionMaps.get(0);
	}

	@Override
	public IHandPositioning getFingerPosition(	final int[] lstNotes,
												final int lastScale)
	{
		return getFingerPosition(lstNotes, null, lastScale);
	}
}
