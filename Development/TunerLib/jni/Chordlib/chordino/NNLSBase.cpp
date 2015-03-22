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

#include "NNLSBase.h"

#include "chromamethods.h"

#include <cstdlib>
#include <fstream>
#include <cmath>

#include <algorithm>

const bool debug_on = false;

float cospuls(float x, float centre, float width)
{
    float recipwidth = 1.0/width;
    if (abs(x - centre) <= 0.5 * width) {
        return cos((x-centre)*2*M_PI*recipwidth)*.5+.5;
    }
    return 0.0;
}

float pitchCospuls(float x, float centre, int binsperoctave)
{
    float warpedf = -binsperoctave * (my_log2(centre) - my_log2(x));
    float out = cospuls(warpedf, 0.0, 2.0);
    // now scale to correct for note density
    float c = log(2.0)/binsperoctave;
    if (x > 0) {
        out = out / (c * x);
    } else {
        out = 0;
    }
    return out;
}
float my_log2( float num ) { return log((float)num) / 0.30102999566398119521373889472449; }

bool logFreqMatrix(int fs, int blocksize, float *outmatrix) {

    int binspersemitone = eChromaMethodsBPS;
    int minoctave = 0; // this must be 0
    int maxoctave = 7; // this must be 7
    int oversampling = 80;

    // linear frequency vector
    vector<float> fft_f;
    for (int i = 0; i < blocksize/2; ++i) {
        fft_f.push_back(i * (fs * 1.0 / blocksize));
    }
    float fft_width = fs * 2.0 / blocksize;

    // linear oversampled frequency vector
    vector<float> oversampled_f;
    for (int i = 0; i < oversampling * blocksize/2; ++i) {
        oversampled_f.push_back(i * ((fs * 1.0 / blocksize) / oversampling));
    }

    // pitch-spaced frequency vector
    int minMIDI = 21 + minoctave * 12 - 1; // this includes one additional semitone!
    int maxMIDI = 21 + maxoctave * 12; // this includes one additional semitone!
    vector<float> cq_f;
    float oob = 1.0/binspersemitone; // one over binspersemitone
    // cq_f.push_back(440 * pow(2.0,0.083333 * (minMIDI-69))); // 0.083333 is approx 1/12
    // cq_f.push_back(440 * pow(2.0,0.083333 * (minMIDI+oob-69)));
    for (int i = minMIDI; i < maxMIDI; ++i) {
        for (int k = 0; k < binspersemitone; ++k)	 {
            cq_f.push_back(440 * pow(2.0,0.083333333333 * (i+oob*k-69)));
        }
    }
    // cq_f.push_back(440 * pow(2.0,0.083333 * (minMIDI-oob-69)));
    cq_f.push_back(440 * pow(2.0,0.083333 * (maxMIDI-69)));

    int nFFT = fft_f.size();

    vector<float> fft_activation;
    for (int iOS = 0; iOS < 2 * oversampling; ++iOS) {
        float cosp = cospuls(oversampled_f[iOS],fft_f[1],fft_width);
        fft_activation.push_back(cosp);
        // COUT(cosp << endl);
    }

    float cq_activation;
    for (int iFFT = 1; iFFT < nFFT; ++iFFT) {
        // find frequency stretch where the oversampled vector can be non-zero (i.e. in a window of width fft_width around the current frequency)
        int curr_start = oversampling * iFFT - oversampling;
        int curr_end = oversampling * iFFT + oversampling; // don't know if I should add "+1" here
        // COUT(oversampled_f[curr_start] << " " << fft_f[iFFT] << " " << oversampled_f[curr_end] << endl);
        for (unsigned iCQ = 0; iCQ < cq_f.size(); ++iCQ) {
            outmatrix[iFFT + nFFT * iCQ] = 0;
            if (cq_f[iCQ] * pow(2.0, 0.084) + fft_width > fft_f[iFFT] && cq_f[iCQ] * pow(2.0, -0.084 * 2) - fft_width < fft_f[iFFT]) { // within a generous neighbourhood
                for (int iOS = curr_start; iOS < curr_end; ++iOS) {
                    cq_activation = pitchCospuls(oversampled_f[iOS],cq_f[iCQ],binspersemitone*12);
                    // COUT(oversampled_f[iOS] << " " << cq_f[iCQ] << " " << cq_activation << endl);
                    outmatrix[iFFT + nFFT * iCQ] += cq_activation * fft_activation[iOS-curr_start];
                }
                // if (iCQ == 1 || iCQ == 2) {
                // 	COUT(" " << outmatrix[iFFT + nFFT * iCQ] << endl);
                // }
            }
        }
    }
    return true;
}

