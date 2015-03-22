package com.mad.guitarteacher.practice;

import java.io.IOException;
import java.util.HashMap;

import android.content.Context;

import com.mad.guitarteacher.connect.UserProfile;
import com.mad.guitarteacher.practice.Scenarios.OnScenarioDoneListener;
import com.mad.guitarteacher.practice.StagePriotitizer.BaseStagePriotitizer;
import com.mad.guitarteacher.practice.StagePriotitizer.PracticeScheduler;
import com.mad.guitarteacher.utils.Services.JSON.JSONSerializer;
import com.mad.lib.utils.ErrorHandler;

/**
 * Manages all tasks related to loading, starting and reporting exercises.
 * 
 * @author Nati
 * 
 */
public class ExercisesManager implements OnScenarioDoneListener
{
	private final ExerciseLevelsMapFile				m_LevelsMap;
	private final PracticeScheduler					m_pPracticeScheduler;

	private final HashMap<String, ExerciseStage>	m_mapStagesInfo;
	private final HashMap<String, ExerciseField>	m_mapFieldsInfo;
	private final StageDependencyMapper				m_DepMapper;
	private final UserProfile						m_ActiveProfile;

	public ExercisesManager(final Context context,
							UserProfile userProfile) throws InstantiationException,
													IllegalAccessException,
													IOException
	{
		m_ActiveProfile = userProfile;
		m_mapStagesInfo = new HashMap<String, ExerciseStage>();
		m_LevelsMap = new ExerciseLevelsMapFile();
		m_mapFieldsInfo = new HashMap<String, ExerciseField>();
		m_DepMapper = new StageDependencyMapper();
		if (!loadState(context))
		{
			ErrorHandler
					.HandleError(new IllegalStateException());
		}

		// Now build the hash map.
		for (ExerciseField field : m_LevelsMap.Fields)
		{
			m_mapFieldsInfo.put(field.getID(), field);
			for (ExerciseStage stage : field.getStages())
			{
				m_mapStagesInfo.put(stage.getID(), stage);
			}
		}

		if (!m_DepMapper.initialize(m_mapFieldsInfo,
				m_mapStagesInfo, userProfile
						.getUserInformation()))
		{
			ErrorHandler
					.HandleError(new ExceptionInInitializerError());
		}

		m_pPracticeScheduler =
				new PracticeScheduler(	context,
										new BaseStagePriotitizer(),
										userProfile
												.getUserHistory());
	}

	public IReadOnlyLevelsMapFile getLevelMap()
	{
		return m_LevelsMap;
	}

	/**
	 * Returns the practice scheduler.
	 * 
	 * @return PracticeScheduler - The instance of the practice scheduler.
	 */
	public PracticeScheduler getPracticeScheduler()
	{
		return m_pPracticeScheduler;
	}

	public ExerciseStage getStage(String strStageId)
	{
		if (strStageId == null)
		{
			return null;
		}

		if (m_mapStagesInfo.containsKey(strStageId))
		{
			return m_mapStagesInfo.get(strStageId);
		}

		return null;
	}

	public ExerciseStageOption getStageOptionByName(final String fullName)
	{
		String[] arParts = fullName.split("\\|");
		if (arParts.length != 2)
		{
			return null;
		}
		ExerciseStage stage = m_mapStagesInfo.get(arParts[0]);
		if (stage == null)
		{
			return null;
		}

		return stage.getOption(arParts[1]);
	}

	/**
	 * Loads a state for the manager from a stream.
	 * 
	 * @param InputStream
	 *            - Stream to load the state from.
	 * 
	 * @return boolean - true if succeeded.
	 * @throws IOException
	 */
	private boolean loadState(final Context context)
	{
		JSONSerializer deserializer = new JSONSerializer();

		// Read the stages map.
		if ((!deserializer.read(context, m_LevelsMap)))
		{
			return false;
		}

		return true;
	}

	/**
	 * Reports on an exercise done.
	 * 
	 * @param repReport
	 *            - The report to be reported.
	 * 
	 * @return boolean - true if succeeded.
	 */
	// TODO: Rported to who? the user? the server? both? and the fuck how?
	@Override
	public void onScenarioDone(final ExerciseDoneReport report)
	{
		// Update the file.
		JSONSerializer ser = new JSONSerializer();
		ser.write(m_ActiveProfile.getUserHistory());
	}

	/**
	 * Saves the state of the exercise to a stream.
	 * 
	 * @param OutputStream
	 *            - Stream to write the sate to.
	 * 
	 * @return boolean - true if succeeded.
	 */
	public boolean saveState()
	{
		JSONSerializer pSerializer = new JSONSerializer();

		pSerializer.write(m_ActiveProfile.getUserInformation());
		pSerializer.write(m_ActiveProfile.getUserHistory());

		return true;
	}

	public UserProfile getActiveProfile()
	{
		return m_ActiveProfile;
	}

}
