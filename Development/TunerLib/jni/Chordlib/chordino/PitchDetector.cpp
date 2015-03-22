/* -*- c-basic-offset: 4 indent-tabs-mode: nil -*- vi:set ts=8 sts=4 sw=4: */

/*
 Rosegarden
 A MIDI and audio sequencer and musical notation editor.
 Copyright 2000-2009 the Rosegarden development team.

 Other copyrights also apply to some parts of this work.  Please
 see the AUTHORS file and individual file headers for details.

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License as
 published by the Free Software Foundation; either version 2 of the
 License, or (at your option) any later version.  See the file
 COPYING included with this distribution for more information.
 */

#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <fstream>
#include <algorithm>
#include "PitchDetector.h"

#define DEBUG_PT 0

const PitchDetector::Method PitchDetector::PARTIAL = std::string(
		"Partial" "Frequency Component (DSP)");
const PitchDetector::Method PitchDetector::AUTOCORRELATION = std::string(
		"Autocorrelation" "DSP operation");
const PitchDetector::Method PitchDetector::HPS = std::string(
		"Harmonic Product Spectrum" "Pitch determination (DSP)");
const double PitchDetector::NOSIGNAL = -1;
const double PitchDetector::NONE = -2;

const PitchDetector::MethodVector PitchDetector::m_methods;

int factorial(int n)
{
	return (n == 1 || n == 0) ? 1 : factorial(n - 1) * n;
}

PitchDetector::MethodVector::MethodVector()
{
	push_back(AUTOCORRELATION);
	push_back(HPS);
	push_back(PARTIAL);
}

PitchDetector::PitchDetector(int inputFrameSize, int inputStepSize,
		int nSampleRate, int nFftSize)
{
	if (nFftSize < inputFrameSize)
	{
		COUT("Hell noooo!");
		return;
	}
	m_nFftSize = nFftSize;
	m_frameSize = inputFrameSize;
	m_stepSize = inputStepSize;
	m_sampleRate = nSampleRate;
	m_frame = (float *) malloc(sizeof(float) * (m_frameSize + m_stepSize));
	m_ftMagnitude = new double[nFftSize / 2 + 1];
	// allocate fft buffers
	m_in1 = (float *) fftwf_malloc(sizeof(float) * (nFftSize));
	m_in2 = (float *) fftwf_malloc(sizeof(float) * (nFftSize));
	m_ft1 = (fftwf_complex *) fftwf_malloc(sizeof(fftwf_complex) * nFftSize);
	m_ft2 = (fftwf_complex *) fftwf_malloc(sizeof(fftwf_complex) * nFftSize);

	//for cepstrum
//    m_cepstralIn = (float *)fftwf_malloc(sizeof(float) * m_frameSize );
//    m_cepstralOut = (fftwf_complex *)fftwf_malloc(sizeof(fftwf_complex) * m_frameSize );

// create fft plans
	m_p1 = fftwf_plan_dft_r2c_1d(nFftSize, m_in1, m_ft1, FFTW_MEASURE);
	m_p2 = fftwf_plan_dft_r2c_1d(nFftSize, m_in2, m_ft2, FFTW_MEASURE);

	//for autocorrelation
//    m_pc= fftwf_plan_dft_r2c_1d( m_frameSize, m_cepstralIn, m_cepstralOut, FFTW_MEASURE );

//set default method
	m_method = HPS;
}

const std::vector<PitchDetector::Method>* PitchDetector::getMethods()
{
	return &m_methods;
}

void PitchDetector::setMethod(const PitchDetector::Method method)
{
#if DEBUG_PT
	std::cout << "PitchDetector::setMethod " << method << std::endl;
#endif

	MethodVector::const_iterator it;

	// iterator to vector element:
	it = std::find(m_methods.begin(), m_methods.end(), method);

	if (it != m_methods.end())
	{
		m_method = method;
	}
	else
	{
#if DEBUG_PT
		std::cout << "PitchDetector::setMethod Not a method!\n";
#endif
	}
}
template<typename T>
void PitchDetector::logToFile(std::string strFilename,
									int nFileId,
									T* arData,
									int nSize)
{
	std::string szFullpath = "/storage/sdcard0/guitarTeacher/logs/" + strFilename;
	std::ostringstream s;
	s << szFullpath << nFileId;
	szFullpath = s.str();
//	FILE* out = fopen(szFullpath.c_str(), "wb");
//	int ok;
//	if (out != NULL)
//	{
//		ok = fwrite(arData, nSize * sizeof(T), 1, out) == 1;
//		fclose(out);
//	}
	ofstream myfile;
	myfile.open (szFullpath.c_str());
	for(int i = 0; i < nSize; i++)
	{
		myfile << arData[i] << std::endl;
	}
	myfile.close();
}