NNLSBase::NNLSBase(float inputSampleRate) :
		Plugin(inputSampleRate),
				m_frameCount(0),
				m_logSpectrum(0),
				m_blockSize(0),
				m_stepSize(0),
				m_lengthOfNoteIndex(0),
				m_meanTunings(0),
				m_localTunings(0),
				m_whitening(1.0),
				m_preset(0.0),
				m_useNNLS(1.0),
				m_useHMM(1.0),
				m_localTuning(0.0),
				m_kernelValue(0),
				m_kernelFftIndex(0),
				m_kernelNoteIndex(0),
				m_tuneLocal(0.0),
				m_doNormalizeChroma(0),
				m_rollon(0.0),
				m_boostN(0.1),
				m_s(0.7),
				sinvalues(0),
				m_sfMagnitude(0),
				cosvalues(0)
{
	if (debug_on)
		COUT("--> NNLSBase" << endl);
	// make the *note* dictionary matrix

}

NNLSBase::~NNLSBase()
{
	if (debug_on)
		COUT("--> ~NNLSBase" << endl);

	// deletes
	DELETE_ARRAY_POINTER(m_sfMagnitude);
}

string
NNLSBase::getMaker() const
{
	if (debug_on)
		COUT("--> getMaker" << endl);
	// Your name here
	return "Matthias Mauch";
}

int
NNLSBase::getPluginVersion() const
{
	if (debug_on)
		COUT("--> getPluginVersion" << endl);
	// Increment this each time you release a version that behaves
	// differently from the previous one
	return 3;
}

string
NNLSBase::getCopyright() const
{
	if (debug_on)
		COUT("--> getCopyright" << endl);
	// This function is not ideally named.  It does not necessarily
	// need to say who made the plugin -- getMaker does that -- but it
	// should indicate the terms under which it is distributed.  For
	// example, "Copyright (year). All Rights Reserved", or "GPL"
	return "GPL";
}

NNLSBase::InputDomain
NNLSBase::getInputDomain() const
{
	if (debug_on)
		COUT("--> getInputDomain" << endl);
	return FrequencyDomain;
}

size_t
NNLSBase::getPreferredBlockSize() const
{
	if (debug_on)
		COUT("--> getPreferredBlockSize" << endl);
	return BlockSize; // 0 means "I can handle any block size"
}

size_t
NNLSBase::getPreferredStepSize() const
{
	if (debug_on)
		COUT("--> getPreferredStepSize" << endl);
	return StepSize; // 0 means "anything sensible"; in practice this
	// means the same as the block size for TimeDomain
	// plugins, or half of it for FrequencyDomain plugins
}

size_t
NNLSBase::getMinChannelCount() const
{
	if (debug_on)
		COUT("--> getMinChannelCount" << endl);
	return 1;
}

size_t
NNLSBase::getMaxChannelCount() const
{
	if (debug_on)
		COUT("--> getMaxChannelCount" << endl);
	return 1;
}

