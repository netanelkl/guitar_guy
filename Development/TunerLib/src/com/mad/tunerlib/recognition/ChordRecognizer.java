package com.mad.tunerlib.recognition;

import android.util.Log;

import com.mad.lib.R;
import com.mad.lib.utils.AppLibraryServiceProvider;

/**
 * A recognizer for chords.
 * 
 * @author Nati
 * 
 */
public final class ChordRecognizer extends RecognizerBase
{
	/**
	 * How many packets to receive, before processing to retrieve results.
	 * 
	 * Because the latter might be way more computation intensive.
	 */
	static final int					FEATURE_EXTRACTION_LENGTH	=
																			5;

	private static String				TAG							=
																			"ChordRecognizer";
	/**
	 * A list of the last detected chords.
	 * 
	 * We are not translating them to chords yet because we don't know if they'd
	 * be of use.
	 */
	public SingleChordDetectionInfo[]	m_arJNICurrentChords		=
																			new SingleChordDetectionInfo[5];

	public ChordRecognizer(final IAudioInputObserverList audioHandler)
	{
		super(audioHandler);
		for (int nIndex = 0; nIndex < m_arJNICurrentChords.length; nIndex++)
		{
			m_arJNICurrentChords[nIndex] =
					new SingleChordDetectionInfo();
		}
	}

	@Override
	public void AudioPacketReceived(final short[] arSamples)
	{
		boolean fExtractFeatures = false;
		if (m_nExtractIndex == (FEATURE_EXTRACTION_LENGTH - 1))
		{
			fExtractFeatures = true;
			// Clear the array before sending it to the JNI.
			for (int i = 0; i < m_arJNICurrentChords.length; i++)
			{
				m_arJNICurrentChords[i].clear();
			}
		}
		Log.i(TAG, "received packet, pre processing.");
		INativeInterfaceWrapper nativeInterface =
				AppLibraryServiceProvider.getInstance().get(
						R.service.native_interface_wrapper);
		nativeInterface.ProcessChordData(fExtractFeatures,
				arSamples, m_arJNICurrentChords,
				SingleChordDetectionInfo.class);
		Log.i(TAG, "received packet, post processing.");

		if (m_nExtractIndex == (FEATURE_EXTRACTION_LENGTH - 1))
		{
			final ChordDetectionInfo info =
					new ChordDetectionInfo();

			for (int i = 0; i < m_arJNICurrentChords.length; i++)
			{
				if (m_arJNICurrentChords[i].isEmpty())
				{
					break;
				}
				Log.d("Chord", m_arJNICurrentChords[i].Chord);
				SingleChordDetectionInfo currentInfo =
						m_arJNICurrentChords[i];
				if (!currentInfo.Chord.equals("N"))
				{
					currentInfo.Duration =
							m_arJNICurrentChords[i + 1].Time
									- currentInfo.Time;
					info.add(currentInfo);
				}
			}

			if (info.size() > 0)
			{
				new Thread(new Runnable()
				{

					@Override
					public void run()
					{
						notifyRecognitionListener(info);
					}
				}).start();
			}
		}

		m_nExtractIndex =
				(m_nExtractIndex + 1)
						% FEATURE_EXTRACTION_LENGTH;
	}

}
