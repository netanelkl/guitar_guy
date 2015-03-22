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

#include "Tuning.h"

#include "chromamethods.h"
#include <cstdlib>
#include <fstream>
#include <cmath>
#include <set>
#include <algorithm>
#include <complex>
#define TWELVE_ROOT_TWO (1.0594630943592952645618252949463)
#define NOTE_OF_440		 (49)
#define NOTE_EDGE_DISTANCE	(2)

Tuning::Tuning(float inputSampleRate) :
		m_outputLocalTuning(0), m_outputTuning(0), m_pPitchDetector(NULL), Plugin(
				inputSampleRate)
{
}

Tuning::~Tuning()
{
	DELETE_POINTER(m_pPitchDetector);
}

string Tuning::getIdentifier() const
{
	return "tuning";
}

string Tuning::getName() const
{
	return "Tuning";
}

string Tuning::getDescription() const
{
	// Return something helpful here!
	return "The tuning plugin can estimate the local and global tuning of piece. The same tuning method is used for the NNLS Chroma and Chordino plugins.";
}

Tuning::ParameterList Tuning::getParameterDescriptors() const
{
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

Tuning::OutputList Tuning::getOutputDescriptors() const
{
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

bool Tuning::initialise(size_t channels, size_t stepSize, size_t blockSize)
{
	static const int CONST_FFT_SIZE = 32768;
	m_pPitchDetector = new PitchDetector(blockSize, stepSize,
			m_inputSampleRate, CONST_FFT_SIZE);
	m_BlockSize = blockSize;
	return true;
}

void Tuning::reset()
{
	m_mapDetections.clear();
}

Tuning::FeatureSet Tuning::process(const float * const *inputBuffers,
		Vamp::RealTime timestamp)
{
//	float* arBuffer = m_pPitchDetector->getInBuffer();

//	memcpy(arBuffer, inputBuffers[0], NNLSBase::BlockSize);

	PitchDetector::DetectionResult result = m_pPitchDetector->getPitch();
	if (result.sfProbability > 0)
	{
//		VAR_TELLER(result.dFrequency);
//		VAR_TELLER(result.sfProbability);

// We want to mark that note.

		NoteDetectionInfo& rInMap = m_mapDetections[result.nBin];

		rInMap.detectionInfo = result;
		// Just calculate the new average.
		rInMap.detectionInfo.sfProbability =
				((rInMap.detectionInfo.sfProbability * rInMap.nOccurences)
						/ (rInMap.nOccurences + 1))
						+ (rInMap.detectionInfo.sfProbability
								/ (rInMap.nOccurences + 1));
		rInMap.dTimestamp = now_ms();
		rInMap.nOccurences++;
	}
	return FeatureSet();
}

Tuning::FeatureSet Tuning::getRemainingFeatures()
{
	FeatureSet fs;

	NoteDetectionMap::iterator itStrongestBin = m_mapDetections.begin();
// First, figure out who's the strongest note so far.
	for (NoteDetectionMap::iterator itElems = m_mapDetections.begin();
			itElems != m_mapDetections.end(); itElems++)
	{
		if (itElems != m_mapDetections.begin()
				&& itStrongestBin->second.nOccurences
						< itElems->second.nOccurences)
		{
			itStrongestBin = itElems;
		}
	}

// If we have what to work with at all.
	if (itStrongestBin == m_mapDetections.end()
			|| itStrongestBin->second.nOccurences < MIN_OCCURENCAS)
	{
		return fs;
	}

//// Try to find half of the frequency to look for a hidden fundemental.
	int nCurrentBin = itStrongestBin->first;
	int nSubharmonicBin = nCurrentBin / 2;

//	const int SUBHARMONIC_SEARCH_WINDOW = 1;
	NoteDetectionInfo* pChosenFundemental = &itStrongestBin->second;
//	if (nSubharmonicBin > SUBHARMONIC_SEARCH_WINDOW)
//	{
//		for (int i = nSubharmonicBin - SUBHARMONIC_SEARCH_WINDOW;
//				i <= nSubharmonicBin + SUBHARMONIC_SEARCH_WINDOW; i++)
//		{
//			NoteDetectionInfo& rInfo = m_mapDetections[i];
////			VAR_TELLER(rInfo.nOccurences);
////			VAR_TELLER(rInfo.detectionInfo.dFrequency);
//			if (rInfo.nOccurences < MIN_OCCURENCAS
//					|| rInfo.detectionInfo.sfProbability == 0)
//			{
//				continue;
//			}
////			YELL();
//
//			// Should never happen but just in case.
//			if (itStrongestBin->second.detectionInfo.dFrequency
//					<= rInfo.detectionInfo.dFrequency)
//			{
//				continue;
//			}
////			YELL();
//
//			static const int MAX_ACCEPTED_FUNDEMENTAL_DIFFERENCE_FREQ = 3; // In Hz
//			double dDiff = itStrongestBin->second.detectionInfo.dFrequency
//					- rInfo.detectionInfo.dFrequency;
//			if (std::abs(dDiff - rInfo.detectionInfo.dFrequency)
//					<= MAX_ACCEPTED_FUNDEMENTAL_DIFFERENCE_FREQ)
//			{
//				// We've got a winner!
//				COUT("Changed to subHarmony");
//				pChosenFundemental = &rInfo;
//				break;
//			}
////			YELL();
//		}
//	}

	Feature f10; // local tuning
	f10.hasTimestamp = false;
	f10.values.push_back(pChosenFundemental->detectionInfo.nBin / m_BlockSize);
	f10.values.push_back(pChosenFundemental->detectionInfo.sfProbability);
	f10.values.push_back(pChosenFundemental->detectionInfo.dFrequency);
	f10.values.push_back(pChosenFundemental->dTimestamp);
//	VAR_TELLER(pChosenFundemental->detectionInfo.nBin / m_BlockSize);
	VAR_TELLER(pChosenFundemental->detectionInfo.sfProbability);
	VAR_TELLER(pChosenFundemental->detectionInfo.dFrequency);
//	VAR_TELLER(pChosenFundemental->nOccurences);
	fs[m_outputLocalTuning].push_back(f10);

	return fs;

}
