package com.mad.guitarteacher.practice.Acts;

import java.util.Timer;
import java.util.TimerTask;

import com.mad.guitarteacher.activities.IPlayingDisplayer;
import com.mad.guitarteacher.musicalBase.score.MusicScore;
import com.mad.guitarteacher.musicalBase.score.MusicScorePlayer;
import com.mad.guitarteacher.musicalBase.score.MusicScorePlayer.MusicScorePlayerListener;
import com.mad.guitarteacher.musicalBase.score.TimedMusicalObject;
import com.mad.guitarteacher.practice.Scenarios.ExerciseScenario;
import com.mad.lib.R;
import com.mad.lib.musicalBase.IHandPositioning;
import com.mad.lib.musicalBase.MusicalObject;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.tunerlib.recognition.OnRecognitionDetectedListener;
import com.mad.tunerlib.recognition.RecognizerBase;
import com.mad.tunerlib.utils.GuitarNotePlayer;

public abstract class MusicScorePlayAct extends UserPlayActBase
		implements OnRecognitionDetectedListener
{

	protected int				m_nCurrentNote				= 0;
	protected MusicScore		m_MusicScore;
	protected MusicScorePlayer	m_MusicScorePlayer;
	protected RecognizerBase	m_Recognizer;
	protected int				m_nFailedPlayingAttempts	= 0;

	MusicScorePlayAct(	ExerciseScenario parent,
						RecognizerBase recognizer)
	{
		super(parent);
		m_Recognizer = recognizer;
		m_MusicScore = new MusicScore();
	}

	/**
	 * 
	 */
	protected void onCorrectMusicalObjectRecognized()
	{
		if (!m_Parent.isInSimulationMode())
		{
			m_Recognizer.stopListening();
			addTotalPoints(ActBase.MAX_SCORE
					/ (m_MusicScore.size() * 2));
		}
		m_nCurrentNote++;
		// Notify the displayer that the notes played correctly.
		m_Displayer.setNotesPlayedCorrectly();

		playNextMusicalObject();
	}

	@Override
	public void beginPlayingActImpl(final IPlayingDisplayer displayer)
	{
		super.beginPlayingActImpl(displayer);
		m_nFailedPlayingAttempts = 0;
		m_Displayer = displayer;
		m_nCurrentNote = 0;
		m_Displayer.resetScreen();
		// Lazy loading the music player.
		if (m_Parent.isInSimulationMode())
		{
			if (m_MusicScorePlayer == null)
			{
				m_MusicScorePlayer =
						new MusicScorePlayer(m_MusicScore);
				m_MusicScorePlayer.setListener(listener);
			}
			else
			{
				m_MusicScorePlayer.reset();
			}
		}
		playNextMusicalObject();
	}

	@Override
	public void pause()
	{
		super.pause();
		if (m_Parent.isInSimulationMode())
		{
			m_MusicScorePlayer.pause();
		}
		else
		{
			m_Recognizer.stopListening();
		}
	}

	@Override
	public void resume()
	{
		super.resume();
		if (m_Parent.isInSimulationMode())
		{
			m_MusicScorePlayer.play();
		}
		else
		{
			m_Recognizer.beginListening(this);
		}
	}

	protected void playNextMusicalObject()
	{
		// You should note that m_nCurrentNote was already incremented.
		if (m_nCurrentNote == m_MusicScore.size())
		{
			endPlayingAct();
			return;
		}

		MusicalObject currentNote =
				m_MusicScore.getByIndex(m_nCurrentNote)
						.getMusicalObject();
		IHandPositioning holdHandPositioning =
				getHoldHandPositioning();
		m_Displayer.displayNotes(holdHandPositioning,
				getPlayedHandPositioning(holdHandPositioning));
		m_Displayer.setCurrentActState("Play "
				+ currentNote.getName());
		m_Displayer.setWaitingPlay(true);

		resume();
	}

	protected IHandPositioning getHoldHandPositioning()
	{
		return null;
	}

	MusicScorePlayerListener	listener	=
													new MusicScorePlayerListener()
													{

														@Override
														public void onBeat(	MusicScore score,
																			int nBeat)
														{
															final TimedMusicalObject noteCurrent =
																	score.getByBeat(nBeat);

															if (noteCurrent != null)
															{
																playCurrentNote(noteCurrent);
															}

															// We are going to
															// schedule the play
															// of the beat in
															// one beat.
															TimedMusicalObject nextChord =
																	(m_nCurrentNote + 1) < m_MusicScore
																			.size()
																			? m_MusicScore
																					.getByIndex(m_nCurrentNote + 1)

																			: null;
															if ((nextChord != null)
																	&& (noteCurrent != null))
															{
																if (noteCurrent
																		.getMusicalObject() == nextChord
																		.getMusicalObject())
																{
																	onCorrectMusicalObjectRecognized();
																}
															}
														}

														private void playCurrentNote(final TimedMusicalObject noteCurrent)
														{
															new Timer()
																	.schedule(
																			new TimerTask()
																			{
																				@Override
																				public void run()
																				{
																					GuitarNotePlayer player =
																							AppLibraryServiceProvider
																									.getInstance()
																									.get(R.service.guitar_note_player);
																					player.playIHandPositioning(noteCurrent
																							.getMusicalObject()
																							.getHandPositioning(
																									0));
																				}
																			},
																			1000);
														}

														@Override
														public void onEnd(MusicScore score)
														{
															m_Displayer
																	.resetScreen();
															endPlayingAct(false);
														}
													};

	protected abstract IHandPositioning getPlayedHandPositioning(IHandPositioning holdedHandPositioning);

}
