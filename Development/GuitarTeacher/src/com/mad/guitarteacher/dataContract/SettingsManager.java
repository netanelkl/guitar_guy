package com.mad.guitarteacher.dataContract;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * This class is responsible to handle all data that can be modified through
 * settings.
 * 
 * @author Tom
 * 
 */
public class SettingsManager
{
	// The current context.
	SharedPreferences	m_sharedPreferences;

	final String		FACEBOOK_SESSION	= "facebook_session";

	/**
	 * Create a new instance of the SettingsManager class.
	 * 
	 * @param context
	 *            - The current context.
	 */
	public SettingsManager(Context context)
	{
		m_sharedPreferences =
				context.getSharedPreferences(
						"shared_preferences", 0);
	}

	/**
	 * Logs the user out.
	 */
	public void logout()
	{
		Editor editor = m_sharedPreferences.edit();
		editor.putString(FACEBOOK_SESSION, "");
		editor.apply();
	}
}