PitchDetector::DetectionResult PitchDetector::getPitch()
{
//     return 446.0;
	double freq = 0;
//    double f2 = 0;   // not used?

// Fill input buffers with data for two overlapping frames.
	memset(m_in1, 0, m_nFftSize * sizeof(float));
	memset(m_in2, 0, m_nFftSize * sizeof(float));
	for (int c = 0; c < m_frameSize - m_stepSize; c++)
	{
		double window = 0.5 - 0.5 * (cos(2 * M_PI * c / m_nFftSize));
		m_in1[c] = m_frame[c] * window;
		m_in2[c] = m_frame[c + m_stepSize] * window;
	}

	for (int c = m_frameSize - m_stepSize; c < m_nFftSize; c++)
	{
		m_in1[c] = 0;
		m_in2[c] = 0;
	}

	// Perform DFT
	fftwf_execute(m_p1);
	fftwf_execute(m_p2);

	for(int i = 0; i < m_nFftSize / 2 + 1; i++)
	{
		m_ftMagnitude[i] = abs(
				std::complex<float>(m_ft1[i][0], m_ft1[i][1]));
	}

	DetectionResult result;
	if (m_method == AUTOCORRELATION)
		result = autocorrelation();
	else if (m_method == HPS)
	{
		result = hps();
	}

	if(result.sfProbability != 0)
	{
		VAR_TELLER(result.nBin);
		if(result.nBin == 121 || result.nBin == 122)
		{
			static int i = 0;
			i++;
			result.print();
			COUT("Printing file" << i);
			logToFile<float>("m_in1", i, m_in1, m_nFftSize);
			logToFile<double>("m_ftMagnitude", i, m_ftMagnitude, m_nFftSize / 2 + 1);
		}
	}
	return result;
}

float *PitchDetector::getInBuffer()
{
	return m_frame;
}

PitchDetector::~PitchDetector()
{
	fftwf_free(m_in1);
	fftwf_free(m_in2);
	fftwf_free(m_ft1);
	fftwf_free(m_ft2);
	fftwf_free(m_cepstralIn);
	fftwf_free(m_cepstralOut);
	fftwf_destroy_plan(m_p1);
	fftwf_destroy_plan(m_p2);
	fftwf_destroy_plan(m_pc);
	delete m_ftMagnitude;
}

/**
 Calculates autocorrelation from FFT using Wiener-Khinchin Theorem.
 */

PitchDetector::DetectionResult PitchDetector::autocorrelation()
{
	DetectionResult result;
	double value = 0;
	int bin = 0;

	/*
	 Do 2nd FT on abs of original FT. Cepstrum would use Log
	 instead of square
	 */
	for (int c = 0; c < m_frameSize / 2; c++)
	{
		value = abs(std::complex<double>(m_ft1[c][0], m_ft1[c][1]))
				/ m_frameSize; // normalise
		m_cepstralIn[c] = value;
		m_cepstralIn[(m_frameSize - 1) - c] = 0; //value; //fills second half of fft
	}
	fftwf_execute(m_pc);

	// search for peak after first trough
//    double oldValue = 0;   // not used?
	value = 0;
	double max = 0;

	int c = 0;

	double buff[m_frameSize / 2];
	//fill buffer with magnitudes
	for (int i = 0; i < m_frameSize / 2; i++)
	{
		buff[i] = abs(
				std::complex<double>(m_cepstralOut[i][0], m_cepstralOut[i][1]));
	}

	double smoothed[m_frameSize / 2];
	for (int i = 0; i < 10; i++)
		smoothed[i] = 0;
	for (int i = m_frameSize / 2 - 10; i < m_frameSize / 2; i++)
		smoothed[i] = 0;

	for (int i = 10; i < (m_frameSize / 2) - 10; i++)
	{
		smoothed[i] = 0;
		for (int x = -10; x <= 10; x++)
			smoothed[i] += buff[i + x];
		smoothed[i] /= 21;
	}

	// find end of peak in smoothed buffer (c must atart after smoothing)
	// starts at 50
	for (c = 30; c < (m_frameSize / 4) && smoothed[c] >= smoothed[c + 1]; c++)
	{
	}
	if (c >= m_frameSize / 4)
	{
#if DEBUG_PT
		std::cout << "error: no end to first peak\n";
#endif
		return result;
	}

	max = 0;
	//find next peak from bin 30 (1500Hz) to 588 (75Hz)
	for (int i = 0; i < m_frameSize / 2; i++)
	{
		value = smoothed[i];
		if (i > c && i < 588 && value > max)
		{
			max = value;
			bin = i;
		}
	}

	//std::cout << "max " << max << std::endl;

	if (bin == 0)
	{ // avoids occaisional exception when bin==0
#if DEBUG_PT
	std::cout << "bin = 0???\n";
#endif
		return result;
	}

	int FTbin = round((double) m_frameSize / bin);
//    double fpb = (double)m_sampleRate/(double)m_frameSize;  // no used?

	int fBin = 0;
	std::complex<double> cValue = 0;
	double fMag = 0;

	fMag = 0;
	//find localised partial
	for (int c = FTbin - 2; c < FTbin + 2 && c < m_frameSize / 2; c++)
	{
		cValue = std::complex<double>(m_ft1[c][0], m_ft1[c][1]);
		if (fMag < abs(cValue))
		{
			fBin = c;
			fMag = abs(cValue);
		}
	}

#if DEBUG_PT
	std::cout << "ACbin " << bin
	<< "\tFTbin " << FTbin
	<< "\tPeak FTBin " << fBin
	<< std::endl;
#endif

	result = unwrapPhase(FTbin);

	return result;
}

