#ifndef JNI_INTERFACE_H
#define JNI_INTERFACE_H

#include <jni.h>

extern "C"
{


JNIEXPORT jboolean JNICALL Java_com_mad_tunerlib_recognition_NativeInterface_Initialize(
		JNIEnv * env, jclass obj);

/**
 * A hook for when the JNI is loaded.
 */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM * env, void* res);

/**
 * Process data for the purpose of detecting chords.
 *
 *	@param 	pSamples The array of input data.
 *
 *	@return	A string containing the recognizable chord.
 */
JNIEXPORT jboolean JNICALL Java_com_mad_tunerlib_recognition_NativeInterface_ProcessChordData(
		JNIEnv * pEnv, jclass obj,
		jboolean fExtractFeatures,
		jshortArray pSamples,
		jobjectArray o_arChords,
		jclass clsChordDetectionInfo);

/**
 * Process data for the purpose of detecting a note.
 *
 * @param pSamples The array of input data.
 * @param pNoteTuningResult	The object used to store the results.
 *
 */
JNIEXPORT jboolean JNICALL Java_com_mad_tunerlib_recognition_NativeInterface_ProcessTuning(
		JNIEnv * pEnv, jclass obj, jshortArray pSamples,jboolean fExtractFeatures,
		jobject noteTuningResult, jclass outClass);


JNIEXPORT jboolean JNICALL Java_com_mad_tunerlib_recognition_NativeInterface_Terminate(
		JNIEnv * env, jclass obj);
}
#endif
