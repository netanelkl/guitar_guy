package com.mad.guitarteacher.dataContract;

import java.util.ArrayList;

import com.mad.guitarteacher.practice.IReadOnlyExerciseStage;

public interface IReadOnlyExerciseFieldInfo
{
	/**
	 * returns the Id of the field.
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * Returns the resId of the image.
	 * 
	 * @return
	 */
	public String getImgResId();

	/**
	 * Returns the name of the field.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Tells if the field should be hidden from view.
	 * 
	 * Meaning it contains supporting stages.
	 * 
	 * @return
	 */
	public boolean isHidden();

	/**
	 * Returns all the stages in the field in a read only fashion.
	 * 
	 * @return
	 */
	public ArrayList<IReadOnlyExerciseStage> getStages();
}
