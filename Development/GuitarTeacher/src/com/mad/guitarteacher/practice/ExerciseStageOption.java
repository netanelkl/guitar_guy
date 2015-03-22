package com.mad.guitarteacher.practice;

import com.mad.guitarteacher.practice.Scenarios.ExerciseScenario;
import com.mad.lib.utils.HelperMethods;

public class ExerciseStageOption
{
	private final ExerciseStage	m_Parent;

	private final String		m_Param;

	private final String		m_Id;

	private String				m_Name;
	/**
	 * The ExerciseScenario is loaded by m_StageInfo.Name, with params.
	 */
	private ExerciseScenario	m_ExerciseScenario;

	public ExerciseStageOption(final ExerciseStage parent, final String param)
	{
		m_Parent = parent;
		m_Param = param;
		m_Id = m_Parent.getID() + "|" + m_Param;
		m_Name = null;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (o instanceof ExerciseStageOption)
		{
			ExerciseStageOption other = (ExerciseStageOption) o;
			return m_Param.equals(other.m_Param) && (m_Parent.getType() == other
							.getParent().getType());
		}
		return false;
	}

	/**
	 * Returns the exercise scenario of the current stage.
	 * 
	 * @return ExerciseScenario - The exercise scenario of the current stage.
	 */
	public ExerciseScenario getExerciseScenario()
	{
		if (m_ExerciseScenario == null)
		{
			m_ExerciseScenario = ExerciseStageFactory.getScenario(this);
		}

		return m_ExerciseScenario;
	}

	public String getId()
	{
		return m_Id;
	}

	public String getParam()
	{
		return m_Param;
	}

	public ExerciseStage getParent()
	{
		return m_Parent;
	}

	@Override
	public String toString()
	{
		if (m_Name == null)
		{
			String strRegexName = m_Parent.getInfo().Name;
			if (m_Param != null)
			{
				String[] arParams = HelperMethods.parseParameters(m_Param);

				// Go through all parameters in this.name and find the [0],[1]
				// in the Parent.name pattern.
				// And replace them so they would be fit to our name.
				for (int nParamIndex = 0; nParamIndex < arParams.length; nParamIndex++)
				{
					strRegexName = strRegexName.replace("[" + (nParamIndex + 1)
														+ "]",
							arParams[nParamIndex]);
				}
			}

			m_Name = strRegexName;
		}
		return m_Name;
	}

	/**
	 * @return the m_Name
	 */
	public String getName()
	{
		return m_Name;
	}
}
