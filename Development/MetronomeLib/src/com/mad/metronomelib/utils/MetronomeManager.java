package com.mad.metronomelib.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.mad.metronomelib.R;

/**
 * This class manages internal metronome logic.
 * 
 * @author Tom
 * 
 */
public class MetronomeManager
{
	/******************************************************************
	 * Constants
	 ******************************************************************/
	public int	m_subdivisions	= 4;

	/******************************************************************
	 * Related interfaces.
	 ******************************************************************/
	public interface OnTickListener
	{
		public void onTick(final int nCurrentSubdivision);
	}

	public interface OnTempoChangeListener
	{
		public void onTempoChange(final int nNewBPMMilisecs);
	}

	/********************************************************
	 * Members
	 ********************************************************/
	// The current Bpm (beats per minute)
	private int				m_nCurrentBpm			= 100;
	private int				m_currentBpmMilisecs	=
															(int) ((60f / m_nCurrentBpm) * 1000);

	// Create the sound player.
	private final SoundPool	m_cSoundPlayer;

	// Sound id's for the tick and tock sounds.
	private final int		m_nTickSoundId;
	private final int		m_nTockSoundId;

	// The current subdivision as a number (The number that shows at the middle
	// of the circle)
	private int				m_currentSubdivision	= -1;

	// The beat timer, (goes on each m_nCurrentBpmMilisecs)
	private Timer			m_beatTimer;

	// Flag indicates whether the ticks are playing or not.
	private boolean			m_isPlaying				= false;

	// The timer task to perform each timer tick.
	private TimerTask		m_timerTask;

	private class MetronomeTimerTask extends TimerTask
	{
		@Override
		public void run()
		{
			onTimerTick();
		}
	}

	// A listener enables registering to a tick event.
	private OnTickListener			m_TickListener;

	// A listener for a tempo change event.
	private OnTempoChangeListener	m_BpmChangeListener;

	// After setting a new bpm, set this value to true to make sure that the
	// timer task will set
	// the timer according to the new bpm.
	private boolean					m_shouldUpdateBpm	= false;

	/**
	 * Ctor for the metronome manager class.
	 * 
	 * @param context
	 *            The context used for getting the sound files from the assest.
	 */
	public MetronomeManager(Context context)
	{
		// Create a new instance of the media player to play the ticks.
		m_cSoundPlayer =
				new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

		// Initialize a new timer.
		m_beatTimer = new Timer();

		// Load the tick sound.
		m_nTickSoundId =
				m_cSoundPlayer.load(context,
						R.raw.metronome_tick, 1);

		// Load the tock sound.
		m_nTockSoundId =
				m_cSoundPlayer.load(context,
						R.raw.metronome_tock, 1);
	}

	/**
	 * Set the weight of the beat.
	 * 
	 * @param nominator
	 *            - Nominator of the beat.
	 */
	public void setSubdivision(int nominator)
	{
		m_subdivisions = nominator;
	}

	/**
	 * Start the metronome manager logic.
	 */
	public void start()
	{
		m_currentSubdivision = -1;

		// Create a new timer and activate it.
		m_isPlaying = true;

		// Create a new timer task.
		m_timerTask = new MetronomeTimerTask();

		// Create a new timer and schedule it.
		m_beatTimer = new Timer();
		m_beatTimer.schedule(m_timerTask, 0,
				m_currentBpmMilisecs);
	}

	public void stop()
	{
		// Stop and clean the timer.
		if (m_beatTimer != null)
		{
			m_beatTimer.cancel();
		}

		if (m_timerTask != null)
		{
			m_timerTask.cancel();
		}

		if (m_beatTimer != null)
		{
			m_beatTimer.purge();
		}
		m_beatTimer = null;
		m_timerTask = null;
		m_isPlaying = false;
	}

	/**
	 * Act out logic of sounding a tick.
	 */
	private void onTimerTick()
	{
		// Raise the subdivision counter.
		m_currentSubdivision++;
		if (m_subdivisions != 0)
		{
			m_currentSubdivision %= m_subdivisions;
		}

		// Check what sound we need to play.
		if (m_currentSubdivision == 0)
		{
			m_cSoundPlayer
					.play(m_nTickSoundId, 1f, 1f, 0, 0, 1f);
		}
		else
		{
			m_cSoundPlayer
					.play(m_nTockSoundId, 1f, 1f, 0, 0, 1f);
		}

		// Notify the listener object about the tick event.
		if (m_TickListener != null)
		{
			m_TickListener.onTick(m_currentSubdivision);
		}

		// Check if should update the rate of the timer.
		if (m_shouldUpdateBpm == true)
		{
			// Create a new timer to set a new bpm.
			m_timerTask.cancel();
			m_timerTask = new MetronomeTimerTask();

			// Reschedule the timer.
			m_beatTimer.schedule(m_timerTask,
					m_currentBpmMilisecs, m_currentBpmMilisecs);
			m_shouldUpdateBpm = false;
		}
	}

	/**
	 * Returns the current BPM
	 * 
	 * @return the current bpm.
	 */
	public int getBPM()
	{
		return m_nCurrentBpm;
	}

	/**
	 * Returns the current BPM in milisecs.
	 * 
	 * @return The current BPM in millisecs.
	 */
	public int getBPMMilisecs()
	{
		return m_currentBpmMilisecs;
	}

	/**
	 * Returns whether the metronome is playing or not.
	 * 
	 * @return True if the metronome is playing, false otherwise.
	 */
	public boolean isPlaying()
	{
		return m_isPlaying;
	}

	/**
	 * Sets the current BPM.
	 * 
	 * @param newBPM
	 *            The new BPM to set.
	 */
	public void setBPM(int newBPM)
	{
		if (newBPM != 0)
		{
			// Update the value of the bpm.
			m_nCurrentBpm = newBPM;

			m_currentBpmMilisecs =
					(int) ((60f / m_nCurrentBpm) * 1000);

			// Notify the listener about the change.
			if (m_BpmChangeListener != null)
			{
				m_BpmChangeListener
						.onTempoChange(m_currentBpmMilisecs);
			}

			m_shouldUpdateBpm = true;
		}
	}

	/**
	 * Sets a tick listener.
	 * 
	 * @param listener
	 *            A listener for the tick action.
	 */
	public void setOnTickListener(OnTickListener listener)
	{
		m_TickListener = listener;
	}

	/**
	 * Sets a tick listener.
	 * 
	 * @param listener
	 *            A listener for the tick action.
	 */
	public void setOnBPMChange(OnTempoChangeListener listener)
	{
		m_BpmChangeListener = listener;
	}

}
