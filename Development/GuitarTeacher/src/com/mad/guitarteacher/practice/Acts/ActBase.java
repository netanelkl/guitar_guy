package com.mad.guitarteacher.practice.Acts;

import com.mad.guitarteacher.activities.IPlayingDisplayer;

/**
 * Provides a base class for an act.
 * 
 * Deriving subclasses should override
 * 
 * @author Nati
 * 
 */
public abstract class ActBase
{
	public final static int		NO_SCORE	= -1;
	public final static int		MIN_SCORE	= 0;
	public final static int		MAX_SCORE	= 100;
	/**
	 * The displayer that has displaying capabilities.
	 */
	protected IPlayingDisplayer	m_Displayer;

	/**
	 * A listener to be notified when playing the act has ended.
	 */
	private OnActDoneListener	m_OnActDoneListener;

	/**
	 * Return a score from 0 to 100, of the score for the act.
	 * 
	 * @return
	 */
	public int getScore()
	{
		return NO_SCORE;
	}

	/**
	 * This will hold the default difficulty for this act.
	 * 
	 * @return
	 */
	public int getDifficulty()
	{
		return 0;
	}

	/**
	 * Starts playing the current act.
	 * 
	 * This should trigger calling the displayer and collecting information from
	 * the Recognition package.
	 * 
	 * @param displayer
	 */
	public void beginPlayingAct(IPlayingDisplayer displayer,
								OnActDoneListener onActDoneListener)
	{
		m_Displayer = displayer;
		m_OnActDoneListener = onActDoneListener;

		displayer.setFretVisibility(true);
		beginPlayingActImpl(displayer);
	}

	/**
	 * Pauses the current act.
	 * 
	 */
	public void pause()
	{
	}

	/*
	 * implements logic to play the act.
	 * 
	 * Must be implemented by subclasses.
	 */
	public abstract void beginPlayingActImpl(IPlayingDisplayer displayer);

	/**
	 * abruptly cancels the act and trigger ending all related activities.
	 */
	public void cancelPlaying()
	{
		endPlayingAct();
	}

	/**
	 * Notifies the listeners on being done with the act.
	 */
	protected void endPlayingAct()
	{
		endPlayingAct(false);
	}

	/**
	 * Notifies the listeners on being done with the act.
	 */
	protected void endPlayingAct(boolean fSimulationMode)
	{
		m_OnActDoneListener.onActDone(this, fSimulationMode);

		m_OnActDoneListener = null;
		m_Displayer = null;
	}

	/**
	 * Resumes the current act.
	 * 
	 */
	public void resume()
	{
	}
}