int PitchDetector::getLocalMaxima(int nCentralIndex, int nSearchWindow,
		double array[])
{
	int nChosenSubharmonic = nCentralIndex;
	double dMaxSubharmonic = 0;
	// Retrieve the local-maxima.
	if (nCentralIndex >= nSearchWindow)
	{
		for (int nCurrentIndex = nCentralIndex
				- SUBHARMONIC_LOCAL_MAXIMA_WIDTH;
				nCurrentIndex
						<= nCentralIndex + nSearchWindow;
				nCurrentIndex++)
		{
			if (array[nCurrentIndex] > dMaxSubharmonic)
			{
				dMaxSubharmonic = array[nCurrentIndex];
				nChosenSubharmonic = nCurrentIndex;
			}
		}
		
	}
	
	return nChosenSubharmonic;
}

/**
 Performs Harmonic Product Spectrum frequency estimation
 */
PitchDetector::DetectionResult PitchDetector::hps()
{
	double max = 0;
	int fBin = 0;

	//calculate max HPS - only covering 1/6 of framesize
	//downsampling by factor of 3 * 1/2 of framesize

//	FILE_LOG_CREATOR(harmonics, "HPS_Harmonics", eLogLevelDebug);
	int nMagnitudeSize = m_nFftSize / 2 + 1;
	double arHPS[nMagnitudeSize];
	double dHPS;
	int nMinBin = (MIN_FREQ * 2 * nMagnitudeSize / m_sampleRate);
	int nHarmonicsWithFundenmental = HARMONICS + 1;
	double dAvg = 0;
	for (int i = nMinBin; i < nMagnitudeSize / nHarmonicsWithFundenmental; i++)
	{
		dHPS = 1;
		for (int j = 1; j <= HARMONICS; j++)
		{
			dHPS *= m_ftMagnitude[i * j];
		}
		arHPS[i] = dHPS;
		if (max < dHPS)
		{
			max = dHPS;

			fBin = i;
		}

		// Lets cross our fingers we don't overflow.
		dAvg = dAvg + dHPS;
	}
	dAvg /= ((nMagnitudeSize / nHarmonicsWithFundenmental) - nMinBin);

	// Try to find a subharmonic.
	int nPossibleSubharmonic = round(fBin / 2.0);
	int nChosenSubharmonic = getLocalMaxima(nPossibleSubharmonic, SUBHARMONIC_LOCAL_MAXIMA_WIDTH, arHPS);

	double dRatio = arHPS[nChosenSubharmonic] / max;

	if(dRatio > SUBHARMONIC_THRESHOLD)
	{
//		VAR_TELLER(nChosenSubharmonic);
//		VAR_TELLER(fBin);
		fBin = nChosenSubharmonic;
	}
	else
	{
		VAR_TELLER(dRatio);
	}

	double freq = fBin * m_sampleRate / (nMagnitudeSize * 2.0) ;
	DetectionResult result;
	result.dFrequency = freq;
//	COUT(freq);
//	COUT(max);
	result.sfProbability = max < MIN_HPS_MAGNITUDE ? 0 : (max / (dAvg * 800));
//	COUT(result.sfProbability);
	result.nBin = fBin;
//	unwrapPhase()
	if (result.sfProbability > MIN_PROBABILITY)
	{
		VAR_TELLER(fBin);
		if(fBin == 121 || fBin == 122)
		{
			static int a = 0;
			a++;
	//		VAR_TELLER(a);
			logToFile<double>("arHPS", a, arHPS, nMagnitudeSize / nHarmonicsWithFundenmental);
		}
	}
	else
	{
		result.sfProbability = 0;
	}
	return result;
}

