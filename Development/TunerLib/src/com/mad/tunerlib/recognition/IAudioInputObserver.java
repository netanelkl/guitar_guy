package com.mad.tunerlib.recognition;

/**
 * An interface for handling an audio packet received
 * 
 * @author Nati
 * 
 */
public interface IAudioInputObserver
{

	/**
	 * Be informed of input audio data received.
	 * 
	 * @param arSamples
	 *            The audio packet.
	 */
	void AudioPacketReceived(short[] arSamples);

}
