package com.mad.tunerlib.tuning;

import com.mad.lib.musicalBase.OctavedNote;

/**
 * Interface for a specific tuning.
 * 
 * @author Tom
 * 
 */
public interface ITuning
{
	/**
	 * Get the tuning.
	 * 
	 * @return Array of notes, composing the current tuning.
	 */
	OctavedNote[] getTuning();
}