/**
 Calculates exact frequency of component in fBin.
 Assumes FFT has already been applied to both
 */
PitchDetector::DetectionResult PitchDetector::unwrapPhase(int fBin)
{
	DetectionResult result;

	double oldPhase, fPhase;
	std::complex<double> cVal = std::complex<double>(m_ft1[fBin][0],
			m_ft1[fBin][1]);
	double absFirst = abs(cVal);
	if (absFirst < MIN_THRESHOLD)
		return result;

	oldPhase = arg(cVal);

	cVal = std::complex<double>(m_ft2[fBin][0], m_ft2[fBin][1]);
	fPhase = arg(cVal);

	double freqPerBin = (double) m_sampleRate / (double) m_nFftSize;
	double cf = fBin * freqPerBin;
	double phaseChange = fPhase - oldPhase;
	double expected = cf * (double) m_stepSize / (double) m_sampleRate;

	double phaseDiff = phaseChange / (2.0 * M_PI) - expected;
	phaseDiff -= floor(phaseDiff);

	if ((phaseDiff -= floor(phaseDiff)) > 0.5)
		phaseDiff -= 1;

	phaseDiff *= 2 * M_PI;

	double freqDiff = phaseDiff * freqPerBin
			* ((double) m_nFftSize / (double) m_stepSize) / (2 * M_PI);

//	double freq = cf + freqDiff;
	double freq = cf;
#if DEBUG_PT
	std::cout << "Bin=" << fBin
	<< "\tFPB=" << freqPerBin
	<< "\tcf=" << cf
	<< "\tfreq=" << freq
	<< "\toPh=" << oldPhase
	<< "\tnPh=" << fPhase
	<< "\tphdf=" <<phaseChange
	<< "\texpc=" << expected
	<< std::endl;
#endif

	if (freq >= MIN_FREQ)
	{
//		VAR_TELLER(fBin);
//		VAR_TELLER( absFirst);

//		double arMagnitudes[m_frameSize];
//		for (int i = 0; i < m_frameSize; ++i)
//		{
//			arMagnitudes[i] = abs(
//					std::complex<double>(m_ft1[i][0], m_ft1[i][1]));
//		}
		result.sfProbability = (absFirst > 100) ? 1 : (absFirst / 100);
	}
	else
	{
		result.sfProbability = 0;
	}

	result.nBin = fBin;
	result.dFrequency = freq;

	return result;
}

/**
 Searches for highest component in frequency domain.
 */

double PitchDetector::partial()
{
	int oldBin = 0, fBin = 0;
	std::complex<double> value = 0;
	double fMag = 0;
	double fPhase = 0;
	double oldPhase = 0;

	fMag = 0;
	// find maximum input for first fft (in range)
	for (int c = 4; c < 200; c++)
	{
		value = std::complex<double>(m_ft1[c][0], m_ft1[c][1]);
		if (fMag < abs(value))
		{
			oldBin = c;
			fMag = abs(value);
			oldPhase = arg(value);
		}

	}

	fMag = 0;

	for (int c = 4; c < m_frameSize / 2; c++)
	{
		value = std::complex<double>(m_ft2[c][0], m_ft2[c][1]);
		if (fMag < abs(value))
		{
			fBin = c;
			fMag = abs(value);
			fPhase = arg(value);
		}
	}
	// If magnitude below threshold return -1
	if (fMag < MIN_THRESHOLD)
		return NOSIGNAL;

	double freqPerBin = (double) m_sampleRate / (double) m_frameSize;
	double cf = (double) fBin * freqPerBin;
	double phaseChange = fPhase - oldPhase;
	double expected = cf * (double) m_stepSize / (double) m_sampleRate;

	double phaseDiff = phaseChange / (2.0 * M_PI) - expected;
	phaseDiff -= floor(phaseDiff);

	if ((phaseDiff -= floor(phaseDiff)) > 0.5)
		phaseDiff -= 1;

	phaseDiff *= 2 * M_PI;

	double freqDiff = phaseDiff * freqPerBin
			* ((double) m_frameSize / (double) m_stepSize) / (2.0 * M_PI);

	double freq = cf + freqDiff;

	return freq;

}

int PitchDetector::getFrameSize() const
{
	return m_frameSize;
}
void PitchDetector::setFrameSize(int nextFrameSize)
{
	m_frameSize = nextFrameSize;
}

int PitchDetector::getStepSize() const
{
	return m_stepSize;
}
void PitchDetector::setStepSize(int nextStepSize)
{
	m_stepSize = nextStepSize;
}

int PitchDetector::getBufferSize() const
{
	return m_frameSize + m_stepSize;
}

