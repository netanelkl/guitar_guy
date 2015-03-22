package com.mad.guitarteacher.practice;

import com.mad.guitarteacher.dataContract.IReadOnlyExerciseFieldInfo;

public interface IReadOnlyExerciseField
{
	/**
	 * Gets the icon res Id.
	 * 
	 * @return
	 */
	public String getIcon();

	/**
	 * Gets the id of the field.
	 * 
	 * @return
	 */
	public String getID();

	/**
	 * Gets the field info object.
	 * 
	 * @return
	 */
	public IReadOnlyExerciseFieldInfo getInfo();

	/**
	 * Gets the name of the field.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Returns whether or not the field has open stages.
	 * 
	 * @return
	 */
	public boolean isAvailable();
}
