/* -*- c-basic-offset: 4 indent-tabs-mode: nil -*-  vi:set ts=8 sts=4 sw=4: */

/*
 NNLS-Chroma / Chordino

 Audio feature extraction plugins for chromagram and chord
 estimation.

 Centre for Digital Music, Queen Mary University of London.
 This file copyright 2008-2010 Matthias Mauch and QMUL.

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License as
 published by the Free Software Foundation; either version 2 of the
 License, or (at your option) any later version.  See the file
 COPYING included with this distribution for more information.
 */

#include "FrequencyFinder.h"

#include <cstdlib>
#include <fstream>
#include <cmath>
#include <cfloat>
#include <algorithm>

const bool debug_on = false;

string FrequencyFinder::getMaker() const
{
	if (debug_on)
		COUT("--> getMaker" << endl);
	// Your name here
	return "Matthias Mauch";
}

int FrequencyFinder::getPluginVersion() const
{
	if (debug_on)
		COUT("--> getPluginVersion" << endl);
	// Increment this each time you release a version that behaves
	// differently from the previous one
	return 3;
}

string FrequencyFinder::getCopyright() const
{
	return "";
}

Vamp::Plugin::InputDomain FrequencyFinder::getInputDomain() const
{
	return FrequencyDomain;
}

FrequencyFinder::FrequencyFinder(float inputSampleRate) :
		m_outputTuning(0), FFT_SIZE(0), m_outputLocalTuning(0), usefullMaxSpectr(
				0), usefullMinSpectr(0), Plugin(inputSampleRate)
{
}

FrequencyFinder::~FrequencyFinder()
{
	if (debug_on)
		CERR("--> ~Tuning" << endl);
}

size_t FrequencyFinder::getPreferredStepSize() const
{
	if (debug_on)
		CERR("--> getPreferredStepSize" << endl);
	return 2048 * 4;
}

string FrequencyFinder::getIdentifier() const
{
	if (debug_on)
		CERR("--> getIdentifier" << endl);
	return "tuning";
}

string FrequencyFinder::getName() const
{
	return GetName();
}
string FrequencyFinder::GetName()
{
	return "FrequencyFinder";
}

string FrequencyFinder::getDescription() const
{
	// Return something helpful here!
	if (debug_on)
		CERR("--> getDescription" << endl);
	return "The tuning plugin can estimate the local and global tuning of piece. The same tuning method is used for the NNLS Chroma and Chordino plugins.";
}

FrequencyFinder::ParameterList FrequencyFinder::getParameterDescriptors() const
{
	if (debug_on)
		CERR("--> getParameterDescriptors" << endl);
	ParameterList list;

	ParameterDescriptor d0;
	d0.identifier = "rollon";
	d0.name = "spectral roll-on";
	d0.description =
			"Consider the cumulative energy spectrum (from low to high frequencies). All bins below the first bin whose cumulative energy exceeds the quantile [spectral roll on] x [total energy] will be set to 0. A value of 0 means that no bins will be changed.";
	d0.unit = "%";
	d0.minValue = 0;
	d0.maxValue = 5;
	d0.defaultValue = 0;
	d0.isQuantized = true;
	d0.quantizeStep = 0.5;
	list.push_back(d0);

	return list;
}

FrequencyFinder::OutputList FrequencyFinder::getOutputDescriptors() const
{
	if (debug_on)
		CERR("--> getOutputDescriptors" << endl);
	OutputList list;

	int index = 0;

	OutputDescriptor d0;
	d0.identifier = "tuning";
	d0.name = "Tuning";
	d0.description =
			"Returns a single label (at time 0 seconds) containing an estimate of the concert pitch in Hz.";
	d0.unit = "Hz";
	d0.hasFixedBinCount = true;
	d0.binCount = 1;
	d0.hasKnownExtents = true;
	d0.minValue = 427.47;
	d0.maxValue = 452.89;
	d0.isQuantized = false;
	d0.sampleType = OutputDescriptor::VariableSampleRate;
	d0.hasDuration = true;
	list.push_back(d0);
	m_outputTuning = index++;

	OutputDescriptor d10;
	d10.identifier = "localtuning";
	d10.name = "Local Tuning";
	d10.description =
			"Returns a tuning estimate at every analysis frame, an average of the (recent) previous frame-wise estimates of the concert pitch in Hz.";
	d10.unit = "Hz";
	d10.hasFixedBinCount = true;
	d10.binCount = 1;
	d10.hasKnownExtents = true;
	d10.minValue = 427.47;
	d10.maxValue = 452.89;
	d10.isQuantized = false;
	d10.sampleType = OutputDescriptor::FixedSampleRate;
	d10.hasDuration = false;
	// d10.sampleRate = (m_stepSize == 0) ? m_inputSampleRate/2048 : m_inputSampleRate/m_stepSize;
	list.push_back(d10);
	m_outputLocalTuning = index++;

	return list;
}

