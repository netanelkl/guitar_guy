package com.mad.guitarteacher.practice.Scenarios;

import java.util.ArrayList;

import com.mad.guitarteacher.practice.ExerciseStageFactory;
import com.mad.guitarteacher.practice.ExerciseStageOption;
import com.mad.guitarteacher.practice.ExerciseTypes;
import com.mad.guitarteacher.practice.Acts.FixedPositionSequencePlayAct;
import com.mad.lib.chordGeneration.EString;
import com.mad.lib.musicalBase.FretFingerPairBase;
import com.mad.tunerlib.musicalBase.BasicHandPositioning;
import com.mad.tunerlib.musicalBase.FretFingerPair;

/**
 * This scenario will display the right acts in order to learn to play a barre.
 * 
 * @author Tom
 * 
 */
public class BarreScenario extends ExerciseScenario
{
	public BarreScenario()
	{
		super(ExerciseTypes.eExerciseTypes_Barre);
	}

	static
	{
		ExerciseStageFactory.register(
				ExerciseTypes.eExerciseTypes_Barre,
				BarreScenario.class);
	}

	@Override
	public boolean init(final String[] arParams,
						final ExerciseStageOption parent)
	{
		if (!super.init(arParams, parent))
		{
			return false;
		}

		if (arParams.length != 1)
		{

			return false;
		}
		// Create the act to be played.
		FixedPositionSequencePlayAct fixedPositionSequencePlayAct =
				new FixedPositionSequencePlayAct(this);

		// Extract the fret of the barre act.
		String strPosition = arParams[0];
		int nActFret = Integer.parseInt(strPosition);

		// Create an array for the barre position
		// TODO: Move this to a class, all this logic should be concealed.
		ArrayList<FretFingerPairBase> arBare =
				new ArrayList<FretFingerPairBase>();

		// Go through the arrays to create the barre position.
		for (int curretString = 0; curretString < EString.NumberOfStrings; curretString++)
		{
			arBare.add(new FretFingerPair(	1,
											curretString,
											nActFret));
		}

		// Go through the strings.
		// TODO: This shouldn't be here! This should all be concealed
		// in a class!
		for (int stringIndex = 0; stringIndex < EString.NumberOfStrings; stringIndex++)
		{
			FretFingerPair fretFingerPair1 =
					new FretFingerPair(	2,
										stringIndex,
										nActFret + 1);
			FretFingerPair fretFingerPair2 =
					new FretFingerPair(1, stringIndex, nActFret);
			FretFingerPair fretFingerPair3 =
					new FretFingerPair(	2,
										stringIndex,
										nActFret + 1);

			BasicHandPositioning tempBarreHandPositioning =
					new BasicHandPositioning(arBare);

			fixedPositionSequencePlayAct
					.addHandPosition(fretFingerPair1);
			fixedPositionSequencePlayAct
					.addHandPosition(fretFingerPair2);
			fixedPositionSequencePlayAct
					.addHandPosition(fretFingerPair3);
			fixedPositionSequencePlayAct
					.addHandPosition(tempBarreHandPositioning);
		}
		addAct(fixedPositionSequencePlayAct);
		return true;
	}
}
