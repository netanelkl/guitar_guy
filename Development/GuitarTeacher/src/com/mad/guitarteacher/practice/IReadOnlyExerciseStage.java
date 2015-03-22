package com.mad.guitarteacher.practice;

import java.util.Collection;

import com.mad.guitarteacher.dataContract.ExerciseStageInfo;

/**
 * A read-only interface for a stage.
 * 
 * Users of this interface cannot edit the stage.
 * 
 * @author Nati
 * 
 */
public interface IReadOnlyExerciseStage
{
	/**
	 * Get dependency information about the current stage.
	 * 
	 * @return
	 */
	public DependencyInformationHolder getDependencyInformation();

	/**
	 * Gets the ID of the current stage.
	 * 
	 * @return
	 */
	public String getID();

	/**
	 * Gets the info (kernel of the stage information).
	 * 
	 * @return
	 */
	public ExerciseStageInfo getInfo();

	/**
	 * Gets the current level of the scenario.
	 * 
	 * @return
	 */
	public int getLevel();

	/**
	 * Gets the name of the stage.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Gets the StageOption according to the param.
	 * 
	 * @param parameter
	 * @return
	 */
	public ExerciseStageOption getOption(final String parameter);

	/**
	 * Gets a collection of all of the StageOption children.
	 * 
	 * @return
	 */
	public Collection<ExerciseStageOption> getOptions();

	/**
	 * Gets the parent field of the current stage.
	 * 
	 * @return
	 */
	public IReadOnlyExerciseField getParent();

	/**
	 * Gets the max score averaged on all of the options in the stage.
	 * 
	 * @return
	 */
	public int getMaxScore();
}
