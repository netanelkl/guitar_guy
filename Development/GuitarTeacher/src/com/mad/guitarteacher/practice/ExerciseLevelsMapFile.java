package com.mad.guitarteacher.practice;

import java.util.ArrayList;

import com.mad.guitarteacher.utils.Definitions;
import com.mad.guitarteacher.utils.Services.JSON.StorableObject;

public class ExerciseLevelsMapFile extends StorableObject
		implements IReadOnlyLevelsMapFile
{
	public ArrayList<ExerciseField>	Fields;

	@Override
	public ArrayList<IReadOnlyExerciseField> getFields()
	{
		ArrayList<IReadOnlyExerciseField> readOnlyList =
				new ArrayList<IReadOnlyExerciseField>();

		for (ExerciseField field : Fields)
		{
			if (!field.getInfo().isHidden())
			{
				readOnlyList.add(field);
			}
		}

		return readOnlyList;
	}

	@Override
	public String getStorableFilename()
	{
		return Definitions.STORABLE_FILE_LEVELS;
	}

	@Override
	public boolean isStoredOnAssets()
	{
		return true;
	}

	@Override
	public boolean setDefaultInformation()
	{
		Fields = new ArrayList<ExerciseField>(0);
		return true;
	}

	@Override
	public void postReadInitialization()
	{
		if (Fields == null)
		{
			Fields = new ArrayList<ExerciseField>(0);
		}
	}
}
