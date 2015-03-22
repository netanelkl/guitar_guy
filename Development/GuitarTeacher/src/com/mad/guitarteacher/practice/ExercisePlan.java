package com.mad.guitarteacher.practice;

import java.util.ArrayList;

// TODO: Kidder, commenrs
public class ExercisePlan extends ArrayList<ExerciseStageOption>
{
	/**
	 * 
	 */
	private static final long	serialVersionUID				=
																		3760197939642353589L;

	private int					m_nCurrentStageOption			=
																		0;

	// Flags.
	public static final int		PLAN_ID_INVALID					=
																		-1;

	public static final int		PLAN_ACTION_ALLOW_CHANGE_STAGE	=
																		4;

	private int					m_nId;

	public ExercisePlan()
	{
	}

	public void advanceToNextStage()
	{
		m_nCurrentStageOption++;
	}

	public ExerciseStageOption getCurrentStageOption()
	{
		if (m_nCurrentStageOption >= size())
		{
			return null;
		}

		return get(m_nCurrentStageOption);
	}

	public int getCurrentStageOptionIndex()
	{
		return m_nCurrentStageOption;
	}

	public int getId()
	{
		return m_nId;
	}

	public boolean isDone()
	{
		return m_nCurrentStageOption == size();
	}

	public void setId(final int nId)
	{
		m_nId = nId;
	}

}
