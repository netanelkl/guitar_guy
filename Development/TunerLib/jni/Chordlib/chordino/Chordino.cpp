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

#include "Chordino.h"

#include "chromamethods.h"
#include "viterbi.h"

#include <cstdlib>
#include <fstream>
#include <cmath>
#include <ctime>
#include <algorithm>
#include <boost/tokenizer.hpp>
#include <boost/iostreams/device/file.hpp>
#include <boost/iostreams/stream.hpp>
#include <boost/lexical_cast.hpp>

const bool debug_on = false;
using namespace boost;

static const char* notenames[24] = {
    "A  (bass)","Bb (bass)","B  (bass)","C  (bass)","C# (bass)","D  (bass)","Eb (bass)","E  (bass)","F  (bass)","F# (bass)","G  (bass)","Ab (bass)",
    "A","Bb","B","C","C#","D","Eb","E","F","F#","G","Ab"};

static const char* bassnames[12][12] ={
    {"A","","B","C","C#","D","","E","","F#","G","G#"},
    {"Bb","","C","Db","D","Eb","","F","","G","Ab","A"},
    {"B","","C#","D","D#","E","","F#","","G#","A","A#"},
    {"C","","D","Eb","E","F","","G","","A","Bb","B"},
    {"C#","","D#","E","E#","F#","","G#","","A#","B","B#"},
    {"D","","E","F","F#","G","","A","","B","C","C#"},
    {"Eb","","F","Gb","G","Ab","","Bb","","C","Db","D"},
    {"E","","F#","G","G#","A","","B","","C#","D","D#"},
    {"F","","G","Ab","A","Bb","","C","","D","Eb","E"},
    {"F#","","G#","A","A#","B","","C#","","D#","E","E#"},
    {"G","","A","Bb","B","C","","D","","E","F","F#"},
    {"Ab","","Bb","Cb","C","Db","","Eb","","F","Gb","G"}
};

static const float basswindow[] = {0.001769, 0.015848, 0.043608, 0.084265, 0.136670, 0.199341, 0.270509, 0.348162, 0.430105, 0.514023, 0.597545, 0.678311, 0.754038, 0.822586, 0.882019, 0.930656, 0.967124, 0.990393, 0.999803, 0.995091, 0.976388, 0.944223, 0.899505, 0.843498, 0.777785, 0.704222, 0.624888, 0.542025, 0.457975, 0.375112, 0.295778, 0.222215, 0.156502, 0.100495, 0.055777, 0.023612, 0.004909, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000};
static const float treblewindow[] = {0.000350, 0.003144, 0.008717, 0.017037, 0.028058, 0.041719, 0.057942, 0.076638, 0.097701, 0.121014, 0.146447, 0.173856, 0.203090, 0.233984, 0.266366, 0.300054, 0.334860, 0.370590, 0.407044, 0.444018, 0.481304, 0.518696, 0.555982, 0.592956, 0.629410, 0.665140, 0.699946, 0.733634, 0.766016, 0.796910, 0.826144, 0.853553, 0.878986, 0.902299, 0.923362, 0.942058, 0.958281, 0.971942, 0.982963, 0.991283, 0.996856, 0.999650, 0.999650, 0.996856, 0.991283, 0.982963, 0.971942, 0.958281, 0.942058, 0.923362, 0.902299, 0.878986, 0.853553, 0.826144, 0.796910, 0.766016, 0.733634, 0.699946, 0.665140, 0.629410, 0.592956, 0.555982, 0.518696, 0.481304, 0.444018, 0.407044, 0.370590, 0.334860, 0.300054, 0.266366, 0.233984, 0.203090, 0.173856, 0.146447, 0.121014, 0.097701, 0.076638, 0.057942, 0.041719, 0.028058, 0.017037, 0.008717, 0.003144, 0.000350};


static vector<string> staticChordnames() {
    vector<string> chordnames;
    chordnames.push_back("maj");//=1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,0,0
    chordnames.push_back("maj");// =0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,0,0
    chordnames.push_back("m");//=1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,1,0,0,0,0
    chordnames.push_back("m");//=0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,1,0,0,0,0
    chordnames.push_back("dim7");//=0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,1,0,0,1,0,0,0,1,0
    chordnames.push_back("dim7");//=1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,1,0,0,0,1,0
    chordnames.push_back("6");//=1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1,0,1,0,0
    chordnames.push_back("7");//=1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,1,0
    chordnames.push_back("maj7");//=1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,0,1
    chordnames.push_back("m7");//=1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,1,0,0,1,0
    chordnames.push_back("m6");//=1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,1,0,1,0,0
    chordnames.push_back("");//=0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,0,0
    chordnames.push_back("");//=0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,1,0,0,1,0,0,0,0
    chordnames.push_back("");//=1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,1,0,0,0,0,0
    chordnames.push_back("aug");//=1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0
    chordnames.push_back("");//=0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,0,0
    chordnames.push_back("");//=0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,1,0,0,1,0,0,0,0
    chordnames.push_back("7");//=0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,1,0
    return chordnames;
}

static vector<float> staticChordvalues() {
    vector<float> chordvalues;
    float chordvaluearray[] = {1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,0,0,
    0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,0,0,
    1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,1,0,0,0,0,
    0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,1,0,0,0,0,
    0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,1,0,0,1,0,0,0,1,0,
    1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,1,0,0,0,1,0,
    1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1,0,1,0,0,
    1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,1,0,
    1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,0,1,
    1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,1,0,0,1,0,
    1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,1,0,1,0,0,
    0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,0,0,
    0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,1,0,0,1,0,0,0,0,
    1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,1,0,0,0,0,0,
    1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,
    0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,0,0,
    0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,1,0,0,1,0,0,0,0,
    0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,1,0};
    for (int iChord = 0; iChord < 18; ++iChord) {
        for (int iNote = 0; iNote < 24; iNote++) {
            chordvalues.push_back(chordvaluearray[24*iChord+iNote]);
        }
    }
    return chordvalues;
}


