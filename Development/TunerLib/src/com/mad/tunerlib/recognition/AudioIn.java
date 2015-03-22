package com.mad.tunerlib.recognition;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioRecord.OnRecordPositionUpdateListener;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

import com.mad.lib.utils.ErrorHandler;
import com.mad.tunerlib.recognition.AudioReadBuffer.EBufferStatus;

/**
 * The class responsible for listening for audioinput
 * 
 * @author Nati
 * 
 */
public class AudioIn extends Thread implements
		OnRecordPositionUpdateListener, IAudioInputObserverList
{
	private static String								TAG						=
																						"AudioIn";
	public final static int								SAMPLING_RATE			=
																						44100;

	public final static int								BYTES_IN_SAMPLE			=
																						2;
	public final static int								READ_BUFFER_SIZE		=
																						16384;

	/**
	 * The number of buffers pending processing.
	 */
	private static final int							BUFFERS_COUNT			=
																						20;

	/**
	 * A list of observers for incoming audio packets.
	 */
	private final ArrayList<IAudioInputObserver>		m_arAudioInputHandlers	=
																						new ArrayList<IAudioInputObserver>();

	/**
	 * A boolean indicating if we are currently listening.
	 */
	private volatile boolean							m_fRunning				=
																						false;

	/**
	 * A thread used to process the incoming packets.
	 * 
	 * This happens asynchronously to the blocking read process.
	 */
	Thread												m_ProcessingThread		=
																						new Thread()
																						{
																							@Override
																							public void run()
																							{
																								try
																								{
																									processingAudioThread();
																								}
																								catch (InterruptedException e)
																								{
																									e.printStackTrace();
																								}
																							};
																						};

	/**
	 * The blocking queue for incoming packets.
	 */
	private final ArrayBlockingQueue<AudioReadBuffer>	m_queReadyBuffers		=
																						new ArrayBlockingQueue<AudioReadBuffer>(BUFFERS_COUNT);

	/**
	 * Cyclic array of buffers for audio input.
	 */
	AudioReadBuffer[]									m_ReadBuffers			=
																						new AudioReadBuffer[BUFFERS_COUNT];

	/**
	 * Semaphore to make sure the handling and registering/unregistering process
	 * happens not at the same time.
	 */
	// private final Semaphore m_semHandlers =
	// new Semaphore(1);
	private final Object								m_lockHandlers			=
																						new Object();

	public AudioIn(final Context context)
	{
		init(context);
		// m_ProcessingThread.start();
		// start();
	}

	/**
	 * Adds the observer to the list.
	 * 
	 * @param pObserver
	 */
	private void addToList(final IAudioInputObserver pObserver)
	{
		if (!m_arAudioInputHandlers.contains(pObserver))
		{
			m_arAudioInputHandlers.add(pObserver);
		}

		if (m_arAudioInputHandlers.size() == 1)
		{
			m_fRunning = true;
			synchronized (this)
			{
				notify();
			}
		}
	}

	/**
	 * Allocates the buffers for listening.
	 * 
	 * @param context
	 *            The current context.
	 */
	public void init(final Context context)
	{
		for (int nIndex = 0; nIndex < m_ReadBuffers.length; nIndex++)
		{
			m_ReadBuffers[nIndex] = new AudioReadBuffer();
		}
		m_ProcessingThread.start();
	}

	@Override
	public void onMarkerReached(final AudioRecord arg0)
	{
	}

	@Override
	public void onPeriodicNotification(final AudioRecord recorder)
	{

	}

	/**
	 * Processes an input audio packet.
	 * 
	 * @throws InterruptedException
	 */
	private void processingAudioThread() throws InterruptedException
	{
		while (true)
		{
			final AudioReadBuffer arReadBuffer =
					m_queReadyBuffers.take();
			synchronized (arReadBuffer)
			{

				try
				{
					// Log.i(TAG, "PreAcquire");
					// m_semHandlers.acquire();
					synchronized (m_lockHandlers)
					{
						if (m_arAudioInputHandlers.size() == 0)
						{
							m_queReadyBuffers.clear();
						}
						else
						{

							// Notify the observers.
							for (IAudioInputObserver observer : m_arAudioInputHandlers)
							{
								// Log.i(TAG, "PreProcess");
								observer.AudioPacketReceived(arReadBuffer.arSamples);
								// Log.i(TAG, "PostProcess");
							}
						}
						// Log.i(TAG, "PreRelease");
					}
					// m_semHandlers.release();
					// Log.i(TAG, "PostRelease");
				}
				catch (Exception ex)
				{
					ErrorHandler.HandleError(ex);
				}
			}
		}
	}

	/**
	 * Register an observable to the audio observer.
	 * 
	 * @param IAudioInputObserver
	 *            pObserver - Observer to be registered.
	 * 
	 * @return boolean - true if succeeded.
	 */
	@Override
	public boolean register(final IAudioInputObserver pObserver)
	{
		// try
		{
			// m_semHandlers.acquire();
			synchronized (m_lockHandlers)
			{
				addToList(pObserver);
			}
			// m_semHandlers.release();
		}
		// catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return true;
	}

	/**
	 * unregister an observer.
	 * 
	 * @param pObserver
	 * @return
	 */
	private boolean removeFromList(final IAudioInputObserver pObserver)
	{
		m_arAudioInputHandlers.remove(pObserver);
		if (m_arAudioInputHandlers.size() == 0)
		{
			terminate();
		}
		return true;
	}

	@Override
	public void run()
	{
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		AudioRecord recorder = null;

		int ix = 0;

		try
		{ // ... initialize
			recorder =
					new AudioRecord(AudioSource.DEFAULT,
									AudioIn.SAMPLING_RATE,
									AudioFormat.CHANNEL_IN_MONO,
									AudioFormat.ENCODING_PCM_16BIT,
									AudioIn.READ_BUFFER_SIZE
											* AudioIn.BYTES_IN_SAMPLE);

			// ... loop
			recorder.setPositionNotificationPeriod(AudioIn.READ_BUFFER_SIZE);
			recorder.setRecordPositionUpdateListener(this);
			int nReadBytes;
			while (true)
			{
				if (!m_fRunning)
				{
					recorder.stop();
					synchronized (this)
					{
						wait();
					}
				}
				recorder.startRecording();

				AudioReadBuffer readBuffer =
						m_ReadBuffers[ix++
								% m_ReadBuffers.length];
				synchronized (readBuffer)
				{
					// Log.d("RECORDING", "Writing buffer"
					// + readBuffer);
					readBuffer.eStatus =
							EBufferStatus.eBufferStatus_Filling;
					short[] arBuffer = readBuffer.arSamples;
					for (int i = 0; i < arBuffer.length; i++)
					{
						arBuffer[i] = 0;
					}

					nReadBytes =
							recorder.read(arBuffer, 0,
									arBuffer.length);
					// Log.i(TAG, "buffer received");
					readBuffer.eStatus =
							EBufferStatus.eBufferStatus_Full;
					m_queReadyBuffers.put(readBuffer);
				}
				if ((AudioRecord.ERROR_INVALID_OPERATION == nReadBytes)
						|| (nReadBytes == AudioRecord.ERROR_BAD_VALUE))
				{
					Log.e("AUDIO_IN/ERROR", "Read failed :(");
				}

			}
		}
		catch (Throwable x)
		{
			Log.e("AUDIO/IN", "Error reading voice audio", x);
		}
		finally
		{
			if ((recorder != null)
					&& (recorder.getState() == AudioRecord.STATE_INITIALIZED))
			{
				recorder.stop();
			}
		}
	}

	/**
	 * stops listening.
	 */
	public void terminate()
	{
		m_fRunning = false;
	}

	/**
	 * Unregister a registerd observable.
	 * 
	 * @param IAudioInputObserver
	 *            pObserver - observable to unregister.
	 * 
	 * @return boolean - true if succeeded.
	 */
	@Override
	public boolean unregister(final IAudioInputObserver pObserver)
	{
		// try
		{
			// m_semHandlers.acquire();

			synchronized (m_lockHandlers)
			{
				removeFromList(pObserver);
			}

			// m_semHandlers.release();
		}
		// catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return true;
	}

}
