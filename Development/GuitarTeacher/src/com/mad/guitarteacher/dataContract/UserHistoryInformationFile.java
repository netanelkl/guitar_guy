package com.mad.guitarteacher.dataContract;

import java.util.ArrayList;

import com.mad.guitarteacher.practice.ExerciseDoneReport;
import com.mad.guitarteacher.utils.Definitions;
import com.mad.guitarteacher.utils.Services.JSON.StorableObject;

/**
 * Represents list of reports stored for the given user.
 * 
 * @author Nati
 * 
 */
public class UserHistoryInformationFile extends StorableObject
{
	private final String	strRelativePath;

	public UserHistoryInformationFile(String relativePath)
	{
		strRelativePath = relativePath;
	}

	/**
	 * A list of all reports
	 */
	public ArrayList<ExerciseDoneReport>	Reports;

	@Override
	public String getStorableFilename()
	{
		return strRelativePath
				+ Definitions.STORABLE_FILE_USER_HISTORY_INFORMATION;
	}

	@Override
	public boolean isStoredOnAssets()
	{
		return false;
	}

	@Override
	public boolean setDefaultInformation()
	{
		Reports = new ArrayList<ExerciseDoneReport>();
		return true;
	}

	@Override
	public void postReadInitialization()
	{
		if (Reports == null)
		{
			Reports = new ArrayList<ExerciseDoneReport>();
		}
	}

}
