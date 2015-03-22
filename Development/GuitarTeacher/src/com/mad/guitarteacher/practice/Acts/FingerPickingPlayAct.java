package com.mad.guitarteacher.practice.Acts;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Collections;

import com.mad.guitarteacher.musicalBase.picking.FingerPicking;
import com.mad.guitarteacher.musicalBase.picking.FingerPickingFactory;
import com.mad.guitarteacher.musicalBase.picking.PickingFinger;
import com.mad.guitarteacher.practice.Scenarios.ExerciseScenario;
import com.mad.lib.musicalBase.FretFingerPairBase;
import com.mad.lib.musicalBase.IHandPositioning;
import com.mad.lib.musicalBase.OctavedNote;
import com.mad.lib.musicalBase.chords.Chord;
import com.mad.lib.utils.ErrorHandler;
import com.mad.tunerlib.musicalBase.BasicHandPositioning;
import com.mad.tunerlib.musicalBase.FretFingerPair;
import com.mad.tunerlib.recognition.DetectionInfo;
import com.mad.tunerlib.recognition.NoteRecognizer.NoteDetectionInfo;
import com.mad.tunerlib.recognition.OnRecognitionDetectedListener;

/**
 * An act for playing finger picking.
 * 
 * @author Tom
 * 
 */
public class FingerPickingPlayAct extends ChordSequencePlayAct
		implements OnRecognitionDetectedListener
{
	FingerPicking		m_arPickingFingers;
	int					m_cycleNum;
	int					m_currentPickingIndex;
	int					m_pickingId;
	private int			m_currentCycleNum;
	private OctavedNote	m_octavedNoteToListen;

	/**
	 * Create a new instance of the FingerPickingPlayAct.
	 * 
	 * @param chords
	 *            - Chords to pick.
	 * @param cycleNum
	 *            - Number of cycles.
	 * @param bpm
	 *            - Beats per minute.
	 * @param pickingId
	 *            - Picking type ID.
	 */
	public FingerPickingPlayAct(ExerciseScenario parent,
								final Chord[] chords,
								int cycleNum,
								int bpm,
								int pickingId)
	{
		super(parent, chords);
		m_octavedNoteToListen = new OctavedNote(-1);
		m_currentPickingIndex = 0;
		m_cycleNum = cycleNum;
		m_currentCycleNum = 0;
		m_pickingId = pickingId;
		getFingerPicking();
	}

	/**
	 * @param chord
	 * @param pickingId
	 */
	@SuppressWarnings("unchecked")
	private void getFingerPicking()
	{
		IHandPositioning handPositioning =
				m_MusicScore.getByIndex(m_nCurrentNote)
						.getMusicalObject()
						.getHandPositioning(0);

		ArrayList<FretFingerPairBase> arFingerPairs =
				handPositioning.getFingerPositions();

		Collections.sort(arFingerPairs);

		m_arPickingFingers =
				FingerPickingFactory.getPicking(arFingerPairs,
						m_pickingId);
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

			if (info.NoteIndex == m_octavedNoteToListen
					.getAbsoluteIndex())
			{
				m_Recognizer.stopListening();

				advanceInternalIndexes();

				playNextMusicalObject();
			}
			else
			{
				m_nFailedPlayingAttempts++;
				m_Displayer
						.wrongNotePlayed(new OctavedNote(info.NoteIndex)
								.getHandPositioning(0));
			}
		}
		else
		{
			ErrorHandler
					.HandleError(new InvalidClassException(""));
		}
	}

	/**
	 * 
	 */
	private void advanceInternalIndexes()
	{
		if (m_cycleNum > m_currentCycleNum)
		{
			m_nCurrentNote++;
			m_cycleNum = 0;
			m_currentPickingIndex = 0;
			getFingerPicking();
		}

		if (m_currentPickingIndex >= m_arPickingFingers
				.getPickingFingers().size())
		{
			m_currentCycleNum++;
			m_currentPickingIndex = 0;
		}
		else
		{
			m_currentPickingIndex++;
		}
	}

	@Override
	protected IHandPositioning getHoldHandPositioning()
	{
		return m_MusicScore.getByIndex(m_nCurrentNote)
				.getMusicalObject().getHandPositioning(0);
	}

	@Override
	protected IHandPositioning getPlayedHandPositioning(IHandPositioning holdedHandPositioning)
	{
		ArrayList<PickingFinger> pickingFinger =
				m_arPickingFingers.getPickingFingers().get(
						m_currentPickingIndex);

		ArrayList<FretFingerPairBase> fingersToPlay =
				new ArrayList<FretFingerPairBase>();

		// Go through all the finger positions.
		for (PickingFinger pickingFinger2 : pickingFinger)
		{
			for (FretFingerPairBase fretFingerPair : holdedHandPositioning
					.getFingerPositions())
			{
				if (pickingFinger2.getString().getString() == fretFingerPair
						.getString())
				{
					// TODO: Does not have to be always only one!
					fingersToPlay.add(fretFingerPair);
				}
			}
		}
		FretFingerPair pair =
				(FretFingerPair) fingersToPlay.get(0);
		m_octavedNoteToListen = pair.getOctavedNote();

		BasicHandPositioning basicHandPositioningToPlay =
				new BasicHandPositioning(fingersToPlay);
		return basicHandPositioningToPlay;
	}
}
