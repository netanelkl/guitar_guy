package com.mad.guitarteacher.practice.StagePriotitizer;

import com.mad.guitarteacher.practice.ExerciseStageOption;

public interface IReportPrioritizationInfo extends
		Comparable<IReportPrioritizationInfo>
{
	@Override
	public int compareTo(IReportPrioritizationInfo another);

	public ExerciseStageOption getInfo();

	public boolean setInfo(ExerciseStageOption info);
}
