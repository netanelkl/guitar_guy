package com.mad.guitarteacher.practice;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.mad.guitarteacher.dataContract.DataContractBase;
import com.mad.guitarteacher.dataContract.ExerciseStageInfo;
import com.mad.guitarteacher.dataContract.GeneralUserInformationFile;
import com.mad.guitarteacher.services.JSONSerializerBase;
import com.mad.lib.R;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.lib.utils.ErrorHandler;
import com.mad.lib.utils.HelperMethods;

public class ExerciseStage extends DataContractBase implements
		IReadOnlyExerciseStage
{
	/**
	 * What level is this in the field.
	 * 
	 * This will be dynamically filled.
	 */
	public int											m_nLevel;

	private final HashMap<String, ExerciseStageOption>	m_Options	=
																			new HashMap<String, ExerciseStageOption>();

	/*
	 * This is private because it is not saved for each parent but rather set by
	 * the parenting field.
	 */
	private ExerciseField								m_Parent;

	/**
	 * Holds all information related to dependencies.
	 */
	public DependencyInformationHolder					m_DependencyInformation;

	/**
	 * The backed information used to build this stage.
	 * 
	 */
	private ExerciseStageInfo							m_StageInfo;

	/**
	 * Create a new instance of the exercise stage object.
	 * 
	 * @param int - nLevel Level of the exercise.
	 */
	public ExerciseStage()
	{
		m_nLevel = 0;
	}

	/**
	 * Parse the options and fill the map with stageOptions.
	 */
	public void fillOptionsMap(final String optionParam)
	{

	}

	@Override
	public DependencyInformationHolder getDependencyInformation()
	{
		return m_DependencyInformation;
	}

	/**
	 * Get the exercise ID.
	 * 
	 * @return String - ID.
	 */
	@Override
	public String getID()
	{
		return m_StageInfo.Id;
	}

	/**
	 * Returns the stage info.
	 * 
	 * @return ExerciseStageInfo
	 */
	@Override
	public ExerciseStageInfo getInfo()
	{
		return m_StageInfo;
	}

	/**
	 * Returns the level of this stage. for a specific stage this is const.
	 * 
	 * Field -> Stages(has Scenario) -> Acts
	 * 
	 * @return int - Level of stage.
	 */
	@Override
	public int getLevel()
	{
		return m_nLevel;
	}

	/**
	 * Returns the name of the stage.
	 * 
	 * @return String - The name of the current stage.
	 */
	@Override
	public String getName()
	{
		return m_StageInfo.GroupName;
	}

	@Override
	public ExerciseStageOption getOption(final String parameter)
	{
		return m_Options.get(parameter);
	}

	/**
	 * Get all of the available options.
	 * 
	 * @return
	 */
	@Override
	public Collection<ExerciseStageOption> getOptions()
	{
		return m_Options.values();
	}

	@Override
	public IReadOnlyExerciseField getParent()
	{
		return m_Parent;
	}

	/**
	 * Get the exercise type.
	 * 
	 * @return ExerciseTypes - Type.
	 */
	public ExerciseTypes getType()
	{
		return m_StageInfo.Type;
	}

	public void initialize(final ExerciseField parent)
	{
		m_Parent = parent;
	}

	public boolean initializeDependency(final Map<String, ExerciseStage> mapStages)
	{
		// Already initialized.
		if (m_DependencyInformation != null)
		{
			return true;
		}

		if ((m_StageInfo.Dependency == null)
				|| (m_StageInfo.Dependency == ""))
		{
			m_DependencyInformation =
					new DependencyInformationHolder(this);
			return true;
		}

		String[] arDepParams =
				HelperMethods
						.parseParameters(m_StageInfo.Dependency);

		Integer nThresholdValue;
		if (arDepParams.length != 2)
		{
			return false;
		}
		try
		{
			nThresholdValue = Integer.parseInt(arDepParams[1]);
		}
		catch (NumberFormatException e)
		{
			ErrorHandler.HandleError(e);
			return false;
		}

		ExerciseStage dependentStage =
				mapStages.get(arDepParams[0]);

		if (dependentStage == null)
		{
			return true;
		}

		if (dependentStage.m_DependencyInformation == null)
		{
			dependentStage.initializeDependency(mapStages);
		}
		m_DependencyInformation =
				new DependencyInformationHolder(this,
												dependentStage.m_DependencyInformation,
												nThresholdValue);

		dependentStage.m_DependencyInformation
				.addDependsOnMe(m_DependencyInformation);

		return true;
	}

	@Override
	public boolean readObject(final JSONSerializerBase serializer)
	{
		if (m_StageInfo == null)
		{
			m_StageInfo = new ExerciseStageInfo();
		}

		boolean fRead = m_StageInfo.readObject(serializer);

		if (fRead)
		{
			String[] arParams =
					HelperMethods
							.parseParameters(m_StageInfo.Parameters);

			// Check if there are any params.
			if (arParams != null)
			{
				// Create the options. This is probably not the right place for
				// this.
				for (String param : arParams)
				{
					m_Options
							.put(param,
									new ExerciseStageOption(this,
															param));
				}
			}
		}
		return fRead;
	}

	/**
	 * Stores the object to the serializes.
	 * 
	 * <b>virtual</b> - provides non-default implementation.
	 */
	public JSONObject storeObject(	final JSONSerializerBase serializer,
									final Class<?> objClass)
	{
		return m_StageInfo.storeObject(serializer);

	}

	@Override
	public int getMaxScore()
	{
		ExercisesManager exercisesManager =
				AppLibraryServiceProvider.getInstance().get(
						R.service.exercises_manager);
		GeneralUserInformationFile userInfo =
				exercisesManager.getActiveProfile()
						.getUserInformation();

		int nTotal = 0;
		Collection<ExerciseStageOption> arOptions =
				m_Options.values();
		for (ExerciseStageOption stageOption : arOptions)
		{

			Integer nScore =
					userInfo.BestScores.get(stageOption.getId());
			if (nScore != null)
			{
				nTotal += nScore;
			}
		}
		return nTotal / arOptions.size();
	}
}