NNLSBase::ParameterList
NNLSBase::getParameterDescriptors() const
{
	if (debug_on)
		COUT("--> getParameterDescriptors" << endl);
	ParameterList list;

	ParameterDescriptor d;
	d.identifier = "useNNLS";
	d.name = "use approximate transcription (NNLS)";
	d.description = "Toggles approximate transcription (NNLS).";
	d.unit = "";
	d.minValue = 0.0;
	d.maxValue = 1.0;
	d.defaultValue = 1.0;
	d.isQuantized = true;
	d.quantizeStep = 1.0;
	list.push_back(d);

	ParameterDescriptor d0;
	d0.identifier = "rollon";
	d0.name = "spectral roll-on";
	d0.description = "Consider the cumulative energy spectrum (from low to high frequencies). All bins below the first bin whose cumulative energy exceeds the quantile [spectral roll on] x [total energy] will be set to 0. A value of 0 means that no bins will be changed.";
	d0.unit = "%";
	d0.minValue = 0;
	d0.maxValue = 5;
	d0.defaultValue = 0;
	d0.isQuantized = true;
	d0.quantizeStep = 0.5;
	list.push_back(d0);

	ParameterDescriptor d1;
	d1.identifier = "tuningmode";
	d1.name = "tuning mode";
	d1.description = "Tuning can be performed locally or on the whole extraction segment. Local tuning is only advisable when the tuning is likely to change over the audio, for example in podcasts, or in a cappella singing.";
	d1.unit = "";
	d1.minValue = 0;
	d1.maxValue = 1;
	d1.defaultValue = 0;
	d1.isQuantized = true;
	d1.valueNames.push_back("global tuning");
	d1.valueNames.push_back("local tuning");
	d1.quantizeStep = 1.0;
	list.push_back(d1);

	ParameterDescriptor d2;
	d2.identifier = "whitening";
	d2.name = "spectral whitening";
	d2.description = "Spectral whitening: no whitening - 0; whitening - 1.";
	d2.unit = "";
	d2.isQuantized = true;
	d2.minValue = 0.0;
	d2.maxValue = 1.0;
	d2.defaultValue = 1.0;
	d2.isQuantized = false;
	list.push_back(d2);

	ParameterDescriptor d3;
	d3.identifier = "s";
	d3.name = "spectral shape";
	d3.description = "Determines how individual notes in the note dictionary look: higher values mean more dominant higher harmonics.";
	d3.unit = "";
	d3.minValue = 0.5;
	d3.maxValue = 0.9;
	d3.defaultValue = 0.7;
	d3.isQuantized = false;
	list.push_back(d3);

	ParameterDescriptor d4;
	d4.identifier = "chromanormalize";
	d4.name = "chroma normalization";
	d4.description = "How shall the chroma vector be normalized?";
	d4.unit = "";
	d4.minValue = 0;
	d4.maxValue = 3;
	d4.defaultValue = 0;
	d4.isQuantized = true;
	d4.valueNames.push_back("none");
	d4.valueNames.push_back("maximum norm");
	d4.valueNames.push_back("L1 norm");
	d4.valueNames.push_back("L2 norm");
	d4.quantizeStep = 1.0;
	list.push_back(d4);

	return list;
}

float
NNLSBase::getParameter(string identifier) const
		{
	if (debug_on)
		COUT("--> getParameter" << endl);
	if (identifier == "useNNLS")
	{
		return m_useNNLS;
	}

	if (identifier == "whitening")
	{
		return m_whitening;
	}

	if (identifier == "s")
	{
		return m_s;
	}

	if (identifier == "rollon")
	{
		return m_rollon;
	}

	if (identifier == "boostn")
	{
		return m_boostN;
	}

	if (identifier == "tuningmode")
	{
		if (m_tuneLocal)
		{
			return 1.0;
		}
		else
		{
			return 0.0;
		}
	}
	if (identifier == "preset")
	{
		return m_preset;
	}
	if (identifier == "chromanormalize")
	{
		return m_doNormalizeChroma;
	}

	if (identifier == "useHMM")
	{
		return m_useHMM;
	}

	return 0;

}

void
NNLSBase::setParameter(string identifier, float value)
{
	if (debug_on)
		COUT("--> setParameter" << endl);
	if (identifier == "useNNLS")
	{
		m_useNNLS = (int) value;
	}

	if (identifier == "whitening")
	{
		m_whitening = value;
	}

	if (identifier == "s")
	{
		m_s = value;
	}

	if (identifier == "useHMM")
	{
		m_useHMM = value;
	}

	if (identifier == "boostn")
	{
		m_boostN = value;
	}

	if (identifier == "tuningmode")
	{
		// m_tuneLocal = (value > 0) ? true : false;
		m_tuneLocal = value;
		// COUT("m_tuneLocal :" << m_tuneLocal << endl);
	}
	// if (identifier == "preset") {
	//     m_preset = value;
	//     if (m_preset == 0.0) {
	//         m_tuneLocal = false;
	//         m_whitening = 1.0;
	//         m_dictID = 0.0;
	//     }
	//     if (m_preset == 1.0) {
	//         m_tuneLocal = false;
	//         m_whitening = 1.0;
	//         m_dictID = 1.0;
	//     }
	//     if (m_preset == 2.0) {
	//         m_tuneLocal = false;
	//         m_whitening = 0.7;
	//         m_dictID = 0.0;
	//     }
	// }
	if (identifier == "chromanormalize")
	{
		m_doNormalizeChroma = value;
	}

	if (identifier == "rollon")
	{
		m_rollon = value;
	}
}

NNLSBase::ProgramList
NNLSBase::getPrograms() const
{
	if (debug_on)
		COUT("--> getPrograms" << endl);
	ProgramList list;

	// If you have no programs, return an empty list (or simply don't
	// implement this function or getCurrentProgram/selectProgram)

	return list;
}

