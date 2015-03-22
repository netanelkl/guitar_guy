package com.mad.tunerlib.recognition;

/**
 * Be informed on recognizing an audio object (Note, chord...).
 * 
 * Because the nature of the recognizable objct varies, the listener should
 * gather the recognizable object by casting to the right RecognizerBase class.
 * 
 */
public interface OnRecognitionDetectedListener
{

	/**
	 * Be informed on recognizing an audio object (Note, chord...).
	 * 
	 * Because the nature of the recognizable objct varies, the listener should
	 * gather the recognizable object by casting to the right RecognizerBase
	 * class.
	 * 
	 * @param recognizer
	 *            The recognizer that found the audio pattern.
	 */
	void onRecognitionDetected(DetectionInfo info);

}
