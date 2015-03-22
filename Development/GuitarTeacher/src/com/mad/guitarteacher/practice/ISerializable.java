package com.mad.guitarteacher.practice;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public interface ISerializable
{
	/**
	 * Saves the state of the exercise to a stream.
	 * 
	 * @param OutputStreamWriter
	 *            - Stream to write the sate to.
	 * 
	 * @return boolean - true if succeeded.
	 */
	public boolean saveState(OutputStreamWriter oswStream);

	/**
	 * Loads a state for the manager from a stream.
	 * 
	 * @param InputStreamReader
	 *            - Stream to load the state from.
	 * 
	 * @return boolean - true if succeeded.
	 */
	public boolean loadState(InputStreamReader isrStream);
}