string
NNLSBase::getCurrentProgram() const
{
	if (debug_on)
		COUT("--> getCurrentProgram" << endl);
	return ""; // no programs
}

void
NNLSBase::selectProgram(string name)
{
	if (debug_on)
		COUT("--> selectProgram" << endl);
}

bool
NNLSBase::initialise(size_t channels, size_t stepSize, size_t blockSize)
{
	if (debug_on)
	{
		COUT("--> initialise");
	}

	// make things for tuning estimation
	for (int iBPS = 0; iBPS < eChromaMethodsBPS; ++iBPS)
	{
		double sina = sin(2 * M_PI * (iBPS * 1.0 / eChromaMethodsBPS));
		double cosa = cos(2 * M_PI * (iBPS * 1.0 / eChromaMethodsBPS));
		sinvalues.push_back(sina);
		cosvalues.push_back(cosa);
	}

	// make hamming window of length 1/2 octave
	int hamwinlength = eChromaMethodsBPS * 6 + 1;
	float hamwinsum = 0;
	for (int i = 0; i < hamwinlength; ++i)
	{
		hw.push_back(0.54 - 0.46 * cos((2 * M_PI * i) / (hamwinlength - 1)));
		hamwinsum += 0.54 - 0.46 * cos((2 * M_PI * i) / (hamwinlength - 1));
	}
	for (int i = 0; i < hamwinlength; ++i)
		hw[i] = hw[i] / hamwinsum;

	// initialise the tuning
	for (int iBPS = 0; iBPS < eChromaMethodsBPS; ++iBPS)
	{
		m_meanTunings.push_back(0);
		m_localTunings.push_back(0);
	}

	if (channels < getMinChannelCount() ||
			channels > getMaxChannelCount())
		return false;
	m_blockSize = blockSize;
	m_sfMagnitude = new float[m_blockSize / 2];
	m_stepSize = stepSize;
	m_frameCount = 0;
	int tempn = eChromaMethodsNote * m_blockSize / 2;
	// COUT("length of tempkernel : " <<  tempn << endl);
	float *tempkernel;

	tempkernel = new float[tempn];

	logFreqMatrix(m_inputSampleRate, m_blockSize, tempkernel);
	m_kernelValue.clear();
	m_kernelFftIndex.clear();
	m_kernelNoteIndex.clear();
	int countNonzero = 0;
	for (int iNote = 0; iNote < eChromaMethodsNote; ++iNote)
	{ // I don't know if this is wise: manually making a sparse matrix
		for (int iFFT = 0; iFFT < blockSize / 2; ++iFFT)
		{
			if (tempkernel[iFFT + blockSize / 2 * iNote] > 0)
			{
				m_kernelValue.push_back(
						tempkernel[iFFT + blockSize / 2 * iNote]);
				if (tempkernel[iFFT + blockSize / 2 * iNote] > 0)
				{
					countNonzero++;
				}
				m_kernelFftIndex.push_back(iFFT);
				m_kernelNoteIndex.push_back(iNote);
			}
		}
	}
	// COUT("nonzero count : " << countNonzero << endl);
	DELETE_ARRAY_POINTER(tempkernel);
	/*
	 ofstream myfile;
	 myfile.open ("matrix.txt");
	 // myfile << "Writing this to a file.\n";
	 for (int i = 0; i < nNote * 84; ++i) {
	 myfile << m_dict[i] << endl);
	 }
	 myfile.close();
	 */
	return true;
}

void
NNLSBase::reset()
{
	if (debug_on)
		COUT("--> reset");

	// Clear buffers, reset stored values, etc
	m_frameCount = 0;
	// m_dictID = 0;
	m_logSpectrum.clear();
	for (int iBPS = 0; iBPS < eChromaMethodsBPS; ++iBPS)
	{
		m_meanTunings[iBPS] = 0;
		m_localTunings[iBPS] = 0;
	}
	m_localTuning.clear();
}

