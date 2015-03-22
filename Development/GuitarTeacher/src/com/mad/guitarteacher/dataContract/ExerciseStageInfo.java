package com.mad.guitarteacher.dataContract;

import com.mad.guitarteacher.practice.ExerciseTypes;

/**
 * The serialized information stored for a stage.
 * 
 * @author Nati
 * 
 */
public class ExerciseStageInfo extends DataContractBase
{
	/**
	 * Dependencies required before playing this level.
	 * 
	 * Should consider using level numerals instead. Advantages either way.
	 */
	public String			Dependency;

	/**
	 * The name of the stage.
	 * 
	 * Example: <i>Introduction: Body posture</i>
	 */
	public String			GroupName;

	/**
	 * The Id of the stage.
	 * 
	 * Note that this is String to preserve it nicely between versions.
	 * 
	 * Example: <i>"F1_Int"</i>
	 */
	public String			Id;

	/**
	 * The name of the stage.
	 * 
	 * Example: <i>Introduction: Body posture</i>
	 */
	public String			Name;

	/**
	 * Parameters used to initialize the instance.
	 * 
	 * String for convenience.
	 */
	public String			Parameters;

	/**
	 * The type of the exercise.
	 */
	public ExerciseTypes	Type;

	/**
	 * The type of the exercise.
	 */
	public String			ImgResId;

	/*
	 * Default ctor.
	 */
	public ExerciseStageInfo()
	{

	}

	@Override
	public boolean equals(final Object object)
	{
		if (!(object instanceof ExerciseStageInfo))
		{
			return false;
		}

		ExerciseStageInfo asStageInfo =
				(ExerciseStageInfo) object;

		return (this.Id == asStageInfo.Id)
				|| ((this.Type == asStageInfo.Type) && (this.Parameters == asStageInfo.Parameters));
	}

	@Override
	public int hashCode()
	{
		return Type.hashCode() + Parameters.hashCode();
	}
}