bool FrequencyFinder::initialise(size_t channels, size_t stepSize,
		size_t blockSize)
{
	FFT_SIZE = blockSize;
	usefullMinSpectr = max(0, (int) (MIN_FREQ * FFT_SIZE / m_inputSampleRate));

	usefullMaxSpectr = min(FFT_SIZE,
			(int) (MAX_FREQ * FFT_SIZE / m_inputSampleRate) + 1);

	return true;
}

void FrequencyFinder::reset()
{
}

bool FrequencyFinder::scanSignalIntervals(const float * arData, int index,
		int length, int intervalMin, int intervalMax, int& o_nOptimalInterval,
		float& o_dOptimalValue)
{
	o_dOptimalValue = FLT_MAX;
	o_nOptimalInterval = 0;

	// distance between min and max range value can be big
	// limiting it to the fixed value
	const int MaxAmountOfSteps = 30;
	int steps = intervalMax - intervalMin;
	if (steps > MaxAmountOfSteps)
		steps = MaxAmountOfSteps;
	else if (steps <= 0)
		steps = 1;

	// trying all intervals in the range to find one with
	// smaller difference in signal waves
	for (int i = 0; i < steps; i++)
	{
		int interval = intervalMin + (intervalMax - intervalMin) * i / steps;

		double sum = 0;
		for (int j = 0; j < length; j++)
		{
			double diff = arData[index + j] - arData[index + j + interval];
			sum += diff * diff;
		}
		if (o_dOptimalValue > sum)
		{
			o_dOptimalValue = sum;
			o_nOptimalInterval = interval;
		}
	}
	return true;
}

void FrequencyFinder::findPeaks(const float* values,
		int peakIndices[PEAK_COUNT])
{
	float peakValues[PEAK_COUNT];

	for (int i = 0; i < PEAK_COUNT; i++)
	{
		peakValues[i] = values[peakIndices[i] = i + usefullMinSpectr];
	}

	// find min peaked value
	float minStoredPeak = peakValues[0];
	int minIndex = 0;
	for (int i = 1; i < PEAK_COUNT; i++)
	{
		if (minStoredPeak > peakValues[i])
		{
			minStoredPeak = peakValues[minIndex = i];
		}
	}

	for (int i = PEAK_COUNT; i < usefullMaxSpectr - usefullMinSpectr; i++)
	{
		if (minStoredPeak < values[i + usefullMinSpectr])
		{
			// replace the min peaked value with bigger one
			peakValues[minIndex] = values[peakIndices[minIndex] = i
					+ usefullMinSpectr];

			// and find min peaked value again
			minStoredPeak = peakValues[minIndex = 0];
			for (int j = 1; j < PEAK_COUNT; j++)
			{
				if (minStoredPeak > peakValues[j])
				{
					minStoredPeak = peakValues[minIndex = j];
					YELL_NUM(minStoredPeak);
				}
			}
		}
	}

}