void NNLSBase::calculateTunings() {
	// COUT(nm[20];
	// COUT(endl);
	float one_over_N = 1.0 / m_frameCount;
	// update means of complex tuning variables
	for (int iBPS = 0; iBPS < eChromaMethodsBPS; ++iBPS) {
		m_meanTunings[iBPS] *= float(m_frameCount - 1) * one_over_N;
	}
	for (int iTone = 0;
			iTone
					< round(eChromaMethodsNote * 0.62 / eChromaMethodsBPS)
							* eChromaMethodsBPS + 1;
			iTone = iTone + eChromaMethodsBPS) {
		for (int iBPS = 0; iBPS < eChromaMethodsBPS; ++iBPS)
			m_meanTunings[iBPS] += m_arNormaledMagnitude[iTone + iBPS] * one_over_N;
		float ratioOld = 0.997;
		for (int iBPS = 0; iBPS < eChromaMethodsBPS; ++iBPS) {
			m_localTunings[iBPS] *= ratioOld;
			m_localTunings[iBPS] += m_arNormaledMagnitude[iTone + iBPS] * (1 - ratioOld);
		}
	}

	// if (m_tuneLocal) {
	// local tuning
	// float localTuningImag = sinvalue * m_localTunings[1] - sinvalue * m_localTunings[2];
	// float localTuningReal = m_localTunings[0] + cosvalue * m_localTunings[1] + cosvalue * m_localTunings[2];
	float localTuningImag = 0;
	float localTuningReal = 0;
	for (int iBPS = 0; iBPS < eChromaMethodsBPS; ++iBPS) {
		localTuningReal += m_localTunings[iBPS] * cosvalues[iBPS];
		localTuningImag += m_localTunings[iBPS] * sinvalues[iBPS];
	}
	float normalisedtuning = atan2(localTuningImag, localTuningReal)
			/ (2 * M_PI);
	m_localTuning.push_back(normalisedtuning);
}

void NNLSBase::calculateMagnitude(const float* fbuf) {
	float energysum = 0;
	// make magnitude
	float maxmag = -10000;
	int maxBin = m_blockSize;
	for (size_t iBin = 0; iBin < m_blockSize / 2; iBin++) {
		m_sfMagnitude[iBin] = sqrt(
				fbuf[2 * iBin] * fbuf[2 * iBin]
						+ fbuf[2 * iBin + 1] * fbuf[2 * iBin + 1]);
		if (m_sfMagnitude[iBin] > m_blockSize * 1.0) {
			m_sfMagnitude[iBin] = m_blockSize; // a valid audio signal (between -1 and 1) should not be limited here.
		}
		if (maxmag < m_sfMagnitude[iBin]) {
			maxmag = m_sfMagnitude[iBin];
			maxBin = iBin;
		}
		if (m_rollon > 0) {
			energysum += pow(m_sfMagnitude[iBin], 2);
		}
	}

	float cumenergy = 0;
	if (m_rollon > 0) {
		for (size_t iBin = 2; iBin < m_blockSize / 2; iBin++) {
			cumenergy += pow(m_sfMagnitude[iBin], 2);
			if (cumenergy < energysum * m_rollon / 100)
				m_sfMagnitude[iBin - 2] = 0;
			else
				break;
		}
	}

	if (maxmag < 2) {
		// COUT("timestamp " << timestamp << ": very low magnitude, setting magnitude to all zeros" << endl);
		for (size_t iBin = 0; iBin < m_blockSize / 2; iBin++) {
			m_sfMagnitude[iBin] = 0;
		}
	}

}

void
NNLSBase::baseProcess(const float * const *inputBuffers,
		Vamp::RealTime timestamp)
{
	m_frameCount++;
	memset(m_sfMagnitude, 0, sizeof(float) * (m_blockSize / 2));

	const float *fbuf = inputBuffers[0];
	calculateMagnitude(fbuf);

	// note magnitude mapping using pre-calculated matrix
	memset(m_arNormaledMagnitude, 0, sizeof(float) * eChromaMethodsNote);

	int binCount = 0;
	for (vector<float>::iterator it = m_kernelValue.begin();
			it != m_kernelValue.end(); ++it)
	{
		// COUT(".";
		m_arNormaledMagnitude[m_kernelNoteIndex[binCount]] += m_sfMagnitude[m_kernelFftIndex[binCount]] * m_kernelValue[binCount];
		// COUT(m_kernelFftIndex[binCount] << " -- " << magnitude[m_kernelFftIndex[binCount]] << " -- "<< m_kernelValue[binCount] << endl);
		binCount++;
	}
	Feature f1; // logfreqspec
	f1.hasTimestamp = true;
	f1.timestamp = timestamp;
	for (int iNote = 0; iNote < eChromaMethodsNote; iNote++)
	{
		f1.values.push_back(m_arNormaledMagnitude[iNote]);
	}

	m_logSpectrum.push_back(f1); // remember note magnitude
}

