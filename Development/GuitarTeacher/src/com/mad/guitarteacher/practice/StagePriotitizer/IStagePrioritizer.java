package com.mad.guitarteacher.practice.StagePriotitizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.mad.guitarteacher.practice.ExerciseDoneReport;
import com.mad.guitarteacher.practice.ExercisePlan;
import com.mad.guitarteacher.practice.IReadOnlyExerciseField;
import com.mad.guitarteacher.practice.IReadOnlyExerciseStage;

public interface IStagePrioritizer
{
	/**
	 * Creates a general stage plan, based on getting the most important stage
	 * to be played now.
	 * 
	 * @param nNumberOfStageOptions
	 * @return
	 */
	public ExercisePlan createGeneralStagePlan(final Map<String, Integer> arStageOptionScores,
											final Collection<IReadOnlyExerciseField> arFields);

	/**
	 * Return the next stageOptions for the given stage.
	 * 
	 * @param nNumberOfStageOptions
	 *            Number of stages requested
	 * @return The stages to be ran for the user.
	 */
	public ExercisePlan createStagePlan(	final Map<String, Integer> arStageOptionScores,
										final IReadOnlyExerciseStage stage,
										final int nNumberOfStages);

	/**
	 * Initialize the stage priotiarizer.
	 * 
	 * @param arData
	 *            - The stages to be prioritized.
	 * @return true if successful.
	 */
	public boolean initialize(ArrayList<ExerciseDoneReport> arData);

	/**
	 * Update the stage initializer data.
	 * 
	 * @param arData
	 *            - The stages to be prioritized with the already existing data.
	 * @return true if successful.
	 */
	public boolean update(ExerciseDoneReport repReport);
}
