package com.mad.guitarteacher.musicalBase.score;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A class to manager a score's playing.
 * 
 * @author Nati
 * 
 */
public class MusicScorePlayer
{
	private static final int				MILLISECONDS_IN_MINUTE	=
																			60000;
	private static final int				DEFAULT_BPM				=
																			60;
	private final MusicScore				m_MusicScore;
	private int								m_nCurrentBeat;
	private final ScheduledExecutorService	scheduleTaskExecutor;
	private final int						m_nBpm;
	protected MusicScorePlayerListener		m_Listener;
	private boolean							m_fRunning;
	private ScheduledFuture<?>				m_Future;

	/**
	 * An interface for notifying time for a beat.
	 * 
	 * @author Nati
	 * 
	 */
	public interface MusicScorePlayerListener
	{
		public void onBeat(MusicScore score, int nBeat);

		public void onEnd(MusicScore score);
	};

	public void setListener(MusicScorePlayerListener listener)
	{
		m_Listener = listener;
	}

	public MusicScorePlayer(MusicScore musicScore)
	{
		m_MusicScore = musicScore;
		m_nCurrentBeat = 0;
		scheduleTaskExecutor =
				Executors.newScheduledThreadPool(1);
		m_nBpm = DEFAULT_BPM;
	}

	/**
	 * Starts a timer to play the score. A notification will be given on every
	 * beat that occurs.
	 */
	public void play()
	{
		synchronized (this)
		{
			if (m_fRunning)
			{
				return;
			}
			m_fRunning = true;
			m_Future =
					scheduleTaskExecutor.scheduleAtFixedRate(
							new Runnable()
							{
								@Override
								public void run()
								{
									// We must lock to make sure we dont have
									// concurrent use of the lock.
									// And we mustn't release it before
									// finishing the method to prevent that.
									synchronized (MusicScorePlayer.this)
									{
										if (m_fRunning)
										{

											final boolean fReachedEnd =
													m_nCurrentBeat == m_MusicScore
															.getBeats();
											if (fReachedEnd)
											{
												m_fRunning =
														false;
												m_Future.cancel(false);
											}
											if (m_Listener != null)
											{
												final int nCurrentBeat =
														m_nCurrentBeat;
												// We are doing this in a new
												// thread to release the lock
												new Thread(new Runnable()
												{

													@Override
													public void run()
													{
														if (fReachedEnd)
														{
															m_Listener
																	.onEnd(m_MusicScore);
														}
														else
														{
															m_Listener
																	.onBeat(m_MusicScore,
																			nCurrentBeat);
														}
													}
												}).start();

											}
											m_nCurrentBeat++;
										}
									}
								}
							}, 0, MILLISECONDS_IN_MINUTE
									/ m_nBpm,
							TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * Pauses the player. It doesn't block to wait for termination of currently
	 * running process.
	 */
	public void pause()
	{
		synchronized (this)
		{
			if (m_fRunning == false)
			{
				return;
			}

			m_fRunning = false;
			m_Future.cancel(true);
		}
	}

	/**
	 * @return the m_MusicScore
	 */
	public MusicScore getMusicScore()
	{
		return m_MusicScore;
	}

	public TimedMusicalObject getCurrentNote()
	{
		return m_MusicScore.getByBeat(m_nCurrentBeat);
	}

	/**
	 * Resets the player.
	 */
	public void reset()
	{
		m_nCurrentBeat = 0;
	}
}