static vector<string> chordDictionary(vector<float> *mchorddict, vector<vector<int> > *m_chordnotes, float boostN) {

    typedef tokenizer<char_separator<char> > Tok;
    char_separator<char> sep(",; ","=");

    string chordDictBase("chord.dict");
    string chordDictFilename;

//    vector<string> ppath = getPluginPath();

    bool hasExternalDictinoary = true;

 /*   for (size_t i = 0; i < ppath.size(); ++i) {
    	chordDictFilename = ppath[i] + "/" + chordDictBase;
    	COUT("Looking for chord.dict in " << chordDictFilename << "..." ;
    	fstream fin;
        fin.open(chordDictFilename.c_str(),ios::in);
        if( fin.is_open() )
        {
            fin.close();
    	    COUT(" success." << endl);
    	    break;
        } else {
            if (i+1 < ppath.size()) COUT(" (not found yet) ..." << endl);
            else {
                LOGE("* WARNING: failed to find chord dictionary, using default chord dictionary." );
                hasExternalDictinoary = false;
            }
        }
    }

    iostreams::stream<iostreams::file_source> chordDictFile(chordDictFilename);
    string line;*/
    // int iElement = 0;
    int nChord = 0;

    vector<string> tempChordNames = staticChordnames();
    vector<float> tempChordDict = staticChordvalues();
    vector<string> loadedChordNames;
    vector<float> loadedChordDict;
   /* if (hasExternalDictinoary && chordDictFile.is_open()) {
        LOGD("-----------------> %d", tempChordNames.size());
        tempChordDict.clear();
        tempChordNames.clear();
        while (std::getline(chordDictFile, line)) { // loop over lines in chord.dict file
            // first, get the chord definition
            string chordType;
            vector<float> tempPCVector;
            // COUT(line << endl);
            if (!line.empty() && line.substr(0,1) != "#") {
                Tok tok(line, sep);
                for(Tok::iterator tok_iter = tok.begin(); tok_iter != tok.end(); ++tok_iter) { // loop over line elements
                    string tempString = *tok_iter;
                    // COUT(tempString << endl);
                    if (tok_iter == tok.begin()) { // either the chord name or a colon
                        if (tempString == "=") {
                            chordType = "";
                        } else {
                            chordType = tempString;
                            tok_iter++;
                        }
                    } else {
                        tempChordDict.push_back(lexical_cast<float>(*tok_iter));
                    }
                }
                tempChordNames.push_back(chordType);
            }
        }
    }*/


    for (int iType = 0; iType < (int)tempChordNames.size(); ++iType) {
        // now make all 12 chords of every type
        for (int iSemitone = 0; iSemitone < 12; iSemitone++) {
            vector<int> tempchordnotes;
            // add bass slash notation
            string slashNotation = "";
            for (int kSemitone = 1; kSemitone < 12; kSemitone++) {
                if (tempChordDict[24*iType+(kSemitone) % 12] > 0.99) {
                    slashNotation = bassnames[iSemitone][kSemitone];
                }
            }
            if (slashNotation=="") tempchordnotes.push_back(eChromaMethodsMIDIBasenote + (iSemitone+12) % 12);
            for (int kSemitone = 0; kSemitone < 12; kSemitone++) { // bass pitch classes
                // COUT(((kSemitone - iSemitone + 12) % 12) << endl);
                float bassValue = 0;
                if (tempChordDict[24*iType+(kSemitone - iSemitone + 12) % 12]==1) {
                    bassValue = 1;
                    tempchordnotes.push_back(eChromaMethodsMIDIBasenote + (kSemitone+12) % 12);
                } else {
                    if (tempChordDict[24*iType+((kSemitone - iSemitone + 12) % 12) + 12] == 1) bassValue = 0.5;
                }
                loadedChordDict.push_back(bassValue);
            }
            for (int kSemitone = 0; kSemitone < 12; kSemitone++) { // chord pitch classes
                loadedChordDict.push_back(tempChordDict[24*iType+((kSemitone - iSemitone + 12) % 12) + 12]);
                if (tempChordDict[24*iType+((kSemitone - iSemitone + 12) % 12) + 12] > 0) tempchordnotes.push_back(eChromaMethodsMIDIBasenote + (kSemitone+12+6) % 12 - 6 + 24);
            }
            ostringstream os;
            if (slashNotation.empty()) {
                os << notenames[12+iSemitone] << tempChordNames[iType];
            } else {
                os << notenames[12+iSemitone] << tempChordNames[iType] << "/" << slashNotation;
            }
            // COUT(os.str() << endl);
            loadedChordNames.push_back(os.str());

            m_chordnotes->push_back(tempchordnotes);
            // for (int iNote = 0; iNote < tempchordnotes.size(); ++iNote) {
            //     COUT(tempchordnotes[iNote] << " ";
            // }
            // COUT(endl);
        }
    }


    // N type
    loadedChordNames.push_back("N");
    for (unsigned kSemitone = 0; kSemitone < 12; kSemitone++) loadedChordDict.push_back(0.5);
    for (unsigned kSemitone = 0; kSemitone < 12; kSemitone++) loadedChordDict.push_back(1.0);
    vector<int> tempchordvector;
    m_chordnotes->push_back(tempchordvector);
    float exponent = 2.0;
    // float m_boostN = 1.1;
    // COUT(" N BOOST : " << boostN << endl << endl);
    for (int iChord = 0; iChord < (int)loadedChordDict.size()/24; iChord++) {
        float sum = 0;
        float stand = 0;
        for (int iST = 0; iST < 24; ++iST) {
            sum += loadedChordDict[24 * iChord + iST];
        }
        for (int iST = 0; iST < 24; ++iST) {
            // loadedChordDict[24 * iChord + iST] -= sum/24;
            stand += pow(abs(loadedChordDict[24 * iChord + iST]),exponent)/24;
        }
        if (iChord < (int)loadedChordDict.size()/24 - 1) {
            stand = powf(stand,1.0f/exponent);
        } else {
            stand = powf(stand,1.0f/exponent) / (1+boostN);
        }
        for (int iST = 0; iST < 24; ++iST) {
            loadedChordDict[24 * iChord + iST] /= stand;
        }

    }


    std::string chordNames;
    nChord = 0;
    for (int i = 0; i < (int)loadedChordNames.size(); i++) {
    	chordNames += loadedChordNames[i] + ",";
        nChord++;
    }


    //chordDictFile.close();
//    LOGD("ChordNames(%d, %s)", nChord, chordNames.c_str());

    // mchorddict = new float[nChord*24];
    for (int i = 0; i < nChord*24; i++) {
        mchorddict->push_back(loadedChordDict[i]);
    }

    // COUT("before leaving" << chordnames[1] << endl);
    return loadedChordNames;
}




