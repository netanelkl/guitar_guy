package com.mad.tunerlib.recognition;

/**
 * A buffer used for input audio samples.
 * 
 * @author Nati
 * 
 */
public class AudioReadBuffer
{
	enum EBufferStatus
	{
		eBufferStatus_Empty,
		eBufferStatus_Filling,
		eBufferStatus_Full
	};

	/**
	 * The samples.
	 */
	short[]			arSamples	=
										new short[AudioIn.READ_BUFFER_SIZE];

	/**
	 * The buffer status.
	 * 
	 * TODO: Actually check the status.
	 */
	EBufferStatus	eStatus;

	public short[] getSamples()
	{
		return arSamples;
	}

	public EBufferStatus getStatus()
	{
		return eStatus;
	}
}
