package com.mad.guitarteacher.dataContract;

import java.util.HashMap;

import com.mad.guitarteacher.utils.Definitions;
import com.mad.guitarteacher.utils.Services.JSON.StorableObject;

/**
 * This is the information structure.
 * 
 * 
 * 
 * @author Nati
 * 
 */
public class GeneralUserInformationFile extends StorableObject
{
	private final String	strRelativePath;

	public GeneralUserInformationFile(String relativePath)
	{
		strRelativePath = relativePath;
	}

	/**
	 * Total acheived points. You can share that and stuff.
	 */
	public int						TotalPoints;

	/**
	 * A mapping between Field -> Level (stage).
	 * 
	 * For example "FieldId1" -> 3.
	 * 
	 */
	public HashMap<String, Integer>	CurrentStages;

	/**
	 * A mapping between Stage -> Best score (stage).
	 * 
	 * For example "StageId1" -> 97.
	 * 
	 */
	public HashMap<String, Integer>	BestScores;

	public boolean					FinishedIntroduction;

	public void addScore(	final String strStageId,
							final int nScore)
	{
		Integer previousScore = BestScores.get(strStageId);
		if ((previousScore == null) || (previousScore < nScore))
		{
			BestScores.put(strStageId, nScore);
		}
	}

	@Override
	public String getStorableFilename()
	{
		return strRelativePath
				+ Definitions.STORABLE_FILE_GENERAL_USER_INFORMATION;
	}

	@Override
	public boolean isStoredOnAssets()
	{
		return false;
	}

	public void setCurrentStage(final String strFieldName,
								final int nStage)
	{
		Integer nCurrentStage = CurrentStages.get(strFieldName);
		if ((nCurrentStage == null) || (nCurrentStage < nStage))
		{
			CurrentStages.put(strFieldName, nStage);
		}

	}

	@Override
	public boolean setDefaultInformation()
	{
		CurrentStages = new HashMap<String, Integer>();
		BestScores = new HashMap<String, Integer>();
		TotalPoints = 0;
		FinishedIntroduction = false;
		return true;
	}

	@Override
	public void postReadInitialization()
	{
		if (CurrentStages == null)
		{
			CurrentStages = new HashMap<String, Integer>();
		}

		if (BestScores == null)
		{
			BestScores = new HashMap<String, Integer>();
		}
	}

}