void dictionaryMatrix(float* dm, float s_param) {
    int binspersemitone = eChromaMethodsBPS;
    int minoctave = 0; // this must be 0
    int maxoctave = 7; // this must be 7
    // float s_param = 0.7;

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

    float curr_f;
    float floatbin;
    float curr_amp;
    // now for every combination calculate the matrix element
    for (int iOut = 0; iOut < 12 * (maxoctave - minoctave); ++iOut) {
        // COUT(iOut << endl);
        for (int iHarm = 1; iHarm <= 20; ++iHarm) {
            curr_f = 440 * pow(2,(minMIDI-69+iOut)*1.0/12) * iHarm;
            // if (curr_f > cq_f[nNote-1])  break;
            floatbin = ((iOut + 1) * binspersemitone + 1) + binspersemitone * 12 * my_log2(iHarm);
            // COUT(floatbin << endl);
            curr_amp = pow(s_param,float(iHarm-1));
            // COUT("curramp" << curr_amp << endl);
            for (int iNote = 0; iNote < eChromaMethodsNote; ++iNote) {
                if (abs(iNote+1.0-floatbin)<2) {
                    dm[iNote  + eChromaMethodsNote * iOut] += cospuls(iNote+1.0, floatbin, binspersemitone + 0.0) * curr_amp;
                    // dm[iNote + nNote * iOut] += 1 * curr_amp;
                }
            }
        }
    }


}


vector<float> SpecialConvolution(vector<float> convolvee, vector<float> kernel)
{
    float s;
    int m, n;
    int lenConvolvee = convolvee.size();
    int lenKernel = kernel.size();

    vector<float> Z(eChromaMethodsNote,0);

    for (n = lenKernel - 1; n < lenConvolvee; n++) {
    	s=0.0;
    	for (m = 0; m < lenKernel; m++) {
            // COUT("m = " << m << ", n = " << n << ", n-m = " << (n-m) << '\n';
            s += convolvee[n-m] * kernel[m];
            // if (debug_on) COUT("--> s = " << s << '\n';
    	}
        // COUT(n - lenKernel/2 << endl);
        Z[n -lenKernel/2] = s;
    }

    // fill upper and lower pads
    for (n = 0; n < lenKernel/2; n++) Z[n] = Z[lenKernel/2];
    for (n = lenConvolvee; n < lenConvolvee +lenKernel/2; n++) Z[n - lenKernel/2] =
                                                                   Z[lenConvolvee - lenKernel/2 -  1];
    return Z;
}

Chordino::Chordino(float inputSampleRate) :
		NNLSBase(inputSampleRate), m_chorddict(0), m_chordnotes(0), m_chordnames(
				0)
{
	if (debug_on)
		LOGD("--> Chordino" );
	// get the *chord* dictionary from file (if the file exists)

}

Chordino::~Chordino()
{
	if (debug_on)
		LOGD("--> ~Chordino" );
}

string Chordino::getIdentifier() const
{
//	if (debug_on)
//		LOGD("--> getIdentifier" );
	return "chordino";
}

string Chordino::getName() const
{
//	if (debug_on)
//		LOGD("--> getName" );
	return "Chordino";
}

string Chordino::getDescription() const
{
//	if (debug_on)
//		LOGD("--> getDescription" );
	return "Chordino provides a simple chord transcription based on NNLS Chroma (as in the NNLS Chroma plugin). Chord profiles given by the user in the file chord.dict are used to calculate frame-wise chord similarities. Two simple (non-state-of-the-art!) algorithms are available that smooth these to provide a chord transcription: a simple chord change method, and a standard HMM/Viterbi approach.";
}

Chordino::ParameterList Chordino::getParameterDescriptors() const
{
//	if (debug_on)
//		LOGD("--> getParameterDescriptors" );
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

	ParameterDescriptor d4;
	d4.identifier = "useHMM";
	d4.name = "HMM (Viterbi decoding)";
	d4.description =
			"Turns on Viterbi decoding (when off, the simple chord estimator is used).";
	d4.unit = "";
	d4.minValue = 0.0;
	d4.maxValue = 1.0;
	d4.defaultValue = 1.0;
	d4.isQuantized = true;
	d4.quantizeStep = 1.0;
	list.push_back(d4);

	ParameterDescriptor d0;
	d0.identifier = "rollon";
	d0.name = "spectral roll-on";
	d0.description =
			"Consider the cumulative energy spectrum (from low to high frequencies). All bins below the first bin whose cumulative energy exceeds the quantile [spectral roll on] x [total energy] will be set to 0. A value of 0 means that no bins will be changed.";
	d0.unit = "%";
	d0.minValue = 0;
	d0.maxValue = 5;
	d0.defaultValue = 1.0;
	d0.isQuantized = true;
	d0.quantizeStep = 0.5;
	list.push_back(d0);

	ParameterDescriptor d1;
	d1.identifier = "tuningmode";
	d1.name = "tuning mode";
	d1.description =
			"Tuning can be performed locally or on the whole extraction segment. Local tuning is only advisable when the tuning is likely to change over the audio, for example in podcasts, or in a cappella singing.";
	d1.unit = "";
	d1.minValue = 0;
	d1.maxValue = 1;
	d1.defaultValue = 0.0;
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
	d3.description =
			"Determines how individual notes in the note dictionary look: higher values mean more dominant higher harmonics.";
	d3.unit = "";
	d3.minValue = 0.5;
	d3.maxValue = 0.9;
	d3.defaultValue = 0.7;
	d3.isQuantized = false;
	list.push_back(d3);

	ParameterDescriptor boostn;
	boostn.identifier = "boostn";
	boostn.name = "boost N";
	boostn.description = "Boost likelihood of the N (no chord) label.";
	boostn.unit = "";
	boostn.minValue = 0.0;
	boostn.maxValue = 1.0;
	boostn.defaultValue = 0.0;
	boostn.isQuantized = false;
	list.push_back(boostn);

	return list;
}

