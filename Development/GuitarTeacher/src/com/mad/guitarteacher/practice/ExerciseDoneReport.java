package com.mad.guitarteacher.practice;

import org.json.JSONObject;

import com.mad.guitarteacher.dataContract.DataContractBase;
import com.mad.guitarteacher.dataContract.ExerciseDoneReportInfo;
import com.mad.guitarteacher.services.JSONSerializerBase;
import com.mad.guitarteacher.utils.Definitions;
import com.mad.lib.R;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.lib.utils.TimeSpan;

public class ExerciseDoneReport extends DataContractBase
{

	/**
	 * The serialized information - the saved content of this class.
	 */
	private ExerciseDoneReportInfo	m_FieldInfo;

	private ExerciseStageOption		m_StageOption;

	public ExerciseDoneReport(	final ExerciseStageOption stageOption,
								final ExerciseDoneReportInfo info)
	{
		m_StageOption = stageOption;
		m_FieldInfo = info;
	}

	public ExerciseDoneReport(	final ExerciseStageOption stageOption,
								final int nScore,
								final TimeSpan totalTime)
	{
		m_StageOption = stageOption;
		m_FieldInfo =
				new ExerciseDoneReportInfo(	stageOption.getId(),
											nScore,
											totalTime);
	}

	/**
	 * Get the info of the report.
	 * 
	 * @return info of the finished game.
	 */
	public ExerciseDoneReportInfo getInfo()
	{
		return m_FieldInfo;
	}

	/**
	 * Retrieves the unique stageOption associated with this report.
	 * 
	 * @return
	 */
	public ExerciseStageOption getStageOption()
	{
		if (m_StageOption == null)
		{
			ExercisesManager exercisesManager =
					AppLibraryServiceProvider.getInstance().get(
							R.service.exercises_manager);
			m_StageOption =
					exercisesManager
							.getStageOptionByName(m_FieldInfo.StageOption);
		}

		return m_StageOption;
	}

	public int getStars()
	{
		return Definitions.Scores.getStars(m_FieldInfo.Score);

	}

	@Override
	public boolean readObject(final JSONSerializerBase serializer)
	{
		if (m_FieldInfo == null)
		{
			m_FieldInfo = new ExerciseDoneReportInfo();
		}

		return m_FieldInfo.readObject(serializer);
	}

	public JSONObject storeObject(	final JSONSerializerBase serializer,
									final Class<?> objClass)
	{
		return m_FieldInfo.storeObject(serializer);
	}
}
