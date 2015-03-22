package com.mad.tunerlib.recognition;

/**
 * A JNI descriptor class.
 * 
 * Because of the way JNI works, you should not move rename or change anything
 * in this class without changing the cpp code too.
 * 
 * @author Nati
 * 
 */
public class NativeInterface
{
	/**
	 * A struct used to retrieve the results of the
	 * 
	 * @author Nati
	 * 
	 */

	static
	{
		System.loadLibrary("Chordlib");
		Initialize();
	}

	/**
	 * Use to initialize the native library.
	 */
	public final static native void Initialize();

	/**
	 * Use to feed in samples for chord recognition. Returns noticeable chords
	 * found. Note that in any case, the samples will still be valid for the
	 * next iterations.
	 * 
	 * @param arSamples
	 *            Samples for the Chord recognizer.
	 * @return A list of chords detected so far.
	 */
	public final static native boolean ProcessChordData(boolean fExtractFeatures,
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
	public final static native boolean ProcessTuning(	short[] arSamples,
														boolean fExtractFeatures,
														ProcessTuningResult result,
														Class<ProcessTuningResult> outClass);

	/**
	 * Terminates the lib and releases all used memory.
	 */
	public final static native boolean Terminate();

}