Chordino::OutputList Chordino::getOutputDescriptors() const
{
	if (debug_on)
		LOGD("--> getOutputDescriptors" );
	OutputList list;

	int index = 0;

	OutputDescriptor d7;
	d7.identifier = "simplechord";
	d7.name = "Chord Estimate";
	d7.description =
			"Estimated chord times and labels. Two simple (non-state-of-the-art!) algorithms are available that smooth these to provide a chord transcription: a simple chord change method, and a standard HMM/Viterbi approach.";
	d7.unit = "";
	d7.hasFixedBinCount = true;
	d7.binCount = 0;
	d7.hasKnownExtents = false;
	d7.isQuantized = false;
	d7.sampleType = OutputDescriptor::VariableSampleRate;
	d7.hasDuration = false;
	d7.sampleRate =
			(m_stepSize == 0) ?
					m_inputSampleRate / 2048 : m_inputSampleRate / m_stepSize;
	list.push_back(d7);
	m_outputChords = index++;

	OutputDescriptor chordnotes;
	chordnotes.identifier = "chordnotes";
	chordnotes.name = "Note Representation of Chord Estimate";
	chordnotes.description =
			"A simple represenation of the estimated chord with bass note (if applicable) and chord notes.";
	chordnotes.unit = "MIDI units";
	chordnotes.hasFixedBinCount = true;
	chordnotes.binCount = 1;
	chordnotes.hasKnownExtents = true;
	chordnotes.minValue = 0;
	chordnotes.maxValue = 127;
	chordnotes.isQuantized = true;
	chordnotes.quantizeStep = 1;
	chordnotes.sampleType = OutputDescriptor::VariableSampleRate;
	chordnotes.hasDuration = true;
	chordnotes.sampleRate =
			(m_stepSize == 0) ?
					m_inputSampleRate / 2048 : m_inputSampleRate / m_stepSize;
	list.push_back(chordnotes);
	m_outputChordnotes = index++;

	OutputDescriptor d8;
	d8.identifier = "harmonicchange";
	d8.name = "Harmonic Change Value";
	d8.description =
			"An indication of the likelihood of harmonic change. Depends on the chord dictionary. Calculation is different depending on whether the Viterbi algorithm is used for chord estimation, or the simple chord estimate.";
	d8.unit = "";
	d8.hasFixedBinCount = true;
	d8.binCount = 1;
	d8.hasKnownExtents = false;
	// d8.minValue = 0.0;
	// d8.maxValue = 0.999;
	d8.isQuantized = false;
	d8.sampleType = OutputDescriptor::FixedSampleRate;
	d8.hasDuration = false;
	// d8.sampleRate = (m_stepSize == 0) ? m_inputSampleRate/2048 : m_inputSampleRate/m_stepSize;
	list.push_back(d8);
	m_outputHarmonicChange = index++;

	return list;
}

bool Chordino::initialise(size_t channels, size_t stepSize, size_t blockSize)
{
	if (debug_on)
	{
		LOGD("--> initialise");
	}


	if (!NNLSBase::initialise(channels, stepSize, blockSize))
	{
		return false;
	}

	for (unsigned i = 0; i < eChromaMethodsNote * 84; ++i)
		m_dict[i] = 0.0;

	dictionaryMatrix(m_dict, m_s);

	m_chordnames = chordDictionary(&m_chorddict, &m_chordnotes, m_boostN);
	return true;
}

void Chordino::reset()
{
	if (debug_on)
		LOGD("--> reset");
	NNLSBase::reset();
}

Chordino::FeatureSet Chordino::process(const float * const *inputBuffers,
		Vamp::RealTime timestamp)
{
//	if (debug_on)
//		LOGD("--> process" );
	NNLSBase::baseProcess(inputBuffers, timestamp);
	calculateTunings();
	return FeatureSet();
}

#define CLOCK_INIT()	clock_t begin, end
#define CLOCK_START()	begin = clock()
#define CLOCK_END()		end = clock(); \
		COUT( "Time elapsed: " << double(diffclock(end,begin)) << " ms"<< endl)

double diffclock(clock_t clock1,clock_t clock2)
{
	double diffticks=clock1-clock2;
	double diffms=(diffticks*1000)/CLOCKS_PER_SEC;
	return diffms;
}

