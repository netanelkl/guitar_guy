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

#ifndef _FREQUENCY_FINDER_
#define _FREQUENCY_FINDER_

#include <vamp-sdk/Plugin.h>
using namespace std;

class FrequencyFinder: public Vamp::Plugin
{

public:
	enum
	{
		PEAK_COUNT = 5,
		MIN_FREQ = 60,
		MAX_FREQ = 900,
	};

	FrequencyFinder(float inputSampleRate);
	virtual ~FrequencyFinder();

	string getMaker() const;
	int getPluginVersion() const;
	string getCopyright() const;
	InputDomain getInputDomain() const;


	string getIdentifier() const;
	string getName() const;
	static string GetName();
	string getDescription() const;
	size_t getPreferredStepSize() const;

	ParameterList getParameterDescriptors() const;
	OutputList getOutputDescriptors() const;
	bool calculateFrequencies(const float* values, float& o_sfFreq,float& o_sfAccuracy);

	FeatureSet process(const float * const *inputBuffers,
			Vamp::RealTime timestamp);
	FeatureSet getRemainingFeatures();

	bool initialise(size_t channels, size_t stepSize, size_t blockSize);
	void reset();
	void findPeaks(
			const float* values,
			int peakIndices[PEAK_COUNT]);
	bool scanSignalIntervals(const float * arData, int index, int length,
			int intervalMin, int intervalMax, int& o_nOptimalInterval,
			float& o_dOptimalValue);

protected:
	mutable int m_outputTuning;
	mutable int m_outputLocalTuning;

	int usefullMinSpectr;
	int usefullMaxSpectr;
	int FFT_SIZE;
};

#endif
