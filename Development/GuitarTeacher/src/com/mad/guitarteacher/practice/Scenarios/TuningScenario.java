package com.mad.guitarteacher.practice.Scenarios;

import com.mad.guitarteacher.practice.ExerciseStageFactory;
import com.mad.guitarteacher.practice.ExerciseStageOption;
import com.mad.guitarteacher.practice.ExerciseTypes;
import com.mad.guitarteacher.practice.Acts.DisplayInformationAct;
import com.mad.lib.display.pager.PagerPage;
import com.mad.lib.display.pager.PagerPageCollection;
import com.mad.lib.musicalBase.OctavedNote;
import com.mad.lib.tuning.FrequencyNormalizer;

/**
 * This scenario handles the scenario of tuning the guitar.
 * 
 * This scenario should play the following sequence (by adding acts):
 * 
 * 1. Play each open string on the guitar. 2. A tuning bar will appear and
 * specify how much the guitar is tuned.
 * 
 * @author Guy Kaplan
 * @author Tom Feigin
 * @since 10.02.2014
 * 
 */
public class TuningScenario extends ExerciseScenario
{
	/**
	 * The frequency normalizer.
	 */
	FrequencyNormalizer	m_frequencyNormalizer;

	static
	{
		ExerciseStageFactory.register(
				ExerciseTypes.eExerciseTypes_Tuning,
				TuningScenario.class);
	}

	/**
	 * Create a new instance of the tuning scenario.
	 */
	public TuningScenario()
	{
		super(ExerciseTypes.eExerciseTypes_Tuning);
		m_frequencyNormalizer =
				FrequencyNormalizer.createDefaultNormalizer();

	}

	@Override
	public boolean init(final String[] arParams,
						final ExerciseStageOption parent)
	{
		// TODO: We should get parameters in the following fashion:
		// Note for lowest string, and octave.
		// Array of numbers, each number is the number of half
		// tones needed to get to the note of the next string.
		// Example: (normal guitar tuning).
		// E,0;5,5,5,4,5
		// Example for Bass drop D:
		// D,-1;7,5,5
		super.init(arParams, parent);

		PagerPageCollection stageInformation =
				new PagerPageCollection();

		stageInformation
				.addPage(new PagerPage(	"Tuning",
										"First play the seperate chord notes, then the chord alltogether.!"));

		DisplayInformationAct displayInfoAct =
				new DisplayInformationAct(stageInformation);
		addAct(displayInfoAct);

		TuningAct act =
				new TuningAct(	this,
								OctavedNote.getOpenStringNotes(),
								m_frequencyNormalizer);

		// Add the act.
		addAct(act);

		return true;
	}
}
