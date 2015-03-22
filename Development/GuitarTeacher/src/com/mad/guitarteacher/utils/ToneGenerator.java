package com.mad.guitarteacher.utils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;

public class ToneGenerator
{

	private final int		DURATION				= 3;							// seconds
	private final int		SAMPLE_RATE				= 8000;
	private final int		NUM_SAMPLES				= DURATION * SAMPLE_RATE;
	private final double	m_arSamples[]			= new double[NUM_SAMPLES];
	private double			m_FreqTone				= 100;							// hz

	private final byte		m_arGeneratedSound[]	= new byte[2 * NUM_SAMPLES];

	Handler					handler					= new Handler();
	private boolean			m_fGenerated			= false;

	public ToneGenerator(float sfFreqTone)
	{
		m_FreqTone = sfFreqTone;

	}

	private void generateTones()
	{
		// fill out the array
		for (int i = 0; i < NUM_SAMPLES; ++i)
		{
			m_arSamples[i] = Math.sin(2	* Math.PI
										* i
										/ (SAMPLE_RATE / m_FreqTone));
		}

		// convert to 16 bit pcm sound array
		// assumes the sample buffer is normalised.
		int idx = 0;
		for (double dVal : m_arSamples)
		{
			short val = (short) (dVal * 32767);
			m_arGeneratedSound[idx++] = (byte) (val & 0x00ff);
			m_arGeneratedSound[idx++] = (byte) ((val & 0xff00) >>> 8);
		}
	}

	AudioTrack	m_audioTrack;

	public void playSound()
	{

		new Thread(new Runnable()
		{

			@Override
			public synchronized void run()
			{
				if (!m_fGenerated)
				{
					m_fGenerated = true;
					generateTones();
				}
				m_audioTrack = new AudioTrack(	AudioManager.STREAM_MUSIC,
												8000,
												AudioFormat.CHANNEL_OUT_DEFAULT,
												AudioFormat.ENCODING_PCM_16BIT,
												NUM_SAMPLES,
												AudioTrack.MODE_STATIC);
				m_audioTrack.write(m_arGeneratedSound, 0, NUM_SAMPLES);
				m_audioTrack.play();
			}
		}).start();

	}

	public void stopPlay()
	{
		m_audioTrack.stop();
	}

}
