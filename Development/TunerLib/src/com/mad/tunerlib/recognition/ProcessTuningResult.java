package com.mad.tunerlib.recognition;

/**
 * The result of a note detection. It contains the detected note, and the
 * accuracy for it being the right note played.
 * 
 * @author Nati
 * 
 */
public class ProcessTuningResult
{

	int		o_nNoteIndex;
	float	o_sfProbability;
	float	o_sfFrequency;
	long	o_lTimestamp;

	ProcessTuningResult()
	{
	}

	ProcessTuningResult(final int nNoteIndex)
	{
		o_nNoteIndex = nNoteIndex;
	}
}
