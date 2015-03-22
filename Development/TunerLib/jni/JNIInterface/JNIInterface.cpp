#include "JNIInterface.h"
#include <boost/lexical_cast.hpp>
#include <AndroidHost/AndroidHostWrapper.h>
#include <General/FXGGeneral.h>
#ifdef PROFILER_ENABLED
#include "ndk-modules/android-ndk-profiler/prof.h"
#endif

extern "C"
{
	#include "Chordlib/chordino/Yin.h"
}
#define NUM_CHANNELS 	1
#define STEP_SIZE		2048
#define BLOCK_SIZE		16384

extern "C"
{
	Yin yinObject;
	enum EPlugin
	{
		ePlugin_Chordino = 0,
		ePlugin_Tuning,
		ePlugin_NumPlugins
	};

	static AndroidHostWrapper g_arWrappers[ePlugin_NumPlugins];

	bool baseProcessBuffer(HostWrapper& rPlugin, jshortArray pSamples,
			jboolean fIsCopy, JNIEnv* pEnv);

	jstring nativeStringToJstring(JNIEnv * env, const char* szSource);

	inline bool createWrapper(EPlugin ePlugin, const char* szPluginName);

	JNIEXPORT jboolean JNICALL Java_com_mad_tunerlib_recognition_NativeInterface_Initialize(
			JNIEnv * env, jclass obj)
	{
		LOGD("Called Initialize");
		jboolean result = false;
		createWrapper(ePlugin_Chordino, "chordino");
		createWrapper(ePlugin_Tuning, "Tuning");
#ifdef PROFILER_ENABLED
		monstartup("libChordlib.so");
#endif
		memset(&yinObject, 0, sizeof(yinObject));
		return result;
	}

	inline bool createWrapper(EPlugin ePlugin, const char* szPluginName)
	{
		if (g_arWrappers[ePlugin].loadPlugin(szPluginName, "", "", "", 0, false,
				44100))
		{
			return true;
		}
		return false;
	}

	JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM * env, void* res)
	{
		LOGD("JNI loaded.");

		return JNI_VERSION_1_6;
	}

	JNIEXPORT jboolean JNICALL Java_com_mad_tunerlib_recognition_NativeInterface_Terminate(
			JNIEnv * env, jclass obj)
	{
		LOGD("Terminating lib!");
#ifdef PROFILER_ENABLED
		moncleanup();
#endif
		return true;
	}

	bool baseProcessBuffer(HostWrapper& rPlugin, jshortArray pSamples,
			jboolean fIsCopy, JNIEnv* pEnv)
	{
		word* pShortArray = (word*) (pEnv->GetShortArrayElements(pSamples,
				&fIsCopy));
		jsize length = pEnv->GetArrayLength(pSamples);

		// The size is in words and a byte length is required.
		bool fResult = rPlugin.runPlugin(pShortArray, length * sizeof(word));

		pEnv->ReleaseShortArrayElements(pSamples, (jshort*) pShortArray, 0);

		return fResult;
	}

	JNIEXPORT jboolean JNICALL
	Java_com_mad_tunerlib_recognition_NativeInterface_ProcessChordData(
			JNIEnv * pEnv, jclass obj, jboolean fExtractFeatures,
			jshortArray pSamples, jobjectArray o_arChords,
			jclass clsChordDetectionInfo)
	{
		if (g_arWrappers == NULL)
		{
			LOGD("Cannot process");
			return false;
		}

		jboolean fIsCopy = false;

		HostWrapper& rWrapper = g_arWrappers[ePlugin_Chordino];
		baseProcessBuffer(rWrapper, pSamples, fIsCopy, pEnv);

		if (fExtractFeatures)
		{
			jfieldID fldTime = pEnv->GetFieldID(clsChordDetectionInfo, "Time",
					"F");
			jfieldID fldChord = pEnv->GetFieldID(clsChordDetectionInfo, "Chord",
					"Ljava/lang/String;");
			RETURN_IF_FALSE(fldChord);
			RETURN_IF_FALSE(fldTime);

			Plugin::FeatureSet featureSet = rWrapper.extractRemainingFeatures();

			Plugin::FeatureList& flFeatures = featureSet[0];
			word wArraySize = pEnv->GetArrayLength(o_arChords);
			jobject pCurrentObj = NULL;
			for (unsigned int i = 0; i < flFeatures.size() && i < wArraySize;
					++i)
			{
				pCurrentObj = pEnv->GetObjectArrayElement(o_arChords, i);
				jstring asJstring = nativeStringToJstring(pEnv,
						flFeatures[i].label.c_str());
				pEnv->SetObjectField(pCurrentObj, fldChord, asJstring);

				RealTime rtTimeStarted = flFeatures[i].timestamp;
				float sfTimeStarted = rtTimeStarted.sec + ((1.0 * rtTimeStarted.nsec) / 1000000000);
				pEnv->SetFloatField(pCurrentObj, fldTime, sfTimeStarted);

			}
			Plugin::FeatureSet::iterator iterator = featureSet.begin();
			if (iterator == featureSet.end() || iterator->second.size() == 0)
			{
				return false;
			}

		}

		return true;
	}

	JNIEXPORT jboolean JNICALL Java_com_mad_tunerlib_recognition_NativeInterface_ProcessTuning(
			JNIEnv * pEnv, jclass obj, jshortArray pSamples,
			jboolean fExtractFeatures, jobject noteTuningResult,
			jclass outClass)
	{
		if (g_arWrappers == NULL)
		{
			COUT("Cannot process");
			RETURN_FALSE()
			;
		}

		jboolean fIsCopy = true;

		short* pShortArray = (short*) (pEnv->GetShortArrayElements(pSamples, &fIsCopy));
		int16_t nLength = pEnv->GetArrayLength(pSamples);

		// Convert those to signed short.
		Yin_init(&yinObject, nLength, 0.05);
		float sfPitch = Yin_getPitch(&yinObject, pShortArray);
		float sfProbability = Yin_getProbability(&yinObject);
		pEnv->ReleaseShortArrayElements(pSamples, (jshort*) pShortArray, 0);
		if(sfProbability < 0.9)
		{
			return false;
		}

		VAR_TELLER(sfPitch);
		VAR_TELLER(sfProbability);
//		HostWrapper& rWrapper = g_arWrappers[ePlugin_Tuning];
		{
//			RETURN_IF_FALSE(
//					baseProcessBuffer(rWrapper, pSamples, fIsCopy, pEnv));

			if (fExtractFeatures)
			{
				jfieldID fldNoteIndex = pEnv->GetFieldID(outClass,
						"o_nNoteIndex",
						"I");
				jfieldID fldProbability = pEnv->GetFieldID(outClass,
						"o_sfProbability",
						"F");
				jfieldID fldFreq = pEnv->GetFieldID(outClass,
						"o_sfFrequency",
						"F");
				jfieldID fldTimestamp = pEnv->GetFieldID(outClass,
						"o_lTimestamp",
						"J");
				RETURN_IF_FALSE(fldNoteIndex);
				RETURN_IF_FALSE(fldProbability);
				RETURN_IF_FALSE(fldFreq);
				RETURN_IF_FALSE(fldTimestamp);

//				Plugin::FeatureSet featureSet = rWrapper.extractRemainingFeatures();

//				Plugin::FeatureSet::iterator iterator = featureSet.begin();
//				if (iterator == featureSet.end() || iterator->second.size() == 0)
//				{
//					return false;
//				}

//				int nRequestedNoteIndex = (int) pEnv->GetIntField(
//						noteTuningResult, fldNoteIndex);
//				Plugin::FeatureList::iterator iteratorNote  = iterator->second.begin();
//				int nCalcedNoteIndex = iteratorNote->values[0];
//				if (nCalcedNoteIndex == nRequestedNoteIndex)
//				{
				pEnv->SetIntField(noteTuningResult, fldNoteIndex,
						0);
				pEnv->SetFloatField(noteTuningResult, fldProbability,
						sfProbability);
				pEnv->SetFloatField(noteTuningResult, fldFreq,
						sfPitch);
				pEnv->SetLongField(noteTuningResult, fldTimestamp,
						 now_ms());
				return true;
//				}
			}
		}

		return false;
	}

	jstring nativeStringToJstring(JNIEnv * pEnv, const char* szSource)
	{

		jstring strJaveString;

		strJaveString = pEnv->NewStringUTF(szSource);

		return strJaveString;
	}

}
