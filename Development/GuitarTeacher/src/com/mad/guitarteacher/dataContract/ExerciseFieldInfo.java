package com.mad.guitarteacher.dataContract;

import java.util.ArrayList;

import com.mad.guitarteacher.practice.ExerciseStage;
import com.mad.guitarteacher.practice.IReadOnlyExerciseStage;

/**
 * The Serialized information about an exercise field.
 * 
 * @author Nati
 * 
 */
public class ExerciseFieldInfo extends DataContractBase
		implements IReadOnlyExerciseFieldInfo
{
	/**
	 * The Id of the field.
	 * 
	 * For example <i>FChords</i>
	 */
	public String	Id;

	/**
	 * The name of the field.
	 * 
	 * For example <i>Chords</i>
	 */
	public String	Name;

	/**
	 * The resId of the image.
	 * 
	 * For example <i>Chords</i>
	 */
	public String	ImgResId;

	@Override
	public boolean isHidden()
	{
		return Id.equals("FHidden");
	}

	/**
	 * The stages in the field.
	 */
	public ArrayList<ExerciseStage>	Stages;

	public ExerciseFieldInfo()
	{
	}

	@Override
	public String getId()
	{
		return Id;
	}

	@Override
	public String getImgResId()
	{
		return ImgResId;
	}

	@Override
	public String getName()
	{
		return Name;
	}

	@Override
	public ArrayList<IReadOnlyExerciseStage> getStages()
	{
		ArrayList<IReadOnlyExerciseStage> listStages =
				new ArrayList<IReadOnlyExerciseStage>();
		for (ExerciseStage stage : Stages)
		{
			listStages.add(stage);
		}

		return listStages;
	}

}
