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

#ifndef _TUNING_
#define _TUNING_

#include "NNLSBase.h"
#include <map>
#include <vamp-sdk/Plugin.h>
#include "PitchDetector.h"
using namespace std;

class Tuning  : public Vamp::Plugin
{
public:
	Tuning(float inputSampleRate);
	virtual ~Tuning();

    string getMaker() const
    {
    	return "com.mad.guitarteacher";
    }
    int getPluginVersion() const
    {
    	return 1;
    }
    string getCopyright() const
    {
    	return "copyright of Netanel Kadar Levi.";
    }

    InputDomain getInputDomain() const
    {
    	return TimeDomain;
    }

	string getIdentifier() const;
	string getName() const;
	string getDescription() const;
	std::size_t getPreferredStepSize() const
	{
		return NNLSBase::StepSize;
	}
	size_t getPreferredBlockSize() const
	{
		return NNLSBase::BlockSize;
	}

	ParameterList getParameterDescriptors() const;
	OutputList getOutputDescriptors() const;

	FeatureSet process(const float * const *inputBuffers,
			Vamp::RealTime timestamp);
	FeatureSet getRemainingFeatures();

	bool initialise(size_t channels, size_t stepSize, size_t blockSize);
	void reset();

protected:
	mutable int m_outputTuning;
	mutable int m_outputLocalTuning;

private:
	double unwrapPhase( int fBin ) ;
	double hps(const float *fbuf);
	struct NoteDetectionInfo
	{
		PitchDetector::DetectionResult detectionInfo;
		int nOccurences;
		long dTimestamp;
		NoteDetectionInfo()
		{
			nOccurences = 0;
			dTimestamp = 0;
		}
	};

	static const int MIN_OCCURENCAS = 3;
	typedef map<int, NoteDetectionInfo> NoteDetectionMap;
	NoteDetectionMap m_mapDetections;
	PitchDetector* m_pPitchDetector;
	int m_BlockSize;

};

#endif
