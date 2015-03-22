package com.mad.lib.musicalBase.keyConverters;

public interface IKeyConverter
{
	/**
	 * Get the note table for a key.
	 * 
	 * @param nKey
	 *            - Index of the key.
	 * @return array of notes for the key.
	 */
	int[] getNoteTable(int nKey);

	/**
	 * Get the note that is X steps of the base in the key.
	 * 
	 * @param nStep
	 *            - Steps to count in the key from the base note.
	 * @return The note that is nSteps from the base note in the key.
	 */
	int getStepNote(int nStep);

	/**
	 * Return the number of notes composing the key.
	 * 
	 * @return Number of notes in key.
	 */
	int getNumberOfNotes();

	/**
	 * Get the array of intervals composing the key.
	 * 
	 * @return The array of intervals.
	 */
	int[] getIntervals();

	/**
	 * Get the key converter type.
	 * 
	 * @return Type of key converter.
	 */
	EKeyConverterType getType();
}
