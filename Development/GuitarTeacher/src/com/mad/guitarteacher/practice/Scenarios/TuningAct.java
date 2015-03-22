package com.mad.guitarteacher.practice.Scenarios;

import com.mad.guitarteacher.activities.IPlayingDisplayer;
import com.mad.guitarteacher.practice.Acts.NoteSequencePlayAct;
import com.mad.guitarteacher.utils.Definitions;
import com.mad.lib.musicalBase.OctavedNote;
import com.mad.lib.tuning.FrequencyNormalizer;
import com.mad.tunerlib.recognition.NoteRecognizer.NoteDetectionInfo;

/**
 * The tuning act is just a note sequence act with different implementation for
 * the display of the notes.
 * 
 * @author Tom
 * 
 */
public class TuningAct extends NoteSequencePlayAct
{
	FrequencyNormalizer	m_FrequencyNormalizer;

	/**
	 * Create a new instance of the TuningAct class.
	 * 
	 * @param notes
	 *            - Notes to be added.
	 * @param frequencyNormalizer
	 *            - The frequency normalizer instance.
	 */
	public TuningAct(	ExerciseScenario parent,
						OctavedNote[] notes,
						FrequencyNormalizer frequencyNormalizer)
	{
		super(parent, notes);

		m_FrequencyNormalizer = frequencyNormalizer;
	}

	@Override
	public void beginPlayingActImpl(IPlayingDisplayer displayer)
	{
		super.beginPlayingActImpl(displayer);

		displayer.displayTunerGauge(true, 0);
	}

	@Override
	protected boolean compareOnRecognitionInput(NoteDetectionInfo info)
	{

		OctavedNote octavedNote =
				(OctavedNote) m_MusicScore.getByIndex(
						m_nCurrentNote).getMusicalObject();
		int noteIndex = octavedNote.getAbsoluteIndex();

		float wantedFrequency =
				m_FrequencyNormalizer
						.getFrequencyByNoteIndex(noteIndex);

		if (noteIndex != info.NoteIndex)
		{
			return false;
		}

		if (Definitions.DebugFlags.DEBUG_COMPARE_TUNING_PASSES)
		{
			return true;
		}
		else
		{
			boolean result =
					Math.abs((info.Frequency - wantedFrequency)
							/ wantedFrequency) < 0.01;
			return result;
		}
	}

	@Override
	protected void onWrongNoteRecognized(NoteDetectionInfo info)
	{
		m_nFailedPlayingAttempts++;

		float gaugePoint = getGaugePoint(info);

		m_Displayer.displayTunerGauge(true, gaugePoint);
	}

	/**
	 * Return the point in the gauge (from -1 to 1) to be presented to the user
	 * for tuning.
	 * 
	 * @param info
	 *            - Info detected by sound recognition.
	 */
	protected float getGaugePoint(NoteDetectionInfo info)
	{
		OctavedNote musicNote =
				(OctavedNote) m_MusicScore.getByIndex(
						m_nCurrentNote).getMusicalObject();
		int noteIndex = musicNote.getAbsoluteIndex();

		final float sfCurrentFreq =
				m_FrequencyNormalizer
						.getFrequencyByNoteIndex(noteIndex);

		float gaugePointDenominator =
				info.Frequency - sfCurrentFreq;

		float gaugePointNumerator = 1;

		if (gaugePointDenominator > 0)
		{
			final float sfNextFreq =
					m_FrequencyNormalizer
							.getFrequencyByNoteIndex(noteIndex + 1);

			gaugePointNumerator = sfNextFreq - sfCurrentFreq;
		}
		else
		{

			final float sfPrevFreq =
					m_FrequencyNormalizer
							.getFrequencyByNoteIndex((noteIndex - 1));
			gaugePointNumerator = sfCurrentFreq - sfPrevFreq;
		}

		// The point to show in the gauge for tuning.
		float gaugePoint =
				(gaugePointDenominator / gaugePointNumerator);

		// Set the limit for the gauge point.
		if (gaugePoint > 1)
		{
			gaugePoint = 1;
		}
		else if (gaugePoint < -1)
		{
			gaugePoint = -1;
		}

		return gaugePoint;
	}
}
