package com.mad.guitarteacher.practice.StagePriotitizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import com.mad.guitarteacher.practice.ExerciseDoneReport;
import com.mad.guitarteacher.practice.ExercisePlan;
import com.mad.guitarteacher.practice.ExerciseStageOption;
import com.mad.guitarteacher.practice.IReadOnlyExerciseField;
import com.mad.guitarteacher.practice.IReadOnlyExerciseStage;
import com.mad.lib.utils.ErrorHandler;

public class BaseStagePriotitizer implements IStagePrioritizer
{
	// HashMap<ExerciseStageOption, Integer> m_Scores = new
	// HashMap<ExerciseStageOption, Integer>();

	static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(final Map<K, V> map)
	{
		SortedSet<Map.Entry<K, V>> sortedEntries =
				new TreeSet<Map.Entry<K, V>>(new Comparator<Map.Entry<K, V>>()
				{
					@Override
					public int compare(	final Map.Entry<K, V> e1,
										final Map.Entry<K, V> e2)
					{
						int nRes =
								e1.getValue().compareTo(
										e2.getValue());
						// Special fix to preserve items with equal values
						return nRes != 0 ? nRes : 1;

					}
				});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	@Override
	public ExercisePlan createGeneralStagePlan(	final Map<String, Integer> arStageOptionsScores,
												final Collection<IReadOnlyExerciseField> arFields)
	{
		HashMap<IReadOnlyExerciseStage, Integer> stageScores =
				new HashMap<IReadOnlyExerciseStage, Integer>();

		// Iterate over all fields to get all stages that are
		for (IReadOnlyExerciseField field : arFields)
		{
			// Iterate over all stages, taking out those that are not available.
			for (IReadOnlyExerciseStage stage : field.getInfo()
					.getStages())
			{
				if (!stage.getDependencyInformation()
						.isAvailable())
				{
					continue;
				}

				int nTotalScore = 0;
				for (ExerciseStageOption option : stage
						.getOptions())
				{
					Integer nScore =
							arStageOptionsScores.get(option
									.getId());
					if (nScore != null)
					{
						nTotalScore += nScore;
					}
				}

				stageScores.put(stage, nTotalScore
						/ stage.getOptions().size());
			}
		}

		int nMinimalScore = Integer.MAX_VALUE;
		IReadOnlyExerciseStage minimalStage = null;
		// Now get the field with least score.
		for (Entry<IReadOnlyExerciseStage, Integer> kvPair : stageScores
				.entrySet())
		{
			if (kvPair.getValue() < nMinimalScore)
			{
				minimalStage = kvPair.getKey();
				nMinimalScore = kvPair.getValue();
			}
		}

		return createStagePlan(arStageOptionsScores,
				minimalStage, 3);
	}

	@Override
	public ExercisePlan createStagePlan(final Map<String, Integer> arStageOptionScores,
										final IReadOnlyExerciseStage stage,
										final int nNumberOfStages)
	{
		if (stage == null)
		{
			ErrorHandler.HandleError(new NullPointerException());
			return null;
		}

		ExercisePlan result = new ExercisePlan();
		Map<ExerciseStageOption, Integer> arCurrentStageOptionScores =
				new HashMap<ExerciseStageOption, Integer>();
		for (ExerciseStageOption exerciseStageOption : stage
				.getOptions())
		{
			Integer nScore =
					arStageOptionScores.get(exerciseStageOption);
			if (nScore == null)
			{
				nScore = 0;
			}
			arCurrentStageOptionScores.put(exerciseStageOption,
					nScore);
		}

		int nAdded = 0;

		SortedSet<Entry<ExerciseStageOption, Integer>> mapSortedByValue =
				entriesSortedByValues(arCurrentStageOptionScores);
		for (Entry<ExerciseStageOption, Integer> kvPair : mapSortedByValue)
		{
			if (nAdded >= nNumberOfStages)
			{
				break;
			}

			result.add(kvPair.getKey());
			nAdded++;
		}

		// Go through the reports.
		// for (ExerciseStageOption report : stage.getOptions())
		// {
		// ExerciseStageOption key = report.
		// BaseReportPrioritizationInfo reportInfoCurr;
		//
		// if (arTypes.containsKey(report))
		// {
		// reportInfoCurr = (BaseReportPrioritizationInfo) arTypes
		// .get(report);
		// }
		// else
		// {
		// reportInfoCurr = new BaseReportPrioritizationInfo();
		// }
		//
		// if(m_Scores.get(report) == null)
		// {
		// m_Scores.put(report, 0);
		// }
		//
		// reportInfoCurr.setInfo(report);
		// reportInfoCurr.Count++;
		// reportInfoCurr.CombinedScore += ;
		//
		// ExerciseStage stage = key.getParent();
		// if (reportInfoCurr.HighestLevel < stage.getLevel())
		// {
		// reportInfoCurr.HighestLevel = stage.getLevel();
		// }
		//
		// reportInfoCurr.TotalTime.add(report.getInfo().TotalTime);
		//
		// arTypes.put(key, reportInfoCurr);
		// }

		// TODO: I think we need to also mind the total score.

		// for (IReportPrioritizationInfo reportInfoCurr : arTypes.values())
		// {
		// // XXX: Here we can now create new exercises, but based on what?
		// // [Kidder 28 Apr 2013 - 01:21:42]: The exercise manager should give
		// // you the right field.
		// // From the right field you decide which level to take.
		// // Remember that in this function you have access to the lowest
		// // score stages and to the least played fields. You can just take
		// // the according stages and put them in the array.
		//
		// result.add(reportInfoCurr.getInfo());
		//
		// if (result.size() == nNumberOfStages)
		// {
		// break;
		// }
		// }

		return result;
	}

	@Override
	public boolean initialize(final ArrayList<ExerciseDoneReport> arData)
	{
		if ((arData == null) || (arData.size() == 0))
		{
			return false;
		}
		return true;
		// return m_arReports.addAll(arData);
	}

	@Override
	public boolean update(final ExerciseDoneReport repReport)
	{
		// return m_arReports.add(repReport);
		return true;
	}

}
