package com.mad.tunerlib.recognition;

import com.mad.lib.R;
import com.mad.lib.tuning.FrequencyNormalizer;
import com.mad.lib.utils.AppLibraryServiceProvider;

/**
 * Recognizes notes played.
 * 
 * You should use this class when in need of detecting a played note /
 * frequency.
 * 
 * @author Nati
 * 
 */
public final class NoteRecognizer extends RecognizerBase

{
	/**
	 * How many packets to receive, before processing to retrieve results.
	 * 
	 * Because the latter might be way more computation intensive.
	 */
	static final int	FEATURE_EXTRACTION_LENGTH	= 2;

	/*
	 * This inner class gives information about detection of a note.
	 */
	public class NoteDetectionInfo extends DetectionInfo
			implements Cloneable
	{
		/**
		 * The type of the note.
		 */
		public int		NoteIndex;

		/**
		 * The probabability that that's the note that was being played and that
		 * it was played correctly.
		 */
		public float	Probability;

		/**
		 * The probabability that that's the note that was being played and that
		 * it was played correctly.
		 */
		public float	Frequency;

		/**
		 * The probabability that that's the note that was being played and that
		 * it was played correctly.
		 */
		public long		Timestamp;

		public NoteDetectionInfo()
		{
		}

		public NoteDetectionInfo(	final float sfProbability,
									final int nNoteIndex)
		{
			Probability = sfProbability;
			NoteIndex = nNoteIndex;
		}

		@Override
		protected Object clone()
		{
			NoteDetectionInfo info = new NoteDetectionInfo();
			info.Frequency = Frequency;
			info.Probability = Probability;
			info.NoteIndex = NoteIndex;
			return info;
		}
	}

	private static final float			MIN_FREQUENCY	= 80;
	private final FrequencyNormalizer	m_FrequencyNormalizer;

	public NoteRecognizer(final IAudioInputObserverList audioHandler)
	{
		super(audioHandler);
		m_FrequencyNormalizer =
				FrequencyNormalizer.createDefaultNormalizer();
	}

	@Override
	public synchronized void AudioPacketReceived(final short[] arSamples)
	{
		int nNoteIndex = 0;
		boolean fExtractFeatures = false;
		ProcessTuningResult result =
				new ProcessTuningResult(nNoteIndex);
		// Will the native really be able to change these?
		if (m_nExtractIndex == (FEATURE_EXTRACTION_LENGTH - 1))
		{
			INativeInterfaceWrapper nativeInterface =
					AppLibraryServiceProvider.getInstance().get(
							R.service.native_interface_wrapper);
			fExtractFeatures = true;
			if (nativeInterface.ProcessTuning(arSamples,
					fExtractFeatures, result,
					ProcessTuningResult.class))
			{

				{
					if (result.o_sfFrequency > NoteRecognizer.MIN_FREQUENCY)
					{
						final NoteDetectionInfo info =
								new NoteDetectionInfo();
						// TODO: Store the info locally.
						info.Probability =
								result.o_sfProbability;

						// Notes are absolute given 49==440Hz, so considering
						// that,
						// FIRST_GUITAR_NOTE is the first playable note (Lowest
						// E).
						// But we want to move it to our space in which 0 is C,
						// so
						// we
						// add the E.
						int nAsGuitarNote =
								m_FrequencyNormalizer
										.getClosestNote(result.o_sfFrequency);
						info.NoteIndex = nAsGuitarNote;
						info.Frequency = result.o_sfFrequency;
						info.Timestamp = result.o_lTimestamp;
						notifyRecognitionListener(info);
					}
					// new Thread(new Runnable()
					// {
					//
					// @Override
					// public void run()
					// {
					//
					// }
					// }).start();
				}
			}
		}

		m_nExtractIndex =
				(m_nExtractIndex + 1)
						% FEATURE_EXTRACTION_LENGTH;
	}
}
