package com.mad.guitarteacher.practice.Scenarios;

import java.util.ArrayList;
import java.util.Stack;

import com.mad.guitarteacher.activities.IPlayingDisplayer;
import com.mad.guitarteacher.practice.ExerciseStageFactory;
import com.mad.guitarteacher.practice.ExerciseStageOption;
import com.mad.guitarteacher.practice.ExerciseTypes;
import com.mad.guitarteacher.practice.Acts.DisplayInformationAct;
import com.mad.guitarteacher.practice.Acts.NoteSequencePlayAct;
import com.mad.lib.R;
import com.mad.lib.display.pager.PagerPage;
import com.mad.lib.display.pager.PagerPageCollection;
import com.mad.lib.musicalBase.Notes;
import com.mad.lib.musicalBase.OctavedNote;
import com.mad.lib.musicalBase.keyConverters.EKeyConverterType;
import com.mad.lib.musicalBase.keyConverters.IKeyConverter;
import com.mad.lib.musicalBase.keyConverters.KeyConverterFactory;
import com.mad.lib.tuning.FrequencyNormalizer;
import com.mad.lib.utils.AppLibraryServiceProvider;

/**
 * This scenario is for the user on the very first use of the guitar teacher.
 * 
 * @author Tom
 * 
 */
public class IntroductionScenario extends ExerciseScenario
{
	public IntroductionScenario()
	{
		super(ExerciseTypes.eExerciseTypes_Introduction);
	}

	// TODO: Kidder, worst design ever, if every scenario
	// registers to the factory then every scenario knows the factory
	// but all the constructors of the scenarios are in the factory!
	// both ways work fine but together it's just stupid!
	// if the factory calls the constructors of the scenarios
	// it doesn't need the register!
	static
	{
		ExerciseStageFactory.register(
				ExerciseTypes.eExerciseTypes_Introduction,
				IntroductionScenario.class);
	}

	@Override
	public boolean init(final String[] arParams,
						final ExerciseStageOption parent)
	{
		super.init(arParams, parent);

		boolean fIsFirstTime = false;
		if ((arParams.length == 1) && arParams[0].equals("T"))
		{
			fIsFirstTime = true;
		}

		showGuitarInformation();

		if (!fIsFirstTime)
		{
			openStringsIntroduction();

			fretIntroduction();

			scaleIntroduction();

			thirdsIntroduction();
		}
		else
		{
			addAct(new TuningAct(this, OctavedNote
					.getOpenStringNotes(), FrequencyNormalizer
					.createDefaultNormalizer()));
		}
		return true;
	}

	/**
	 * 
	 */
	private void thirdsIntroduction()
	{
		OctavedNote[] notes = new OctavedNote[12];

		// The notes to add.
		notes[0] = new OctavedNote(Notes.C, 1);
		notes[1] = new OctavedNote(Notes.E, 1);
		notes[2] = new OctavedNote(Notes.G, 1);
		notes[3] = new OctavedNote(Notes.C, 1);
		notes[4] = new OctavedNote(Notes.E, 1);
		notes[5] = new OctavedNote(Notes.G, 1);

		notes[6] = new OctavedNote(Notes.D, 1);
		notes[7] = new OctavedNote(Notes.F, 1);
		notes[8] = new OctavedNote(Notes.A, 1);
		notes[9] = new OctavedNote(Notes.D, 1);
		notes[10] = new OctavedNote(Notes.F, 1);
		notes[11] = new OctavedNote(Notes.A, 1);

		// Create the act
		NoteSequencePlayAct thirdIntroductionAct =
				new NoteSequencePlayAct(this, notes);

		// Create user info
		PagerPage startPage =
				new PagerPage(	"Playing thirds",
								"Just like seconds build a scale,"
										+ "thirds build a chord.\n"
										+ "Remember that a third is an interval of "
										+ "three notes e.g C-E (with D in the middle).\n"
										+ "By playing any two consecutive thirds a "
										+ "basic chord will be formed.\n"
										+ "You will now play the basic notes of the Cmaj "
										+ "chord followed by the basic notes of the Emin chord.");

		// Create the stage info.
		PagerPageCollection stageInformation =
				new PagerPageCollection();
		stageInformation.addPage(startPage);

		addAct(new DisplayInformationAct(stageInformation));
		addAct(thirdIntroductionAct);
	}

