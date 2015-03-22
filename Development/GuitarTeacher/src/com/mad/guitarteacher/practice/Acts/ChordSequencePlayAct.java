package com.mad.guitarteacher.practice.Acts;

import java.util.ArrayList;

import com.mad.guitarteacher.R;
import com.mad.guitarteacher.musicalBase.score.MusicScore;
import com.mad.guitarteacher.musicalBase.score.TimedMusicalObject;
import com.mad.guitarteacher.practice.Scenarios.ExerciseScenario;
import com.mad.lib.musicalBase.IHandPositioning;
import com.mad.lib.musicalBase.chords.Chord;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.tunerlib.recognition.ChordDetectionInfo;
import com.mad.tunerlib.recognition.DetectionInfo;
import com.mad.tunerlib.recognition.OnRecognitionDetectedListener;
import com.mad.tunerlib.recognition.RecognizerBase;
import com.mad.tunerlib.recognition.SingleChordDetectionInfo;

public class ChordSequencePlayAct extends MusicScorePlayAct
		implements OnRecognitionDetectedListener
{

	private static final float	FAILED_ATTEMPT_SCORE_REDUCTENT	=
																		ActBase.MAX_SCORE / 10;

	public ChordSequencePlayAct(ExerciseScenario parent,
								final Chord[] chords)
	{
		super(parent, (RecognizerBase) AppLibraryServiceProvider
				.getInstance().get(R.service.chord_recognizer));

		for (Chord chord : chords)
		{
			m_MusicScore
					.add(new TimedMusicalObject(chord,
												MusicScore.BEATS_PER_MUSICAL_OBJECT));
		}

	}

	@Override
	public int getScore()
	{
		int nScore =
				(int) (ActBase.MAX_SCORE - ((FAILED_ATTEMPT_SCORE_REDUCTENT * m_nFailedPlayingAttempts) / m_MusicScore
						.size()));

		if (nScore < 0)
		{
			nScore = 0;
		}
		return nScore;
	}

	@Override
	public synchronized void onRecognitionDetected(final DetectionInfo detectionInfo)
	{
		if (detectionInfo instanceof ChordDetectionInfo)
		{
			ArrayList<SingleChordDetectionInfo> arChords =
					((ChordDetectionInfo) detectionInfo)
							.getChords();

			for (SingleChordDetectionInfo chordInfo : arChords)
			{
				// m_Displayer.displayInformation("ChordDetected",
				// chordInfo.Chord, null);
				if (chordInfo.Chord.equals(m_MusicScore
						.getByIndex(m_nCurrentNote)
						.getMusicalObject().getName()))
				{
					onCorrectMusicalObjectRecognized();
				}
				else
				{
					m_nFailedPlayingAttempts++;
					// TODO: Replace with something suitable for chords.
					// m_Displayer.wrongNotePlayed(
					// new OctavedNote(info.Chord, 0).getFingering(),
					// m_arChordSequence[m_nCurrentNote].getFingering());
				}
			}
		}

		else
		{
			// TODO: Report error.
		}
	}

	@Override
	protected IHandPositioning getPlayedHandPositioning(IHandPositioning holdedHandPositioning)
	{
		return m_MusicScore.getByIndex(m_nCurrentNote)
				.getMusicalObject().getHandPositioning(0);
	}

}
