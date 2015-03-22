package com.mad.guitarteacher.practice.Acts;

import android.util.Log;

import com.mad.guitarteacher.musicalBase.score.MusicScore;
import com.mad.guitarteacher.musicalBase.score.TimedMusicalObject;
import com.mad.guitarteacher.practice.Scenarios.ExerciseScenario;
import com.mad.lib.R;
import com.mad.lib.musicalBase.IHandPositioning;
import com.mad.lib.musicalBase.OctavedNote;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.lib.utils.ErrorHandler;
import com.mad.lib.utils.HelperMethods;
import com.mad.tunerlib.recognition.DetectionInfo;
import com.mad.tunerlib.recognition.NoteRecognizer.NoteDetectionInfo;
import com.mad.tunerlib.recognition.RecognizerBase;

public class NoteSequencePlayAct extends MusicScorePlayAct

{

	private static final String	TAG	= "NoteSequencePlayingAct";

	protected boolean			m_fShouldPlayToHimFirst;

	NoteDetectionInfo			m_LastDetectedNote;

	/**
	 * Create new instance of the NoteSequencePlayAct object.
	 */
	public NoteSequencePlayAct(ExerciseScenario parent)
	{
		this(parent, null);
	}

	/**
	 * Create new instance of the NoteSequencePlayAct object.
	 * 
	 * @param notes
	 *            - notes to play.
	 */
	public NoteSequencePlayAct(	ExerciseScenario parent,
								OctavedNote[] notes)
	{
		super(parent, (RecognizerBase) AppLibraryServiceProvider
				.getInstance().get(R.service.note_recognizer));
		if (notes != null)
		{
			for (OctavedNote octavedNote : notes)
			{
				addNote(octavedNote);
			}
		}

	}

	public void addNote(OctavedNote octavedNote)
	{
		m_MusicScore
				.add(new TimedMusicalObject(octavedNote,
											MusicScore.BEATS_PER_MUSICAL_OBJECT));
	}

	@Override
	public int getScore()
	{
		return (ActBase.MAX_SCORE * m_MusicScore.size())
				/ (m_MusicScore.size() + m_nFailedPlayingAttempts);
	}

	@Override
	public synchronized void onRecognitionDetected(final DetectionInfo detectionInfo)
	{
		if (detectionInfo instanceof NoteDetectionInfo)
		{
			NoteDetectionInfo info =
					(NoteDetectionInfo) detectionInfo;

			if ((info == null) || (m_Displayer == null))
			{
				ErrorHandler
						.HandleError(new NullPointerException());
				return;
			}

			if ((m_LastDetectedNote != null)
					&& (m_LastDetectedNote.Frequency == info.Frequency))
			{
				double diffSeconds =
						HelperMethods.dateDiff(
								m_LastDetectedNote.Timestamp,
								info.Timestamp)
								.getTotalSeconds();
				if (diffSeconds < 2)
				{
					Log.d(TAG,
							String.format(
									"Same note recognized. new(%d)last(%d)diff(%f)",
									info.Timestamp,
									m_LastDetectedNote.Timestamp,
									diffSeconds));
					return;
				}
			}

			Log.i(TAG, "note(" + info.NoteIndex + ") freq("
					+ info.Frequency + ")");
			m_LastDetectedNote = info;
			if (compareOnRecognitionInput(info))
			{
				onCorrectMusicalObjectRecognized();
			}
			else
			{
				onWrongNoteRecognized(info);
			}
		}
		else
		{
			// TODO: Report error.
		}
	}

	/**
	 * @param info
	 * @return
	 */
	protected boolean compareOnRecognitionInput(NoteDetectionInfo info)
	{
		OctavedNote byBeat =
				(OctavedNote) (m_MusicScore
						.getByIndex(m_nCurrentNote)
						.getMusicalObject());
		return (info.NoteIndex == byBeat.getAbsoluteIndex());
	}

	/**
	 * @param info
	 */
	protected void onWrongNoteRecognized(NoteDetectionInfo info)
	{
		m_nFailedPlayingAttempts++;
		m_Displayer
				.wrongNotePlayed(new OctavedNote(info.NoteIndex)
						.getHandPositioning(0));
	}

	@Override
	protected IHandPositioning getPlayedHandPositioning(IHandPositioning holdedHandPositioning)
	{
		return m_MusicScore.getByIndex(m_nCurrentNote)
				.getMusicalObject().getHandPositioning(0);
	}

}
