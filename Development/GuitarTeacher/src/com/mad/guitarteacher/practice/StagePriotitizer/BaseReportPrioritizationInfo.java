package com.mad.guitarteacher.practice.StagePriotitizer;

import com.mad.guitarteacher.practice.ExerciseStageOption;
import com.mad.lib.utils.TimeSpan;

public class BaseReportPrioritizationInfo implements
		IReportPrioritizationInfo
{
	int							Count;
	int							CombinedScore;
	int							Priority;
	int							HighestLevel;
	TimeSpan					TotalTime;

	private ExerciseStageOption	m_Exercise;

	public BaseReportPrioritizationInfo()
	{
		Count = 0;
		CombinedScore = 0;
		Priority = 0;
		HighestLevel = 0;
	}

	@Override
	public int compareTo(final IReportPrioritizationInfo another)
	{
		if (another.getClass().isAssignableFrom(
				BaseReportPrioritizationInfo.class))
		{
			return ((BaseReportPrioritizationInfo) another).Count
					- Count;
		}

		return 0;
	}

	@Override
	public ExerciseStageOption getInfo()
	{
		// TODO Auto-generated method stub
		return m_Exercise;
	}

	@Override
	public boolean setInfo(final ExerciseStageOption info)
	{
		if (info == null)
		{
			return false;
		}

		m_Exercise = info;

		return true;
	}
}
