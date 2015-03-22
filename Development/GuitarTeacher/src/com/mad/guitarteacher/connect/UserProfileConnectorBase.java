package com.mad.guitarteacher.connect;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

/**
 * A class representing the current active session (corresponding with a social
 * network web site).
 * 
 * @author Tom
 * 
 */
public abstract class UserProfileConnectorBase
{

	public interface OnSessionOpen
	{
		void onSessionEstablished(UserProfileConnectorBase session);

		void onConnectionFailed(UserProfileConnectorBase session);
	}

	public abstract void logout();

	public abstract boolean isUserReady();

	public abstract int getSessionType();

	public abstract String getSessionTypeName();

	/**
	 * Tries to connect to a social network. Doesn't trigger any activities.
	 * 
	 * @param activity
	 *            - Current activity.
	 * 
	 * @return true if successful.
	 */
	public boolean connectBG(Activity activity)
	{
		return false;
	}

	public abstract void connect(Activity activity);

	/**
	 * Get the current session user name.
	 * 
	 * @return Current active user name.
	 */
	public abstract String getUserName();

	/**
	 * Get the current session user ID.
	 * 
	 * @return Current active user ID.
	 */
	public abstract String getUserID();

	/**
	 * Get the current session user ID.
	 * 
	 * @return Current active user ID.
	 */
	public abstract String getProfilePictureURL();

	/**
	 * Share a picture.
	 * 
	 * @param activity
	 *            - current activity.
	 * @param bitmap
	 *            - picture to share.
	 * @param title
	 *            - Title to present.
	 * @param caption
	 *            - Caption to present.
	 */
	public abstract void sharePicture(	Activity activity,
										Bitmap bitmap,
										String title,
										String caption);

	public abstract void onActivityResult(	Activity activity,
											int requestCode,
											int resultCode,
											Intent data);
}
