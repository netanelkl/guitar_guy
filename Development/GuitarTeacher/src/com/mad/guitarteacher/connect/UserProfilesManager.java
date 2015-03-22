package com.mad.guitarteacher.connect;

import java.lang.ref.WeakReference;
import java.security.InvalidParameterException;

import android.app.Activity;
import android.content.Intent;
import android.util.SparseArray;

import com.mad.guitarteacher.app.GuitarTeacherServiceProviderRegistrar;
import com.mad.guitarteacher.connect.UserProfileConnectorBase.OnSessionOpen;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.lib.utils.ErrorHandler;

public class UserProfilesManager
{
	public static final int					FACEBOOK_SESSON					=
																					0;
	public static final int					GOOGLE_SESSON					=
																					1;
	public static final int					INTERNAL_SESSION				=
																					2;
	public static final int					ANY_READY_SESSION				=
																					3;
	SparseArray<UserProfileConnectorBase>	m_Sessions						=
																					new SparseArray<UserProfileConnectorBase>(3);

	private WeakReference<Activity>			m_Activity;
	private OnSessionOpen					m_ExternalSessionOpenListener;
	private final OnSessionOpen				m_InternalSessionOpenListener	=
																					new OnSessionOpen()
																					{

																						@Override
																						public void onSessionEstablished(UserProfileConnectorBase session)
																						{
																							GuitarTeacherServiceProviderRegistrar
																									.initManager(
																											AppLibraryServiceProvider
																													.getInstance(),
																											m_Activity
																													.get(),
																											new UserProfile(m_Activity
																																	.get(),
																															session));

																							if (m_ExternalSessionOpenListener != null)
																							{
																								m_ExternalSessionOpenListener
																										.onSessionEstablished(session);
																							}
																						}

																						@Override
																						public void onConnectionFailed(UserProfileConnectorBase session)
																						{
																							if (m_ExternalSessionOpenListener != null)
																							{
																								m_ExternalSessionOpenListener
																										.onConnectionFailed(session);
																							}
																						}
																					};

	public boolean attemptBackgroundConnecting(	Activity activity,
												OnSessionOpen listener)
	{
		m_Activity = new WeakReference<Activity>(activity);
		m_ExternalSessionOpenListener = listener;
		UserProfileConnectorBase sessionGoogle =
				new GooglePlusProfileConnector(	activity,
												m_InternalSessionOpenListener);
		UserProfileConnectorBase sessionFacebook =
				new FacebookProfileConnector(	activity,
												m_InternalSessionOpenListener);
		UserProfileConnectorBase sessionInternal =
				new InternalProfileConnector(	activity,
												m_InternalSessionOpenListener);

		UserProfileConnectorBase[] arConnections =
				new UserProfileConnectorBase[] {
						sessionFacebook, sessionGoogle,
						sessionInternal };

		boolean fConnected = false;
		for (UserProfileConnectorBase currentSession : arConnections)
		{
			setSession(currentSession);
			if (!fConnected)
			{
				fConnected = currentSession.connectBG(activity);
			}
		}

		return fConnected;
	}

	public UserProfileConnectorBase getActiveSession(int nSessionSource)
	{
		switch (nSessionSource)
		{
			case FACEBOOK_SESSON:
			case GOOGLE_SESSON:
			case INTERNAL_SESSION:
			{
				UserProfileConnectorBase session =
						m_Sessions.get(nSessionSource);
				return session;
			}
			case ANY_READY_SESSION:
			{
				for (int i = 0; i < m_Sessions.size(); i++)
				{
					int key = m_Sessions.keyAt(i);
					UserProfileConnectorBase session =
							m_Sessions.get(key);
					if (session.isUserReady())
					{
						return session;
					}
				}
				return null;
			}
			default:
			{
				ErrorHandler
						.HandleError(new InvalidParameterException());
				return null;
			}
		}
	}

	public void setSession(UserProfileConnectorBase session)
	{
		switch (session.getSessionType())
		{
			case FACEBOOK_SESSON:
			case GOOGLE_SESSON:
			case INTERNAL_SESSION:
			{
				m_Sessions.append(session.getSessionType(),
						session);

				// if (session.isUserReady()
				// && (m_UserListener != null))
				// {
				// m_UserListener.onUserRetrieved(session);
				// }
				break;
			}
			default:
			{
				ErrorHandler
						.HandleError(new InvalidParameterException());
				break;
			}
		}
	}

	public void onActivityResult(	Activity activity,
									int requestCode,
									int resultCode,
									Intent data)
	{
		int key = 0;
		for (int i = 0; i < m_Sessions.size(); i++)
		{
			key = m_Sessions.keyAt(i);

			// get the object by the key.
			UserProfileConnectorBase current =
					m_Sessions.get(key);
			if (current == null)
			{
				continue;
			}

			current.onActivityResult(activity, requestCode,
					resultCode, data);
		}
	}
}
