package com.mad.guitarteacher.practice.Scenarios;

import com.mad.guitarteacher.practice.ExerciseStageOption;
import com.mad.guitarteacher.practice.ExerciseTypes;
import com.mad.guitarteacher.practice.Acts.DisplayInformationAct;
import com.mad.guitarteacher.practice.Acts.NoteSequencePlayAct;
import com.mad.lib.display.pager.PagerPage;
import com.mad.lib.display.pager.PagerPageCollection;
import com.mad.lib.musicalBase.Notes;
import com.mad.lib.musicalBase.OctavedNote;

/**
 * The position scenario teaches to play an octave in that position.
 * 
 * 1. The parameters for this scenario would be either 1 2 3 or 4, for 1st 2nd
 * 3rd and 4th position. 2. You should add the NoteSequencePlayAct to play C D E
 * F G A B on that position.
 * 
 * @author Nati
 * 
 */
public class PositionScenario extends ExerciseScenario
{
	public PositionScenario()
	{
		super(ExerciseTypes.eExerciseTypes_Introduction);
	}

	@Override
	public boolean init(final String[] arParams,
						final ExerciseStageOption parent)
	{
		super.init(arParams, parent);
		if (arParams.length != 1)
		{
			return false;
		}

		String strPosition = arParams[0];
		int nPosition = 0;
		if (strPosition.equals("0"))
		{
			nPosition = 0;
		}
		else if (strPosition.equals("1"))
		{
			nPosition = 1;
		}
		else if (strPosition.equals("2"))
		{
			nPosition = 2;
		}
		else if (strPosition.equals("3"))
		{
			nPosition = 3;
		}
		else
		{
			return false;
		}
		PagerPageCollection stageInformation =
				new PagerPageCollection();
		stageInformation
				.addPage(new PagerPage(	"Position playing",
										"You're gonna play the first position..."));

		DisplayInformationAct displayInfoAct =
				new DisplayInformationAct(stageInformation);
		addAct(displayInfoAct);

		NoteSequencePlayAct noteSequenceAct =
				new NoteSequencePlayAct(this);
		// TODO: Tom, I know this is supposed to be only A-G, without the
		// sharps.
		// help with that too.
		for (int nNoteIndex = 0; nNoteIndex < Notes.NumberOfNotes; nNoteIndex++)
		{
			// OctavedNote note = OctavedNote.
			// TODO: Tom, help with this.
			noteSequenceAct.addNote(new OctavedNote(nNoteIndex,
													0,
													nPosition));
		}

		addAct(noteSequenceAct);
		return true;
	}
}
