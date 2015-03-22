package com.mad.tunerlib.recognition;

/**
 * An interface between the java and the native code (JNI).
 * 
 * @author Nati
 * 
 */
public interface INativeInterfaceWrapper
{
	/**
	 * Use to initialize the native library.
	 */
	public void Initialize();

	/**
	 * Use to feed in samples for chord recognition. Returns noticable chords
	 * found. Note that in any case, the samples will still be valid for the
	 * next iterations.
	 * 
	 * @param arSamples
	 *            Samples for the Chord recognizer.
	 * @return A list of chords detected so far.
	 */
	public boolean ProcessChordData(boolean fExtractFeatures,
									short[] arSamples,
									SingleChordDetectionInfo[] o_arChords,
									Class<SingleChordDetectionInfo> outClass);

	/**
	 * Detects the played frequency, for a tuner.
	 * 
	 * @param arSamples
	 *            Samples for the frequency recognizer.
	 * @param o_sfFrequency
	 *            The frequency found. 0 if too weak of a signal.
	 */
	public boolean ProcessTuning(	short[] arSamples,
									boolean fExtractFeatures,
									ProcessTuningResult result,
									Class<ProcessTuningResult> outClass);

	/**
	 * Terminates the lib and releases all used memory.
	 */
	public boolean Terminate();

}