void Chordino::hmmProcessing(const vector<vector<double> >& chordogram,
		const vector<Vamp::RealTime>& timestamps, FeatureSet& fsOut,
		vector<float>& chordchange, vector<Feature>& oldnotes)
{
	int nFrame = m_logSpectrum.size();
	int nChord = m_chordnames.size();
	//		LOGD("[Chordino Plugin] HMM Chord Estimation ... ");
	int oldchord = nChord - 1;
	double selftransprob = 0.99;
	// vector<double> init = vector<double>(nChord,1.0/nChord);
	vector<double> init = vector<double>(nChord, 0);
	init[nChord - 1] = 1;
	double* delta;
	delta = (double*) (malloc(sizeof(double) * nFrame * nChord));
	vector<vector<double> > trans;
	for (int iChord = 0; iChord < nChord; iChord++)
	{
		vector<double> temp = vector<double>(nChord,
				(1 - selftransprob) / (nChord - 1));
		temp[iChord] = selftransprob;
		trans.push_back(temp);
	}
	vector<int> chordpath = ViterbiPath(init, trans, chordogram, delta);
	Feature chord_feature; // chord estimate
	chord_feature.hasTimestamp = true;
	chord_feature.timestamp = timestamps[0];
	chord_feature.label = m_chordnames[chordpath[0]];
	fsOut[m_outputChords].push_back(chord_feature);
	if (chordpath[0] < 216)
	{
		LOGD("ChordLabel(%d,%s)", chordpath[0],
				m_chordnames[chordpath[0]].c_str());
	}
	chordchange[0] = 0;
	for (int iFrame = 1; iFrame < (int) (chordpath.size()); ++iFrame)
	{
		// LOGD(chordpath[iFrame] );
		if (chordpath[iFrame] != oldchord)
		{
			// chord
			Feature chord_feature; // chord estimate
			chord_feature.hasTimestamp = true;
			chord_feature.timestamp = timestamps[iFrame];
			chord_feature.label = m_chordnames[chordpath[iFrame]];
			fsOut[m_outputChords].push_back(chord_feature);
			oldchord = chordpath[iFrame];
			// chord notes
			for (int iNote = 0; iNote < (int) (oldnotes.size()); ++iNote)
			{
				// finish duration of old chord
				oldnotes[iNote].duration = oldnotes[iNote].duration + timestamps[iFrame];
				fsOut[m_outputChordnotes].push_back(oldnotes[iNote]);
			}
			oldnotes.clear();
			for (int iNote = 0;
					iNote < (int) (m_chordnotes[chordpath[iFrame]].size());
					++iNote)
			{
				// prepare notes of current chord
				Feature chordnote_feature;
				chordnote_feature.hasTimestamp = true;
				chordnote_feature.timestamp = timestamps[iFrame];
				chordnote_feature.values.push_back(
						m_chordnotes[chordpath[iFrame]][iNote]);
				chordnote_feature.hasDuration = true;
				chordnote_feature.duration = -timestamps[iFrame]; // this will be corrected at the next chord
				oldnotes.push_back(chordnote_feature);
			}
		}
		/* calculating simple chord change prob */
		for (int iChord = 0; iChord < nChord; iChord++)
		{
			chordchange[iFrame - 1] += delta[(iFrame - 1) * nChord + iChord] * log(
					delta[(iFrame - 1) * nChord + iChord] / delta[iFrame * nChord + iChord]);
		}
	}
}

