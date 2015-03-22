package com.mad.guitarteacher.practice.Scenarios;

import com.mad.guitarteacher.activities.Rhythm;
import com.mad.guitarteacher.musicalBase.Weight;
import com.mad.guitarteacher.practice.ExerciseStageOption;
import com.mad.guitarteacher.practice.ExerciseTypes;

/**
 * This class represents a rhythm scenario.
 * 
 * @author Tom
 * 
 */
public class RhythmScenario extends ExerciseScenario
{
	private Weight	m_cWeight;
	// private Tempo m_cTempo;
	// private ArrayList<BasicHandPositioning> m_arNotes;
	private Rhythm	m_cRhythm;

	public RhythmScenario()
	{
		super(ExerciseTypes.eExerciseTypes_Introduction);
	}

	@Override
	public boolean init(final String[] arParams,
						ExerciseStageOption parent)
	{
		if (!super.init(arParams, parent))
		{
			return false;
		}

		// Check that the parameters are sufficient.
		if (arParams.length < 6)
		{
			return false;
		}

		// Extract the weight.
		String strWeightNumerator = arParams[0];
		byte weightNumerator =
				Byte.parseByte(strWeightNumerator);
		String strWeightDenominator = arParams[1];
		byte weightDenominator =
				Byte.parseByte(strWeightDenominator);

		m_cWeight =
				new Weight(weightNumerator, weightDenominator);

		// TODO: Parse the string to get the rhythm object.

		int nRhythmLength = Integer.parseInt(arParams[2]);

		if ((nRhythmLength + 2) > arParams.length)
		{
			return false;
		}

		m_cRhythm = new Rhythm();

		// Go through the rhythm.
		for (int rhythmIndex = 0; rhythmIndex < nRhythmLength; rhythmIndex++)
		{
			m_cRhythm.add(Integer
					.parseInt(arParams[3 + rhythmIndex]));
		}

		return true;
	}
}
