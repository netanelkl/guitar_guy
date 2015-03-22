package com.mad.guitarteacher.practice;

import java.util.Map;
import java.util.Map.Entry;

import com.mad.guitarteacher.dataContract.GeneralUserInformationFile;

/**
 * This class is meant to fill the mapping of dependencies on the LevelMap, and
 * to give information on the currently available stages.
 * 
 * @author Nati
 * 
 */
public class StageDependencyMapper
{
	/**
	 * Relying on the levels map, and on information about current stages in
	 * fields
	 * 
	 * @param mapFields
	 * @param userInfo
	 */
	public boolean initialize(final Map<String, ExerciseField> mapFields,
								final Map<String, ExerciseStage> mapStages,
								final GeneralUserInformationFile userInfo)
	{
		for (Entry<String, ExerciseStage> stage : mapStages.entrySet())
		{
			if (stage.getValue().initializeDependency(mapStages) == false)
			{
				return false;
			}
		}

		// First of all, we set the current stage in all of the stages.
		for (Entry<String, Integer> entry : userInfo.CurrentStages.entrySet())
		{
			ExerciseField field = mapFields.get(entry.getKey());

			if (field == null)
			{
				return false;
			}
			field.setCurrentStage(entry.getValue());
		}

		for (Entry<String, Integer> entry : userInfo.BestScores.entrySet())
		{
			ExerciseStage stage = mapStages.get(entry.getKey());
			int nStageScore = entry.getValue();
			stage.getDependencyInformation().setValue(nStageScore);
		}

		return true;
	}
}