	/**
	 */
	private void scaleIntroduction()
	{
		NoteSequencePlayAct scaleAct =
				getScaleAct(EKeyConverterType.Major, Notes.C);
		addAct(scaleAct);

		scaleAct = getScaleAct(EKeyConverterType.Minor, Notes.A);

		PagerPage startPage =
				new PagerPage(	"Seconds.",
								"You have just played a sequence of seconds.\n"
										+ "Second is the interval that builds a scale.\n"
										+ "Now you will play the C maj scale, from C and "
										+ "and to C of a higher octave, and then repeat with the A");

		PagerPageCollection stageInformation =
				new PagerPageCollection();
		stageInformation.addPage(startPage);
		addAct(new DisplayInformationAct(stageInformation));
		addAct(scaleAct);
	}

	/**
	 * @param converterType
	 * @param key
	 * @param converterFactory
	 * @return
	 */
	private NoteSequencePlayAct getScaleAct(EKeyConverterType converterType,
											int key)
	{
		KeyConverterFactory factory =
				AppLibraryServiceProvider.getInstance().get(
						R.service.key_converter_factory);

		IKeyConverter converter =
				factory.getConverter(converterType);

		ArrayList<OctavedNote> notes =
				new ArrayList<OctavedNote>();
		Stack<OctavedNote> notes2 = new Stack<OctavedNote>();

		for (int noteIndex : converter.getNoteTable(key))
		{
			OctavedNote note = new OctavedNote(noteIndex, 1, 0);
			notes.add(note);

			note = new OctavedNote(noteIndex, 1, 0);
			notes2.add(note);
		}

		NoteSequencePlayAct scaleAct =
				new NoteSequencePlayAct(this);

		for (OctavedNote octavedNote : notes)
		{
			scaleAct.addNote(octavedNote);
		}

		while (!notes2.empty())
		{
			scaleAct.addNote(notes2.pop());
		}

		return scaleAct;
	}

	/**
	 * 
	 */
	private void fretIntroduction()
	{
		OctavedNote[] notes = new OctavedNote[12];

		notes[0] = new OctavedNote(Notes.C, 2);
		notes[1] = new OctavedNote(Notes.D, 2);
		notes[2] = new OctavedNote(Notes.C, 2);
		notes[3] = new OctavedNote(Notes.C, 2);
		notes[4] = new OctavedNote(Notes.B, 1);
		notes[5] = new OctavedNote(Notes.BBemol, 0);
		notes[6] = new OctavedNote(Notes.B, 0);
		notes[7] = new OctavedNote(Notes.C, 1);
		notes[8] = new OctavedNote(Notes.F, 0);
		notes[9] = new OctavedNote(Notes.E, 0);
		notes[10] = new OctavedNote(Notes.F, 0);
		notes[11] = new OctavedNote(Notes.FSharp, 0);

		NoteSequencePlayAct fretIntroductionAct =
				new NoteSequencePlayAct(this, notes);

		PagerPage startPage =
				new PagerPage(	"Pressing fret",
								"Notice the metal bars.\n pressing "
										+ "the frets of the guitar presses "
										+ "the string onto the bar shorthening "
										+ "the string which produces a higer note.");

		PagerPageCollection stageInformation =
				new PagerPageCollection();
		stageInformation.addPage(startPage);
		addAct(new DisplayInformationAct(stageInformation));
		addAct(fretIntroductionAct);
	}

	/**
	 * 
	 */
	private void openStringsIntroduction()
	{
		OctavedNote[] notes = new OctavedNote[4];

		notes[0] = new OctavedNote(Notes.A, 0);
		notes[1] = new OctavedNote(Notes.D, 1);
		notes[2] = new OctavedNote(Notes.E, 2);
		notes[3] = new OctavedNote(Notes.E, 0);

		NoteSequencePlayAct openStringsAct =
				new NoteSequencePlayAct(this, notes)
				{
					@Override
					public void beginPlayingActImpl(IPlayingDisplayer displayer)
					{
						// We want the frets to be invisible here.
						super.beginPlayingActImpl(displayer);
						m_Displayer.setFretVisibility(false);
					}
				};

		PagerPage startPage =
				new PagerPage(	"Open strings",
								"We will now play some open strings.\n"
										+ "The vibration of the string causes a sound to be heard."
										+ "The thicker the string is, the slower the vibration and "
										+ "the lower the sound is.");

		PagerPageCollection stageInformation =
				new PagerPageCollection();
		stageInformation.addPage(startPage);
		addAct(new DisplayInformationAct(stageInformation));
		addAct(openStringsAct);
	}

	public void showGuitarInformation()
	{
		// todo: PUT PICTURES
	}
}
