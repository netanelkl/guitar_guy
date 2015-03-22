package com.mad.tunerlib.recognition;

/**
 * This is the actual native implementation caller.
 * 
 * @author Nati
 * 
 */
public class RealNativeInterfaceWrapper implements
		INativeInterfaceWrapper
{
	@Override
	public void Initialize()
	{
		NativeInterface.Initialize();
	}

	@Override
	public boolean ProcessChordData(final boolean fExtractFeatures,
									final short[] arSamples,
									final SingleChordDetectionInfo[] o_arChords,
									final Class<SingleChordDetectionInfo> outClass)
	{
		return NativeInterface.ProcessChordData(
				fExtractFeatures, arSamples, o_arChords,
				outClass);
	}

	@Override
	public boolean ProcessTuning(	final short[] arSamples,
									final boolean fExtractFeatures,
									final ProcessTuningResult result,
									final Class<ProcessTuningResult> outClass)
	{

		return NativeInterface.ProcessTuning(arSamples,
				fExtractFeatures, result, outClass);
	}

	@Override
	public boolean Terminate()
	{
		return NativeInterface.Terminate();
	}

}
