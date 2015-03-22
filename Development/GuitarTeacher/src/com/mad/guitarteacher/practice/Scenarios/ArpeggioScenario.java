package com.mad.guitarteacher.practice.Scenarios;

import java.util.ArrayList;

import com.mad.guitarteacher.practice.ExerciseStageOption;
import com.mad.guitarteacher.practice.ExerciseTypes;
import com.mad.guitarteacher.practice.Acts.FixedPositionSequencePlayAct;
import com.mad.tunerlib.musicalBase.FretFingerPair;

/**
 * This class represents a scenario of an arpeggio playing.
 * 
 * @author Tom
 * 
 */
public class ArpeggioScenario extends ExerciseScenario
{

	public ArpeggioScenario()
	{
		super(ExerciseTypes.eExerciseTypes_Barre);
	}

	ArrayList<FretFingerPair>	m_arFretFingerPairs;

	@Override
	public boolean init(final String[] arParams,
						ExerciseStageOption parent)
	{
		if (!super.init(arParams, parent))
		{
			return false;
		}

		m_arFretFingerPairs = new ArrayList<FretFingerPair>();

		// Create the act to be played.
		FixedPositionSequencePlayAct fixedPositionSequencePlayAct =
				new FixedPositionSequencePlayAct(this);

		if ((arParams.length % 3) != 0)
		{
			return false;
		}
		// Parsing the script should be as follows:
		// FingerIndex,String,Fret.
		for (int stringIndex = 0; stringIndex < arParams.length; stringIndex +=
				3)
		{
			fixedPositionSequencePlayAct
					.addHandPosition(new FretFingerPair(Integer.parseInt(arParams[stringIndex]),
														Integer.parseInt(arParams[stringIndex + 1]),
														Integer.parseInt(arParams[stringIndex + 2])));
		}

		addAct(fixedPositionSequencePlayAct);

		return true;
	}
}