void Chordino::simpleChordEstimationProcessing(
		vector<vector<int> > scoreChordogram,
		const vector<vector<double> >& chordogram,
		vector<float>& chordchange, FeatureSet& fsOut,
		vector<Vamp::RealTime>& timestamps, vector<Feature>& oldnotes)
{
	int nNumChords = m_chordnames.size();
	/* Simple chord estimation
	 I just take the local chord estimates ("currentChordSalience") and average them over time, then
	 take the maximum. Very simple, don't do this at home...
	 */
	//		LOGD("[Chordino Plugin] Simple Chord Estimation ... ");
	int count = 0;
	int halfwindowlength = m_inputSampleRate / m_stepSize;
	vector<int> chordSequence;
	for (vector<Vamp::RealTime>::iterator it = timestamps.begin();
			it != timestamps.end(); ++it)
	{
		// initialise the score chordogram
		vector<int> temp = vector<int>(nNumChords, 0);
		scoreChordogram.push_back(temp);
	}
	for (vector<Vamp::RealTime>::iterator it = timestamps.begin();
			it != (timestamps.end() - 2 * halfwindowlength - 1); ++it)
	{
		int startIndex = count + 1;
		int endIndex = count + 2 * halfwindowlength;
		float chordThreshold = 2.5 / nNumChords; //*(2*halfwindowlength+1);
		vector<int> chordCandidates;
		for (int iChord = 0; iChord + 1 < nNumChords; iChord++)
		{
			// float currsum = 0;
			// for (int iFrame = startIndex; iFrame < endIndex; ++iFrame) {
			//  currsum += chordogram[iFrame][iChord];
			// }
			//                 if (currsum > chordThreshold) chordCandidates.push_back(iChord);
			for (int iFrame = startIndex; iFrame < endIndex; ++iFrame)
			{
				if (chordogram[iFrame][iChord] > chordThreshold)
				{
					chordCandidates.push_back(iChord);
					break;
				}
			}
		}
		chordCandidates.push_back(nNumChords - 1);
		// LOGD(chordCandidates.size() );
		float maxval = 0; // will be the value of the most salient *chord change* in this frame
		float maxindex = 0; //... and the index thereof
		int bestchordL = nNumChords - 1; // index of the best "left" chord
		int bestchordR = nNumChords - 1; // index of the best "right" chord
		for (int iWF = 1; iWF < 2 * halfwindowlength; ++iWF)
		{
			// now find the max values on both sides of iWF
			// left side:
			float maxL = 0;
			int maxindL = nNumChords - 1;
			for (int kChord = 0; kChord < (int) (chordCandidates.size());
					kChord++)
			{
				int iChord = chordCandidates[kChord];
				float currsum = 0;
				for (int iFrame = 0; iFrame < iWF - 1; ++iFrame)
				{
					currsum += chordogram[count + iFrame][iChord];
				}
				if (iChord == nNumChords - 1)
					currsum *= 0.8;

				if (currsum > maxL)
				{
					maxL = currsum;
					maxindL = iChord;
				}
			}
			// right side:
			float maxR = 0;
			int maxindR = nNumChords - 1;
			for (int kChord = 0; kChord < (int) (chordCandidates.size());
					kChord++)
			{
				int iChord = chordCandidates[kChord];
				float currsum = 0;
				for (int iFrame = iWF - 1; iFrame < 2 * halfwindowlength;
						++iFrame)
				{
					currsum += chordogram[count + iFrame][iChord];
				}
				if (iChord == nNumChords - 1)
					currsum *= 0.8;

				if (currsum > maxR)
				{
					maxR = currsum;
					maxindR = iChord;
				}
			}
			if (maxL + maxR > maxval)
			{
				maxval = maxL + maxR;
				maxindex = iWF;
				bestchordL = maxindL;
				bestchordR = maxindR;
			}
		}
		// LOGD("maxindex: " << maxindex << ", bestchordR is " << bestchordR << ", of frame " << count );
		// add a score to every chord-frame-point that was part of a maximum
		for (int iFrame = 0; iFrame < maxindex - 1; ++iFrame)
		{
			scoreChordogram[iFrame + count][bestchordL]++;
		}
		for (int iFrame = maxindex - 1; iFrame < 2 * halfwindowlength; ++iFrame)
		{
			scoreChordogram[iFrame + count][bestchordR]++;
		}
		if (bestchordL != bestchordR)
		{
			chordchange[maxindex + count] += (halfwindowlength - abs(
					maxindex - halfwindowlength)) * 2.0 / halfwindowlength;
		}
		count++;
	}
	// LOGD("*******  agent finished   *******" );
	count = 0;
	for (vector<Vamp::RealTime>::iterator it = timestamps.begin();
			it != timestamps.end(); ++it)
	{
		float maxval = 0; // will be the value of the most salient chord in this frame
		float maxindex = 0; //... and the index thereof
		for (int iChord = 0; iChord < nNumChords; iChord++)
		{
			if (scoreChordogram[count][iChord] > maxval)
			{
				maxval = scoreChordogram[count][iChord];
				maxindex = iChord;
				// LOGD(iChord );
			}
		}
		chordSequence.push_back(maxindex);
		count++;
	}
	// mode filter on chordSequence
	count = 0;
	string oldChord = "";
	for (vector<Vamp::RealTime>::iterator it = timestamps.begin();
			it != timestamps.end(); ++it)
	{
		Feature chord_feature; // chord estimate
		chord_feature.hasTimestamp = true;
		chord_feature.timestamp = *it;
		// Feature currentChord; // chord estimate
		// currentChord.hasTimestamp = true;
		// currentChord.timestamp = currentChromas.timestamp;
		vector<int> chordCount = vector<int>(nNumChords, 0);
		int maxChordCount = 0;
		int maxChordIndex = nNumChords - 1;
		string maxChord;
		int startIndex = max(count - halfwindowlength / 2, 0);
		int endIndex = min(int(chordogram.size()),
				count + halfwindowlength / 2);
		for (int i = startIndex; i < endIndex; i++)
		{
			chordCount[chordSequence[i]]++;
			if (chordCount[chordSequence[i]] > maxChordCount)
			{
				// LOGD("start index " << startIndex );
				maxChordCount++;
				maxChordIndex = chordSequence[i];
				maxChord = m_chordnames[maxChordIndex];
			}
		}
		// chordSequence[count] = maxChordIndex;
		// LOGD(maxChordIndex );
		// LOGD(chordchange[count] );
		if (oldChord != maxChord)
		{
			oldChord = maxChord;
			chord_feature.label = m_chordnames[maxChordIndex];
			fsOut[m_outputChords].push_back(chord_feature);
			for (int iNote = 0; iNote < (int) (oldnotes.size()); ++iNote)
			{
				// finish duration of old chord
				oldnotes[iNote].duration = oldnotes[iNote].duration + chord_feature.timestamp;
				fsOut[m_outputChordnotes].push_back(oldnotes[iNote]);
			}
			oldnotes.clear();
			for (int iNote = 0;
					iNote < (int) (m_chordnotes[maxChordIndex].size()); ++iNote)
			{
				// prepare notes of current chord
				Feature chordnote_feature;
				chordnote_feature.hasTimestamp = true;
				chordnote_feature.timestamp = chord_feature.timestamp;
				chordnote_feature.values.push_back(
						m_chordnotes[maxChordIndex][iNote]);
				chordnote_feature.hasDuration = true;
				chordnote_feature.duration = -chord_feature.timestamp; // this will be corrected at the next chord
				oldnotes.push_back(chordnote_feature);
			}
		}
		count++;
	}
}

