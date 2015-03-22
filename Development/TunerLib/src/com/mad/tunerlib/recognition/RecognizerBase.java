package com.mad.tunerlib.recognition;

/**
 * A class for the processing of audio packets.
 * 
 * The class allows activation of the processing of incoming audio packets. It
 * enables the registration of listeners for when the specific recognition event
 * occurs (A note was found, etc...).
 * 
 * @author Nati
 * 
 */
public abstract class RecognizerBase implements
		IAudioInputObserver
{

	/**
	 * This is the class responsible for receiving the packets and telling us
	 * when one is ready.
	 */
	private final IAudioInputObserverList	m_AudioInputManager;

	/**
	 * Hold the son of a bitch waiting to get the results.
	 */
	private OnRecognitionDetectedListener	m_AwaitingRecognitionListener;

	/**
	 * The index to know when to call the extraction processing.
	 */
	int										m_nExtractIndex	= 0;

	/**
	 * Creates a Recognizer based on the following input manager, which happens
	 * to maintain a list of IAudioInputObservers.
	 * 
	 * @param audioHandler
	 *            The audio packets manager.
	 */
	protected RecognizerBase(final IAudioInputObserverList audioHandler)
	{
		m_AudioInputManager = audioHandler;

	}

	/**
	 * Starts listening for inputs.
	 * 
	 * Subclasses should initialize any native data to prepare for such a read.
	 */
	public synchronized void beginListening(final OnRecognitionDetectedListener listener)
	{
		m_AudioInputManager.register(this);
		m_AwaitingRecognitionListener = listener;
	}

	/**
	 * Should be called by subclasses to notify Recognition listener about a
	 * recognition event.
	 */
	protected synchronized void notifyRecognitionListener(DetectionInfo info)
	{
		if (m_AwaitingRecognitionListener == null)
		{
			// The son of a beach started recognition without even listening.
			// Report error.

			return;
		}

		m_AwaitingRecognitionListener
				.onRecognitionDetected(info);
	}

	/**
	 * Stops listening for inputs.
	 * 
	 * Subclasses should terminate any native calls and release resources if
	 * allocated.
	 */
	public void stopListening()
	{
		m_AudioInputManager.unregister(this);
		m_AwaitingRecognitionListener = null;
	}

}
