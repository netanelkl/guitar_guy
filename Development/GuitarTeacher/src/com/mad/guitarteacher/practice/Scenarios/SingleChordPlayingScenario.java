package com.mad.guitarteacher.practice.Scenarios;

import com.mad.guitarteacher.display.userInformation.ChordInformationDisplayer;
import com.mad.lib.musicalBase.chords.Chord;
import com.mad.guitarteacher.practice.ExerciseStageFactory;
import com.mad.guitarteacher.practice.ExerciseStageOption;
import com.mad.guitarteacher.practice.ExerciseTypes;
import com.mad.guitarteacher.practice.Acts.ChordSequencePlayAct;
import com.mad.guitarteacher.practice.Acts.DisplayInformationAct;

/**
 * This scenario handles the scenario of playing one chord.
 * 
 * This scenario should play the following sequence (by adding acts):
 * 
 * 1. play the correct 1st finger note and let the user play it (by operating
 * NoteSequencePlayAct). 2. Do so for all fingers. 3. Do so for all Strings one
 * after the other. 4. Play the chord altogether a few times.
 * 
 * @author Nati
 * 
 */
public class SingleChordPlayingScenario extends ExerciseScenario
{

	public SingleChordPlayingScenario()
	{
		super(ExerciseTypes.eExerciseTypes_SingleChord);
	}

	static
	{
		ExerciseStageFactory.register(
				ExerciseTypes.eExerciseTypes_SingleChord,
				SingleChordPlayingScenario.class);
	}

	@Override
	public boolean init(final String[] arParams,
						final ExerciseStageOption parent)
	{
		super.init(arParams, parent);

		// Check if there is a specified way.
		if (arParams.length != 1)
		{
			return false;
		}

		Chord chord = Chord.parse(arParams[0]);

		if (chord == null)
		{
			return false;
		}

		addAct(new DisplayInformationAct(new ChordInformationDisplayer(chord)));
		// addAct(new NoteSequencePlayAct(chord.getComposingNotes()));

		addAct(new ChordSequencePlayAct(this,
										new Chord[] { chord }));

		return true;
	}
}