Chordino::FeatureSet Chordino::getRemainingFeatures()
{
	// Kidder says: This code looks so bad, it's unlawful!
	// Refactor when we can.
	CLOCK_INIT();
	CLOCK_START();
	// LOGD(hw[0] << hw[1] );
	if (debug_on)
		LOGD("--> getRemainingFeatures" );

	FeatureSet fsOut;
	if (m_logSpectrum.size() == 0)
		return fsOut;

	//
	/**  Calculate Tuning
	 calculate tuning from (using the angle of the complex number defined by the
	 cumulative mean real and imag values)
	 **/
	float meanTuningImag = 0;
	float meanTuningReal = 0;
	for (int iBPS = 0; iBPS < eChromaMethodsBPS; ++iBPS)
	{
		meanTuningReal += m_meanTunings[iBPS] * cosvalues[iBPS];
		meanTuningImag += m_meanTunings[iBPS] * sinvalues[iBPS];
	}
	float cumulativetuning = 440
			* pow(2, atan2(meanTuningImag, meanTuningReal) / (24 * M_PI));
	float normalisedtuning = atan2(meanTuningImag, meanTuningReal) / (2 * M_PI);
	int intShift = floor(normalisedtuning * 3);
	float floatShift = normalisedtuning * 3 - intShift; // floatShift is a really bad name for this

	/** Tune Log-Frequency Spectrogram
	 calculate a tuned log-frequency spectrogram (currentTunedSpec): use the tuning estimated above (kinda f0) to
	 perform linear interpolation on the existing log-frequency spectrogram (kinda currentLogSpectrum).
	 **/
//	LOGD("[Chordino Plugin] Tuning Log-Frequency Spectrogram ... ");

	float tempValue = 0;
	float dbThreshold = 0; // relative to the background spectrum
	float thresh = pow(10, dbThreshold / 20);
	// LOGD("tune local ? " << m_tuneLocal );
	int count = 0;

	FeatureList tunedSpec;

	vector<Vamp::RealTime> timestamps;

	for (FeatureList::iterator i = m_logSpectrum.begin();
			i != m_logSpectrum.end(); ++i)
	{
		Feature currentLogSpectrum = *i;
		Feature currentTunedSpec; // tuned log-frequency spectrum
		currentTunedSpec.hasTimestamp = true;
		currentTunedSpec.timestamp = currentLogSpectrum.timestamp;
		timestamps.push_back(currentLogSpectrum.timestamp);
		currentTunedSpec.values.push_back(0.0);
		currentTunedSpec.values.push_back(0.0); // set lower edge to zero

		if (m_tuneLocal)
		{
			intShift = floor(m_localTuning[count] * 3);
			floatShift = m_localTuning[count] * 3 - intShift; // floatShift is a really bad name for this
		}

		// LOGD(intShift << " " << floatShift );

		for (int k = 2; k < (int) currentLogSpectrum.values.size() - 3; ++k)
		{ // interpolate all inner bins
			tempValue = currentLogSpectrum.values[k + intShift]
					* (1 - floatShift)
					+ currentLogSpectrum.values[k + intShift + 1] * floatShift;
			currentTunedSpec.values.push_back(tempValue);
		}

		currentTunedSpec.values.push_back(0.0);
		currentTunedSpec.values.push_back(0.0);
		currentTunedSpec.values.push_back(0.0); // upper edge
		vector<float> runningmean = SpecialConvolution(currentTunedSpec.values,
				hw);
		vector<float> runningstd;
		for (int i = 0; i < eChromaMethodsNote; i++)
		{ // first step: squared values into vector (variance)
			runningstd.push_back(
					(currentTunedSpec.values[i] - runningmean[i])
							* (currentTunedSpec.values[i] - runningmean[i]));
		}
		runningstd = SpecialConvolution(runningstd, hw); // second step convolve
		for (int i = 0; i < eChromaMethodsNote; i++)
		{
			runningstd[i] = sqrt(runningstd[i]); // square root to finally have running std
			if (runningstd[i] > 0)
			{
				// currentTunedSpec.values[i] = (currentTunedSpec.values[i] / runningmean[i]) > thresh ?
				// 		                    (currentTunedSpec.values[i] - runningmean[i]) / pow(runningstd[i],m_whitening) : 0;
				currentTunedSpec.values[i] =
						(currentTunedSpec.values[i] - runningmean[i]) > 0 ?
								(currentTunedSpec.values[i] - runningmean[i])
										/ pow(runningstd[i], m_whitening) :
								0;
			}
			if (currentTunedSpec.values[i] < 0)
			{
				LOGD("ERROR: negative value in logfreq spectrum" );
			}
		}
		tunedSpec.push_back(currentTunedSpec);
		count++;
	}
//	LOGD("done." );

	/** Semitone spectrum and chromagrams
	 Semitone-spaced log-frequency spectrum derived from the tuned log-freq spectrum above. the spectrum
	 is inferred using a non-negative least squares algorithm.
	 Three different kinds of chromagram are calculated, "treble", "bass", and "both" (which means
	 bass and treble stacked onto each other).
	 **/
	if (m_useNNLS == 0)
	{
//		LOGD("[Chordino Plugin] Mapping to semitone spectrum and chroma ... ");
	}
	else
	{
//		LOGD("[Chordino Plugin] Performing NNLS and mapping to chroma ... ");
	}

	vector<vector<double> > chordogram;
	vector<vector<int> > scoreChordogram;
	vector<float> chordchange = vector<float>(tunedSpec.size(), 0);
	count = 0;

	FeatureList chromaList;

	for (FeatureList::iterator it = tunedSpec.begin(); it != tunedSpec.end();
			++it)
	{
		Feature currentTunedSpec = *it; // logfreq spectrum
		Feature currentChromas; // treble and bass chromagram

		currentChromas.hasTimestamp = true;
		currentChromas.timestamp = currentTunedSpec.timestamp;

		float b[eChromaMethodsNote];

		bool some_b_greater_zero = false;
		float sumb = 0;
		for (int i = 0; i < eChromaMethodsNote; i++)
		{
			// b[i] = m_dict[(nNote * count + i) % (nNote * 84)];
			b[i] = currentTunedSpec.values[i];
			sumb += b[i];
			if (b[i] > 0)
			{
				some_b_greater_zero = true;
			}
		}

		// here's where the non-negative least squares algorithm calculates the note activation x


		vector<float> chroma = vector<float>(12, 0);
		vector<float> basschroma = vector<float>(12, 0);
		if (some_b_greater_zero)
		{
			if (m_useNNLS == 0)
			{
				float currval;
				int iSemitone = 0;
				for (int iNote = eChromaMethodsBPS / 2 + 2; iNote < eChromaMethodsNote - eChromaMethodsBPS / 2;
						iNote += eChromaMethodsBPS)
				{
					currval = 0;
					for (int iBPS = -eChromaMethodsBPS / 2; iBPS < eChromaMethodsBPS / 2 + 1; ++iBPS)
					{
						currval += b[iNote + iBPS]
								* (1 - abs(iBPS * 1.0 / (eChromaMethodsBPS / 2 + 1)));
					}
					chroma[iSemitone % 12] += currval * treblewindow[iSemitone];
					basschroma[iSemitone % 12] += currval
							* basswindow[iSemitone];
					iSemitone++;
				}

			}
			else
			{
				float x[84 + 1000];
				for (int i = 1; i < 1084; ++i)
					x[i] = 1.0;
				vector<int> signifIndex;
				int index = 0;
				sumb /= 84.0;
				for (int iNote = eChromaMethodsBPS / 2 + 2; iNote < eChromaMethodsNote - eChromaMethodsBPS / 2;
						iNote += eChromaMethodsBPS)
				{
					float currval = 0;
					for (int iBPS = -eChromaMethodsBPS / 2; iBPS < eChromaMethodsBPS / 2 + 1; ++iBPS)
					{
						currval += b[iNote + iBPS];
					}
					if (currval > 0)
						signifIndex.push_back(index);
					index++;
				}
				float rnorm;
				float w[84 + 1000];
				float zz[84 + 1000];
				int indx[84 + 1000];
				int mode;
				int dictsize = eChromaMethodsNote * signifIndex.size();
				// LOGD("dictsize is " << dictsize << "and values size" << f3.values.size());
				float *curr_dict = new float[dictsize];
				for (int iNote = 0; iNote < (int) signifIndex.size(); ++iNote)
				{
					for (int iBin = 0; iBin < eChromaMethodsNote; iBin++)
					{
						curr_dict[iNote * eChromaMethodsNote + iBin] = 1.0
								* m_dict[signifIndex[iNote] * eChromaMethodsNote + iBin];
					}
				}
				nnls(curr_dict, eChromaMethodsNote, eChromaMethodsNote, signifIndex.size(), b, x, &rnorm,
						w, zz, indx, &mode);
				DELETE_ARRAY_POINTER(curr_dict);
				for (int iNote = 0; iNote < (int) signifIndex.size(); ++iNote)
				{
					// LOGD(mode );
					chroma[signifIndex[iNote] % 12] += x[iNote]
							* treblewindow[signifIndex[iNote]];
					basschroma[signifIndex[iNote] % 12] += x[iNote]
							* basswindow[signifIndex[iNote]];
				}
			}
		}

		vector<float> origchroma = chroma;
		chroma.insert(chroma.begin(), basschroma.begin(), basschroma.end()); // just stack the both chromas
		currentChromas.values = chroma;

		if (m_doNormalizeChroma > 0)
		{
			vector<float> chromanorm = vector<float>(3, 0);
			switch (int(m_doNormalizeChroma))
			{
			case 0: // should never end up here
				break;
			case 1:
				chromanorm[0] = *max_element(origchroma.begin(),
						origchroma.end());
				chromanorm[1] = *max_element(basschroma.begin(),
						basschroma.end());
				chromanorm[2] = max(chromanorm[0], chromanorm[1]);
				break;
			case 2:
				for (vector<float>::iterator it = chroma.begin();
						it != chroma.end(); ++it)
				{
					chromanorm[2] += *it;
				}
				break;
			case 3:
				for (vector<float>::iterator it = chroma.begin();
						it != chroma.end(); ++it)
				{
					chromanorm[2] += pow(*it, 2);
				}
				chromanorm[2] = sqrt(chromanorm[2]);
				break;
			}
			if (chromanorm[2] > 0)
			{
				for (int i = 0; i < (int) chroma.size(); i++)
				{
					currentChromas.values[i] /= chromanorm[2];
				}
			}
		}

		chromaList.push_back(currentChromas);

		// local chord estimation
		vector<double> currentChordSalience;
		double tempchordvalue = 0;
		double sumchordvalue = 0;

		for (int iChord = 0; iChord < m_chordnames.size(); iChord++)
		{
			tempchordvalue = 0;
			for (int iBin = 0; iBin < 12; iBin++)
			{
				tempchordvalue += m_chorddict[24 * iChord + iBin]
						* chroma[iBin];
			}
			for (int iBin = 12; iBin < 24; iBin++)
			{
				tempchordvalue += m_chorddict[24 * iChord + iBin]
						* chroma[iBin];
			}
			if (iChord == m_chordnames.size() - 1)
				tempchordvalue *= .7;
			if (tempchordvalue < 0)
				tempchordvalue = 0.0;
			tempchordvalue = pow(1.3, tempchordvalue);
			sumchordvalue += tempchordvalue;
//			LOGD("tempchordvalue(%g)", tempchordvalue);
			currentChordSalience.push_back(tempchordvalue);
		}
		if (sumchordvalue > 0)
		{
			for (int iChord = 0; iChord < m_chordnames.size(); iChord++)
			{
				currentChordSalience[iChord] /= sumchordvalue;
//				LOGD("currentChordSalience(%d,%g)", iChord, currentChordSalience[iChord]);
			}
		}
		else
		{
			currentChordSalience[m_chordnames.size() - 1] = 1.0;
		}

		chordogram.push_back(currentChordSalience);

		count++;
	}
//	LOGD("done." );

	vector<Feature> oldnotes;

	// bool m_useHMM = true; // this will go into the chordino header file.
	if (m_useHMM == 1.0)
	{
//		LOGD("[Chordino Plugin] HMM Chord Estimation ... ");
		hmmProcessing(chordogram, timestamps, fsOut,
				chordchange, oldnotes);
		// LOGD(chordpath[0] );
	}
	else
	{
		/* Simple chord estimation
		 I just take the local chord estimates ("currentChordSalience") and average them over time, then
		 take the maximum. Very simple, don't do this at home...
		 */
//		LOGD("[Chordino Plugin] Simple Chord Estimation ... ");
		simpleChordEstimationProcessing(scoreChordogram,
				chordogram, chordchange, fsOut, timestamps, oldnotes);
	}
	Feature chord_feature; // last chord estimate
	chord_feature.hasTimestamp = true;
	chord_feature.timestamp = timestamps[timestamps.size() - 1];
	chord_feature.label = "N";
	fsOut[m_outputChords].push_back(chord_feature);

	for (int iNote = 0; iNote < (int) oldnotes.size(); ++iNote)
	{ // finish duration of old chord
		oldnotes[iNote].duration = oldnotes[iNote].duration
				+ timestamps[timestamps.size() - 1];
		fsOut[m_outputChordnotes].push_back(oldnotes[iNote]);
	}

//	LOGD("done." );
	int nFrame = m_logSpectrum.size();
	for (int iFrame = 0; iFrame < nFrame; iFrame++)
	{
		Feature chordchange_feature;
		chordchange_feature.hasTimestamp = true;
		chordchange_feature.timestamp = timestamps[iFrame];
		chordchange_feature.values.push_back(chordchange[iFrame]);
		// LOGD(chordchange[iFrame] );
		fsOut[m_outputHarmonicChange].push_back(chordchange_feature);
	}

	// for (int iFrame = 0; iFrame < nFrame; iFrame++) LOGD(fsOut[m_outputHarmonicChange][iFrame].values[0] );

	CLOCK_END();
	return fsOut;
}
