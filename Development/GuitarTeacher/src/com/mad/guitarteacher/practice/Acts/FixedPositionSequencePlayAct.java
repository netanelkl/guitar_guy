package com.mad.guitarteacher.practice.Acts;

import java.util.ArrayList;

import com.mad.guitarteacher.activities.IPlayingDisplayer;
import com.mad.guitarteacher.practice.Scenarios.ExerciseScenario;
import com.mad.lib.R;
import com.mad.lib.musicalBase.FretFingerPairBase;
import com.mad.lib.musicalBase.IHandPositioning;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.lib.utils.ErrorHandler;
import com.mad.tunerlib.musicalBase.FretFingerPair;
import com.mad.tunerlib.recognition.DetectionInfo;
import com.mad.tunerlib.recognition.NoteRecognizer;
import com.mad.tunerlib.recognition.NoteRecognizer.NoteDetectionInfo;
import com.mad.tunerlib.recognition.OnRecognitionDetectedListener;

public class FixedPositionSequencePlayAct extends
		UserPlayActBase implements OnRecognitionDetectedListener
{
	private final ArrayList<IHandPositioning>	m_arFingerPairs				=
																					new ArrayList<IHandPositioning>();

	private boolean								m_fShouldPlayToHimFirst;

	private int									m_nCurrentNote				=
																					0;
	private int									m_nFailedPlayingAttempts	=
																					0;

	public FixedPositionSequencePlayAct(ExerciseScenario parent)
	{
		super(parent);
	}

	@Override
	public void pause()
	{
		super.pause();
		NoteRecognizer noteRecognizer =
				AppLibraryServiceProvider.getInstance().get(
						R.service.note_recognizer);
		noteRecognizer.stopListening();
	}

	@Override
	public void resume()
	{
		super.resume();
		NoteRecognizer noteRecognizer =
				AppLibraryServiceProvider.getInstance().get(
						R.service.note_recognizer);
		noteRecognizer.beginListening(this);
	}

	public void addHandPosition(final IHandPositioning noteToAdd)
	{
		m_arFingerPairs.add(noteToAdd);
	}

	@Override
	public void beginPlayingActImpl(final IPlayingDisplayer displayer)
	{
		super.beginPlayingActImpl(displayer);

		m_nCurrentNote = 0;

		playNextNote();
	}

	/**
	 * Return a sequence of finger pairs to be played.
	 * 
	 * @return FingerPair[] - sequence of finger pairs to be played.
	 */
	public ArrayList<IHandPositioning> getNoteSequence()
	{
		return m_arFingerPairs;
	}

	@Override
	public int getScore()
	{
		return (ActBase.MAX_SCORE * m_arFingerPairs.size())
				/ (m_arFingerPairs.size() + m_nFailedPlayingAttempts);
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

			ArrayList<FretFingerPairBase> fingerPositions =
					m_arFingerPairs.get(m_nCurrentNote)
							.getFingerPositions();

			if (fingerPositions.size() == 1)
			{
				FretFingerPair fretFingerPair =
						(FretFingerPair) fingerPositions.get(0);

				if (info.NoteIndex == fretFingerPair
						.getOctavedNote().getAbsoluteIndex())
				{
					NoteRecognizer noteRecognizer =
							AppLibraryServiceProvider
									.getInstance()
									.get(R.service.note_recognizer);
					noteRecognizer.stopListening();
					m_nCurrentNote++;
					m_Displayer.setWaitingPlay(false);
					playNextNote();
				}
				else
				{
					m_nFailedPlayingAttempts++;
					// TODO: Should be easier here.
					m_Displayer
							.wrongNotePlayed(new FretFingerPair(info.NoteIndex));
				}

			}

		}
		else
		{
			// TODO: Report error.
		}
	}

	private void playNextNote()
	{
		// You should note that m_nCurrentNote was already incremented.
		if (m_nCurrentNote == m_arFingerPairs.size())
		{
			endPlayingAct();
			return;
		}
		NoteRecognizer noteRecognizer =
				AppLibraryServiceProvider.getInstance().get(
						R.service.note_recognizer);
		noteRecognizer.beginListening(this);

		IHandPositioning fretFingerPair =
				m_arFingerPairs.get(m_nCurrentNote);
		m_Displayer.displayNotes(null, fretFingerPair);
		m_Displayer.setCurrentActState("Play");
		m_Displayer.setWaitingPlay(true);
	}

	/**
	 * Returns an indication to whether or not play the sequence to the user.
	 * 
	 * @return boolean - If true, play the sequence to the user.
	 */
	public boolean ShouldPlayToHimFirst()
	{
		return m_fShouldPlayToHimFirst;
	}

}
