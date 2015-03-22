package com.mad.tunerlib.recognition;

import java.util.Random;

import com.mad.lib.musicalBase.Notes;
import com.mad.lib.tuning.FrequencyNormalizer;

/**
 * This is used to simulate native in scenarios where input audio is unavailable
 * or just too uncomfortable to create.
 * 
 * @author Nati
 * 
 */
public class TestingNativeInterfaceWrapper implements
		INativeInterfaceWrapper
{
	private final String[]				m_arChords		=
																new String[] {
			"Amaj", "Amin", "Bmaj", "Cmaj", "Dmaj", "Dmin",
			"Emaj", "Emin", "Fmaj", "Gmaj"						};
	private final FrequencyNormalizer	m_Normalizer	=
																FrequencyNormalizer
																		.createDefaultNormalizer();
	Random								rand			=
																new Random();

	/**
	 * Use to initialize the native library.
	 */
	@Override
	public void Initialize()
	{

	}

	/**
	 * Use to feed in samples for chord recognition. Returns noticable chords
	 * found. Note that in any case, the samples will still be valid for the
	 * next iterations.
	 * 
	 * @param arSamples
	 *            Samples for the Chord recognizer.
	 * @return A list of chords detected so far.
	 */
	@Override
	public boolean ProcessChordData(final boolean fExtractFeatures,
									final short[] arSamples,
									final SingleChordDetectionInfo[] o_arChords,
									final Class<SingleChordDetectionInfo> outClass)
	{
		SingleChordDetectionInfo resultInfo =
				new SingleChordDetectionInfo();
		resultInfo.Chord =
				m_arChords[rand.nextInt(m_arChords.length)];
		resultInfo.Duration = 0.5f;
		resultInfo.Probability = 0.92f;
		o_arChords[0] = resultInfo;
		return true;
	}

	/**
	 * Detects the played frequency, for a tuner.
	 * 
	 * @param arSamples
	 *            Samples for the frequency recognizer.
	 * @param o_sfFrequency
	 *            The frequency found. 0 if too weak of a signal.
	 */
	@Override
	public boolean ProcessTuning(	final short[] arSamples,
									final boolean fExtractFeatures,
									final ProcessTuningResult result,
									final Class<ProcessTuningResult> outClass)
	{
		// If you want a real evaluation, use 84 (the number of notes), if you
		// want to always hit the first five frets you should use the second
		// one.
		boolean fAlwaysFirstFiveFrets = true;

		int MAX_FREQ_INDEX = 84;
		if (fAlwaysFirstFiveFrets)
		{
			result.o_nNoteIndex = (rand.nextInt(51) + 19);
		}
		else
		{
			result.o_nNoteIndex =
					(rand.nextInt(MAX_FREQ_INDEX - 1) + 1);
		}

		result.o_sfProbability = 0.91f;

		int nShiftedNote =
				result.o_nNoteIndex
						- Notes.FIRST_ABSOLUTE_GUITAR_INDEX;
		float sfNoteFreq =
				m_Normalizer
						.getFrequencyByNoteIndex(nShiftedNote);

		// result.o_sfFrequency =
		// (rand.nextFloat() * (sfNextFreq - sfPrevFreq))
		// + sfPrevFreq;
		result.o_sfFrequency = sfNoteFreq;
		return true;
	}

	/**
	 * Terminates the lib and releases all used memory.
	 */
	@Override
	public boolean Terminate()
	{
		return NativeInterface.Terminate();
	}

}
