package com.mad.guitarteacher.connect;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import com.mad.lib.utils.ErrorHandler;

/**
 * An active facebook session.
 * 
 * @author Tom
 * 
 */
public class InternalProfileConnector extends
		UserProfileConnectorBase
{
	private static final String	USER_ID			= "1";

	private static final String	USER_NAME		= "User";

	private static final String	INTERNAL		= "Internal";

	private final OnSessionOpen	m_OnSessionOpenListener;

	private boolean				m_fIsconnected	= false;

	/**
	 * Create a new instance of the Facebook station object.
	 * 
	 * @param savedInstanceState
	 *            - The current saved instance state.
	 * @param activity
	 *            - The current running activity.
	 */
	public InternalProfileConnector(Activity activity,
									OnSessionOpen listener)
	{
		m_OnSessionOpenListener = listener;
	}

	@Override
	public void connect(Activity activity)
	{
		m_fIsconnected = true;

		if (m_OnSessionOpenListener != null)
		{
			m_OnSessionOpenListener.onSessionEstablished(this);
		}
	}

	@Override
	public String getUserName()
	{
		return USER_NAME;
	}

	@Override
	public String getUserID()
	{
		return USER_ID;
	}

	@Override
	public String getProfilePictureURL()
	{
		return "";
	}

	@Override
	public void onActivityResult(	Activity activity,
									int requestCode,
									int resultCode,
									Intent data)
	{
	}

	@Override
	public boolean isUserReady()
	{
		return m_fIsconnected;
	}

	@Override
	public int getSessionType()
	{
		return UserProfilesManager.INTERNAL_SESSION;
	}

	@Override
	public String getSessionTypeName()
	{
		return INTERNAL;
	}

	@Override
	public void sharePicture(	Activity activity,
								Bitmap bitmap,
								String title,
								String caption)
	{
		return;
	}

	@Override
	public void logout()
	{
		ErrorHandler
				.HandleError(new UnsupportedOperationException());
	}
}
