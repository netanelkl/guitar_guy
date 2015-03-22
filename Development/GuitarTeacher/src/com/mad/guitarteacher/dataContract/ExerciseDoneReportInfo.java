package com.mad.guitarteacher.dataContract;

import com.mad.lib.utils.TimeSpan;

/**
 * A report about the score of a given stage.
 * 
 * @author Nati
 * 
 */
public class ExerciseDoneReportInfo extends DataContractBase
{
	/**
	 * The stage played.
	 */
	public String	StageOption;

	/**
	 * The score the user got.
	 */
	public int		Score;

	/**
	 * The total time the user spent in the exercise.
	 */
	public TimeSpan	TotalTime;

	public ExerciseDoneReportInfo()
	{

	}

	public ExerciseDoneReportInfo(	final String strStageOptionId,
									final int nScore,
									final TimeSpan totalTime)
	{
		StageOption = strStageOptionId;
		Score = nScore;
		TotalTime = totalTime;
	}
}
