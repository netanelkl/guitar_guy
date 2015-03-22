package com.mad.guitarteacher.connect;

import java.io.FileNotFoundException;

import android.content.Context;

import com.mad.guitarteacher.dataContract.GeneralUserInformationFile;
import com.mad.guitarteacher.dataContract.UserHistoryInformationFile;
import com.mad.guitarteacher.utils.Services.JSON.JSONSerializer;
import com.mad.lib.utils.ErrorHandler;

public class UserProfile
{
	private final UserHistoryInformationFile	m_UserHistory;
	private final GeneralUserInformationFile	m_UserInformation;
	private final UserProfileConnectorBase		m_Connector;

	UserProfile(Context context,
				UserProfileConnectorBase connector)
	{
		// Let's build up the path:
		String strName = connector.getSessionTypeName();
		String strId = connector.getUserID();
		String strRelativePath = strName + "/" + strId + "/";
		m_UserInformation =
				new GeneralUserInformationFile(strRelativePath);
		m_UserHistory =
				new UserHistoryInformationFile(strRelativePath);
		m_Connector = connector;
		JSONSerializer deserializer = new JSONSerializer();
		// Read the stages map.
		if ((!deserializer.read(context, m_UserInformation))
				|| (!deserializer.read(context, m_UserHistory)))
		{
			ErrorHandler
					.HandleError(new FileNotFoundException());
		}

	}

	/**
	 * Gets the user's information
	 * 
	 * TODO: Make that somehow immutable.
	 * 
	 * @return
	 */
	public GeneralUserInformationFile getUserInformation()
	{
		return m_UserInformation;
	}

	public UserHistoryInformationFile getUserHistory()
	{
		return m_UserHistory;
	}

	public UserProfileConnectorBase getConnector()
	{
		return m_Connector;
	}

}
