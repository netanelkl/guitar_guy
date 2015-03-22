package com.mad.guitarteacher.connect;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.mad.lib.R;
import com.mad.lib.utils.AppLibraryServiceProvider;

/**
 * An active facebook session.
 * 
 * @author Tom
 * 
 */
public class GooglePlusProfileConnector extends
		UserProfileConnectorBase implements ConnectionCallbacks,
		OnConnectionFailedListener
{

	private static final String				GOOGLE						=
																				"Google";
	private static final String				TAG							=
																				"GooglePlusSession";
	private static final int				STATE_DEFAULT				=
																				0;
	private static final int				STATE_SIGN_IN				=
																				1;
	private static final int				STATE_IN_PROGRESS			=
																				2;
	private static final int				DIALOG_PLAY_SERVICES_ERROR	=
																				0;
	private static final int				RC_SIGN_IN					=
																				0;

	private final OnSessionOpen				m_OnSessionOpenListener;
	private final GoogleApiClient			m_GoogleApiClient;
	private Person							m_currentUser;
	private final WeakReference<Activity>	m_Activity;
	private int								mSignInProgress;
	private PendingIntent					mSignInIntent;

	// private int mSignInError;

	private GoogleApiClient buildGoogleApiClient(Context context)
	{
		// When we build the GoogleApiClient we specify where connected and
		// connection failed callbacks should be returned, which Google APIs our
		// app uses and which OAuth 2.0 scopes our app requests.
		return new GoogleApiClient.Builder(context)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(
						Plus.API, null).addScope(
						Plus.SCOPE_PLUS_LOGIN).build();
	}

	/**
	 * Create a new instance of the Facebook station object.
	 * 
	 * @param savedInstanceState
	 *            - The current saved instance state.
	 * @param activity
	 *            - The current running activity.
	 */
	public GooglePlusProfileConnector(	Activity activity,
										OnSessionOpen listener)
	{
		m_Activity = new WeakReference<Activity>(activity);
		m_OnSessionOpenListener = listener;
		m_GoogleApiClient = buildGoogleApiClient(activity);
	}

	@Override
	public boolean connectBG(Activity activity)
	{
		m_GoogleApiClient.connect();
		return m_GoogleApiClient.isConnected();
	}

	@Override
	public void connect(Activity activity)
	{
		if (m_GoogleApiClient.isConnected())
		{
			onConnected(null);
		}
		else
		{
			resolveSignInError();
		}
	}

	@Override
	public String getUserName()
	{
		return m_currentUser.getName().getFormatted();
	}

	@Override
	public String getUserID()
	{
		// return m_user.getId();
		return m_currentUser.getId();
	}

	@Override
	public void sharePicture(	Activity activity,
								Bitmap bitmap,
								String title,
								String caption)
	{

	}

	/*
	 * onConnected is called when our Activity successfully connects to Google
	 * Play services. onConnected indicates that an account was selected on the
	 * device, that the selected account has granted any requested permissions
	 * to our app and that we were able to establish a service connection to
	 * Google Play services.
	 */
	@Override
	public void onConnected(Bundle connectionHint)
	{
		// Retrieve some profile information to personalize our app for the
		// user.
		m_currentUser =
				Plus.PeopleApi
						.getCurrentPerson(m_GoogleApiClient);

		// Indicate that the sign in process is complete.
		mSignInProgress = STATE_DEFAULT;
		if (m_OnSessionOpenListener != null)
		{
			m_OnSessionOpenListener.onSessionEstablished(this);
		}
	}

	/*
	 * onConnectionFailed is called when our Activity could not connect to
	 * Google Play services. onConnectionFailed indicates that the user needs to
	 * select an account, grant permissions or resolve an error in order to sign
	 * in.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result)
	{
		// Refer to the javadoc for ConnectionResult to see what error codes
		// might
		// be returned in onConnectionFailed.
		Log.i(TAG,
				"onConnectionFailed: ConnectionResult.getErrorCode() = "
						+ result.getErrorCode());

		if (mSignInProgress != STATE_IN_PROGRESS)
		{
			// We do not have an intent in progress so we should store the
			// latest
			// error resolution intent for use when the sign in button is
			// clicked.
			mSignInIntent = result.getResolution();
			// mSignInError = result.getErrorCode();

			if (mSignInProgress == STATE_SIGN_IN)
			{
				// STATE_SIGN_IN indicates the user already clicked the sign in
				// button
				// so we should continue processing errors until the user is
				// signed in
				// or they click cancel.
				resolveSignInError();
			}
		}

		if (m_OnSessionOpenListener != null)
		{
			m_OnSessionOpenListener.onConnectionFailed(this);
		}
		// In this sample we consider the user signed out whenever they do not
		// have
		// a connection to Google Play services.
		// onSignedOut();
	}

	/*
	 * Starts an appropriate intent or dialog for user interaction to resolve
	 * the current error preventing the user from being signed in. This could be
	 * a dialog allowing the user to select an account, an activity allowing the
	 * user to consent to the permissions being requested by your app, a setting
	 * to enable device networking, etc.
	 */
	private void resolveSignInError()
	{
		if (mSignInIntent != null)
		{
			// We have an intent which will allow our user to sign in or
			// resolve an error. For example if the user needs to
			// select an account to sign in with, or if they need to consent
			// to the permissions your app is requesting.

			try
			{
				// Send the pending intent that we stored on the most recent
				// OnConnectionFailed callback. This will allow the user to
				// resolve the error currently preventing our connection to
				// Google Play services.
				mSignInProgress = STATE_IN_PROGRESS;
				m_Activity.get().startIntentSenderForResult(
						mSignInIntent.getIntentSender(),
						RC_SIGN_IN, null, 0, 0, 0);
			}
			catch (SendIntentException e)
			{
				Log.i(TAG, "Sign in intent could not be sent: "
						+ e.getLocalizedMessage());
				// The intent was canceled before it was sent. Attempt to
				// connect to
				// get an updated ConnectionResult.
				mSignInProgress = STATE_SIGN_IN;
				m_GoogleApiClient.connect();
			}
		}
		else
		{
			// Google Play services wasn't able to provide an intent for some
			// error types, so we show the default Google Play services error
			// dialog which may still start an intent on our behalf if the
			// user can resolve the issue.
			m_Activity.get().showDialog(
					DIALOG_PLAY_SERVICES_ERROR);
		}
	}

	@Override
	public void onConnectionSuspended(int cause)
	{

	}

	@Override
	public String getProfilePictureURL()
	{
		return m_currentUser.getImage().getUrl();
	}

	@Override
	public void onActivityResult(	Activity activity,
									int requestCode,
									int resultCode,
									Intent data)
	{
		switch (requestCode)
		{
			case RC_SIGN_IN:
				if (resultCode == Activity.RESULT_OK)
				{
					// If the error resolution was successful we should
					// continue
					// processing errors.
					mSignInProgress = STATE_SIGN_IN;
				}
				else
				{
					// If the error resolution was not successful or the
					// user
					// canceled,
					// we should stop processing errors.
					mSignInProgress = STATE_DEFAULT;
				}

				if (!m_GoogleApiClient.isConnecting())
				{
					// If Google Play services resolved the issue with a
					// dialog
					// then
					// onStart is not called so we need to re-attempt
					// connection
					// here.
					m_GoogleApiClient.connect();
				}
				break;
		}

	}

	@Override
	public boolean isUserReady()
	{
		return m_currentUser != null;
	}

	@Override
	public int getSessionType()
	{
		return UserProfilesManager.GOOGLE_SESSON;
	}

	@Override
	public String getSessionTypeName()
	{
		return GOOGLE;
	}

	@Override
	public void logout()
	{
		Plus.AccountApi.clearDefaultAccount(m_GoogleApiClient);
		Plus.AccountApi
				.revokeAccessAndDisconnect(m_GoogleApiClient);
		UserProfilesManager manager =
				AppLibraryServiceProvider.getInstance().get(
						R.service.user_profiles_manager);
		UserProfileConnectorBase connector =
				manager.getActiveSession(UserProfilesManager.INTERNAL_SESSION);
		connector.connect(null);
	}
}
