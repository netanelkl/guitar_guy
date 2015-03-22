package com.mad.guitarteacher.practice;

import java.util.ArrayList;

import org.json.JSONObject;

import com.mad.guitarteacher.dataContract.DataContractBase;
import com.mad.guitarteacher.dataContract.ExerciseFieldInfo;
import com.mad.guitarteacher.dataContract.IReadOnlyExerciseFieldInfo;
import com.mad.guitarteacher.services.JSONSerializerBase;

public class ExerciseField extends DataContractBase implements
		IModifiableExerciseField, IReadOnlyExerciseField
{
	private static final String	DEFAULT_ICON	= "field_rec";
	/**
	 * The serialized information - the saved content of this class.
	 */
	private ExerciseFieldInfo	m_FieldInfo;

	private int					m_nCurrentStage;

	public ExerciseField()
	{
	}

	/**
	 * Returns the current exercise stage of the exercise field.
	 * 
	 * @return ExerciseStage - Current exercise stage.
	 */
	public ExerciseStage getCurrentStage()
	{
		if (m_nCurrentStage >= m_FieldInfo.Stages.size())
		{
			throw new ArrayIndexOutOfBoundsException();
		}

		return m_FieldInfo.Stages.get(m_nCurrentStage);
	}

	/**
	 * Get the exercise field icon.
	 * 
	 * @return Icon The fields icon.
	 */
	// TODO: We should have a default icon somewhere.
	// Kidder: Yes, we have time before we get to the activities themselves.
	@Override
	public String getIcon()
	{
		return m_FieldInfo.ImgResId;
	}

	/**
	 * Get the ID of the Exercise field.
	 * 
	 * @return String - ID of the exercise field.
	 */
	@Override
	public String getID()
	{
		return m_FieldInfo.Id;
	}

	/**
	 * Get the info of the Exercise field.
	 * 
	 * @return ExerciseFieldInfo - info of the exercise field.
	 */
	@Override
	public IReadOnlyExerciseFieldInfo getInfo()
	{
		return m_FieldInfo;
	}

	/**
	 * Get the name of the Exercise field.
	 * 
	 * @return String - name of the exercise field.
	 */
	@Override
	public String getName()
	{
		if (m_FieldInfo.Name.equals(""))
		{
			return DEFAULT_ICON;
		}

		return m_FieldInfo.Name;
	}

	@Override
	public ArrayList<ExerciseStage> getStages()
	{
		return m_FieldInfo.Stages;
	}

	@Override
	public boolean readObject(final JSONSerializerBase serializer)
	{
		if (m_FieldInfo == null)
		{
			m_FieldInfo = new ExerciseFieldInfo();
		}

		boolean fResult = m_FieldInfo.readObject(serializer);
		if (fResult == false)
		{
			return false;
		}

		for (ExerciseStage stage : m_FieldInfo.Stages)
		{
			stage.initialize(this);
		}
		if (m_FieldInfo.ImgResId == null)
		{
			m_FieldInfo.ImgResId = "";
		}

		return fResult;
	}

	@Override
	public void setCurrentStage(final int nCurrentStage)
	{
		m_nCurrentStage = nCurrentStage;
	}

	public JSONObject storeObject(	final JSONSerializerBase serializer,
									final Class<?> objClass)
	{
		return m_FieldInfo.storeObject(serializer);
	}

	@Override
	public boolean isAvailable()
	{
		for (IReadOnlyExerciseStage stage : m_FieldInfo
				.getStages())
		{
			if (stage.getDependencyInformation().isAvailable())
			{
				return true;
			}
		}

		return false;
	}
}