bool FrequencyFinder::calculateFrequencies(const float* values, float& o_sfFreq,
		float& o_sfAccuracy)
{
	int arPeakIndices[PEAK_COUNT];
	int nOptimalInterval;
	float sfOptimalValue;
	int minPeakIndex;
	float sfOptimalInterval;
	o_sfFreq = 0;
	o_sfAccuracy = 0;
	// find peaks in the FFT frequency bins
	findPeaks(values, arPeakIndices);

	for (int i = 0; i < sizeof(arPeakIndices); i++)
	{
		if (usefullMinSpectr == arPeakIndices[i])
		{
			// lowest usefull frequency bin shows active
			// looks like is no detectable sound, return 0
			YELL_NUM(usefullMinSpectr);
			RETURN_FALSE();
		}
	}
	YELL();
	float minPeakValue;
	// select fragment to check peak values: data offset
	const int verifyFragmentOffset = 0;
	// ... and half length of data
	int verifyFragmentLength = (int) (m_inputSampleRate / MIN_FREQ);

	// trying all peaks to find one with smaller difference value
	for (int i = 0; i < sizeof(arPeakIndices); i++)
	{
		int index = arPeakIndices[i];
		int binIntervalStart = FFT_SIZE / (index + 1), binIntervalEnd = FFT_SIZE
				/ index;

		// scan bins frequencies/intervals

		scanSignalIntervals(values, verifyFragmentOffset, verifyFragmentLength,
				binIntervalStart, binIntervalEnd, nOptimalInterval,
				sfOptimalValue);

		arPeakIndices[i] = (double) m_inputSampleRate / nOptimalInterval;
		if (sfOptimalValue < minPeakValue)
		{
			minPeakValue = sfOptimalValue;
			minPeakIndex = index;
			sfOptimalInterval = nOptimalInterval;
		}
	}

	YELL();
	// return (double) nSampleRate / minOptimalInterval;
	o_sfAccuracy = (1 - sfOptimalInterval) / m_inputSampleRate;
	o_sfFreq = m_inputSampleRate / sfOptimalInterval;
	return true;
}

FrequencyFinder::FeatureSet FrequencyFinder::process(
		const float * const *inputBuffers, Vamp::RealTime timestamp)
{
	if (debug_on)
		CERR("--> process" << endl);

	// Do process here.
	float sfFreq;
	float sfAccuracy;
	YELL();
//	LOG_BUFFER(inputBuffers[0], FFT_SIZE);
	calculateFrequencies(inputBuffers[0], sfFreq, sfAccuracy);

	Feature f10; // local tuning
	f10.hasTimestamp = true;
	f10.timestamp = timestamp;
//	float normalisedtuning = m_localTuning[m_localTuning.size() - 1];
//	float tuning440 = 440 * pow(2, normalisedtuning / 12);
	f10.values.push_back(sfFreq);
	f10.values.push_back(sfAccuracy);

	FeatureSet fs;
	fs[m_outputLocalTuning].push_back(f10);
	return fs;
}

FrequencyFinder::FeatureSet FrequencyFinder::getRemainingFeatures()
{
	if (debug_on)
		CERR("--> getRemainingFeatures" << endl);
	FeatureSet fsOut;
//	if (m_logSpectrum.size() == 0)
//		return fsOut;
//
//	//
//	/**  Calculate Tuning
//	 calculate tuning from (using the angle of the complex number defined by the
//	 cumulative mean real and imag values)
//	 **/
//
//	float meanTuningImag = 0;
//	float meanTuningReal = 0;
//	for (int iBPS = 0; iBPS < eChromaMethodsBPS; ++iBPS)
//	{
//		meanTuningReal += m_meanTunings[iBPS] * cosvalues[iBPS];
//		meanTuningImag += m_meanTunings[iBPS] * sinvalues[iBPS];
//	}
//
//	float cumulativetuning = 440
//			* pow(2, atan2(meanTuningImag, meanTuningReal) / (24 * M_PI));
//
//	char buffer0[50];
//
//	sprintf(buffer0, "%0.1f Hz", cumulativetuning);

	// push tuning to FeatureSet fsOut
//	Feature f0; // tuning
//	f0.hasTimestamp = true;
//	f0.timestamp = Vamp::RealTime::frame2RealTime(0, lrintf(m_inputSampleRate));
//	f0.values.push_back(cumulativetuning);
//	f0.label = buffer0;
//	f0.hasDuration = true;
//	f0.duration = m_logSpectrum[m_logSpectrum.size() - 1].timestamp;
//	fsOut[m_outputTuning].push_back(f0);

	return fsOut;

}

