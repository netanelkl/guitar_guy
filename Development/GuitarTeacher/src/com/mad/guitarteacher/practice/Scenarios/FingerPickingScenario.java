package com.mad.guitarteacher.practice.Scenarios;

import com.mad.guitarteacher.practice.ExerciseStageFactory;
import com.mad.guitarteacher.practice.ExerciseStageOption;
import com.mad.guitarteacher.practice.ExerciseTypes;
import com.mad.guitarteacher.practice.Acts.FingerPickingPlayAct;
import com.mad.lib.display.pager.PagerPage;
import com.mad.lib.display.pager.PagerPageCollection;
import com.mad.lib.musicalBase.chords.Chord;
import com.mad.tunerlib.musicalBase.BasicHandPositioning;

/**
 * The FingerPicking scenario will display a sequence of notes to pick. Each
 * time different string(s) will be picked.
 * 
 * Init parameters would look like this: A24,A5123. The parsing of a single
 * handPositioning is implemented in Fingering. This will have the acts of
 * playing the handPositionings one after the other.
 * 
 * @author Nati
 * 
 */
public class FingerPickingScenario extends ExerciseScenario
{
	private static final int	BPM			= 100;
	private static final int	CYCLE_SIZE	= 4;

	public FingerPickingScenario()
	{
		super(ExerciseTypes.eExerciseTypes_FingerPicking);
	}

	static
	{
		ExerciseStageFactory.register(
				ExerciseTypes.eExerciseTypes_FingerPicking,
				FingerPickingScenario.class);
	}

	BasicHandPositioning[]	arFingers;

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.mad.guitarteacher.practice.Scenarios.ExerciseScenario#init(java.lang.String)
	 */
	@Override
	public boolean init(final String[] arParams,
						final ExerciseStageOption parent)
	{
		super.init(arParams, parent);

		// Check if there is a specified way.
		if (arParams.length != 2)
		{
			return false;
		}

		Chord chord = Chord.parse(arParams[0]);

		if (chord == null)
		{
			return false;
		}

		int pickingID = Integer.parseInt(arParams[1]);

		PagerPageCollection stageInformation =
				new PagerPageCollection();
		stageInformation
				.addPage(new PagerPage(	chord.getName()
												+ " Chord playing",
										"Repeat the chord. please, or die!"));
		stageInformation
				.addPage(new PagerPage(	chord.getName()
												+ " Chord playing",
										"First play the seperate chord notes, then the chord alltogether.!"));

		addAct(new FingerPickingPlayAct(this,
										new Chord[] { chord },
										CYCLE_SIZE,
										BPM,
										pickingID));

		return true;
	}
}
