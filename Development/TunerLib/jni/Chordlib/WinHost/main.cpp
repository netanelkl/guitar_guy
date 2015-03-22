#ifndef ANDROID_OS

#define WINSTREAMHOSTWRAPPER


#ifdef WINSTREAMHOSTWRAPPER
	#include "WinStreamHostWrapper.h"
#else
	#include "WinHostWrapper.h"
#endif

#define NUM_CHANNELS 			1
#define SAMPLE_RATE				44100
#define SAMPLE_RATE_CONDENSED	441
#define STEP_SIZE				2048
#define BLOCK_SIZE				16384
#define MICROSECS_IN_SEC 		1000000
#define MICROSECS_IN_SEC_CONDENSED 	10000

#include <boost/lexical_cast.hpp>

//bool loadAudioFile()
//{
//	return m_TestWavFile.Load((char*)"Breathing.wav");
//}

//struct SInBuffer
//{
//	float arSamples[BLOCK_SIZE];
//};
//
//Vamp::PluginAdapter<Chordino> adapter;
//void analyzeFeatureSet(Chordino::FeatureSet& featureSet)
//{
//	//		LOGD("%d",pChordino->getOutputChordsKey());
//	Chordino::FeatureSet::const_iterator itrFeatureSet = featureSet.begin();
//	if (itrFeatureSet != featureSet.end())
//	{
//		Chordino::FeatureList featureList = (*itrFeatureSet).second;
////			LOGD("FEATURESET(%d) has (%d) elements:", (*itrFeatureSet).first, featureList.size());
//
//		int nFeatureListIndex = 0;
//		for (Chordino::FeatureList::iterator itrFeatureList =
//				featureList.begin(); itrFeatureList != featureList.end();
//				++itrFeatureList, ++nFeatureListIndex)
//		{
//			std::vector<float> vecValues = (*itrFeatureList).values;
////				LOGD("FEATURE_LIST(%d) has (%d) elements:", nFeatureListIndex, vecValues.size());
//			for (std::vector<float>::iterator itrValues = vecValues.begin();
//					itrValues != vecValues.end();
//					++itrValues, ++nFeatureListIndex)
//			{
//				LOGD("ELEMENT(%d)", *itrValues);
//			}
//		}
//	}
//}
//
//int main()
//{
//	printf("WinJNI\n");

//	Vamp::Plugin* pChordino; //= adapter.createPlugin(SAMPLE_RATE);
//
//	if(pChordino != NULL)
//	{
//		pChordino->setParameter("useHMM",0);
//		pChordino->initialise(NUM_CHANNELS, STEP_SIZE, BLOCK_SIZE);
//		pChordino->getOutputDescriptors();
//	}

// Load the test audio file.
//	if(loadAudioFile() == false)
//	{
//		LOGE("Failed loading test file");
//		return false;
//	}

// Start streaming the file into the processor.

//	float* pFloatArray = new float[BLOCK_SIZE];
//	int nSec = 0,nMicrosec = 0;
//
//	for(volatile int wBlockNum = 0; wBlockNum < m_TestWavFile.GetSize() / (BLOCK_SIZE * sizeof(word)); wBlockNum++)
//	{
//		word* pCurrentSample = ((word*)m_TestWavFile.GetData()) + (wBlockNum * BLOCK_SIZE);
//
//		// Convert the shorts to floats.
//		string sInputs;
//		double dMaxShort = 0x7fff;
//		SInBuffer* pAsStruct = (SInBuffer*)pFloatArray;
//		for(int i = 0; i < BLOCK_SIZE; i++)
//		{
//			pFloatArray[i] = (float)(((double)pCurrentSample[i] - dMaxShort) / dMaxShort);
//			//
////			sInputs += boost::lexical_cast<string>(pFloatArray[i]);
////			sInputs += ',';
//		}
//
//		//LOGD("Inputs(%d,%s)",length, sInputs.c_str());
//
//		// Process the data.
////		LOGD("wBlockNum(%u)",wBlockNum);
//		pChordino->process(&pFloatArray, Vamp::RealTime(nSec,nMicrosec * 1000));
//
//		Chordino::FeatureSet featureSet = pChordino->getRemainingFeatures();
//		analyzeFeatureSet(featureSet);
//
//		nMicrosec = nMicrosec + ((MICROSECS_IN_SEC_CONDENSED * BLOCK_SIZE) / SAMPLE_RATE_CONDENSED);
//		nSec += nMicrosec / (MICROSECS_IN_SEC);
//		nMicrosec = nMicrosec %  (MICROSECS_IN_SEC);
//
//	}
//
//
//
//	Chordino::FeatureSet featureSet = pChordino->getRemainingFeatures();
////		LOGD("%d",pChordino->getOutputChordsKey());
//	analyzeFeatureSet(featureSet);
//
//	featureSet.begin();
//	delete pFloatArray;
//	return 0;
//}

extern "C"
{

#ifdef WINSTREAMHOSTWRAPPER
static WinStreamHostWrapper g_HostWrapper;
#else
static WinHostWrapper g_HostWrapper;
#endif

int main(int argc, char **argv)
{
	g_HostWrapper.shellMain(argc, argv);
}

}
#endif
