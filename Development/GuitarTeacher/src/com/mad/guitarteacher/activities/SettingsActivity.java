package com.mad.guitarteacher.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mad.guitarteacher.R;
import com.mad.guitarteacher.connect.UserProfileConnectorBase;
import com.mad.guitarteacher.connect.UserProfilesManager;
import com.mad.guitarteacher.practice.ExercisesManager;
import com.mad.lib.activities.LayoutActivityBase;
import com.mad.lib.display.pager.PagerPageCollection;
import com.mad.lib.display.pager.PopupInformationDisplayer;
import com.mad.lib.utils.AppLibraryServiceProvider;

/**
 * This activity manages the settings screen.
 * 
 * @author Tom
 * 
 */
public class SettingsActivity extends LayoutActivityBase
{
	// The settings attributes manager.
	PopupInformationDisplayer	m_displayer;
	PagerPageCollection			m_info;
	private boolean				m_fIsAboutShown	= false;
	private TextView			m_txtAccount;
	private Button				m_btnLogout;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (!s_fIsManagerInitialized)
		{
			return;
		}
	}

	/**
	 * Sets the logout button function.
	 */
	protected void setLogoutButton(View root)
	{
		m_btnLogout =
				(Button) root.findViewById(R.id.logout_btn);

		m_btnLogout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				logout();
			}
		});
	}

	/**
	 * Sets the about button function.
	 */
	protected void setAboutButton(View root)
	{
		Button aboutButton =
				(Button) root
						.findViewById(R.id.settings_about_btn);

		aboutButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showAbout();
			}
		});
	}

	/**
	 * This function shows the about window.
	 */
	private void showAbout()
	{
		m_displayer.setPagerInfo(m_info);
		m_displayer.show();
		m_fIsAboutShown = true;
	}

	private void hideAbout()
	{
		m_displayer.hide();
		m_fIsAboutShown = false;
	}

	@Override
	public void onBackPressed()
	{
		if (!m_fIsAboutShown)
		{
			super.onBackPressed();
		}
		else
		{
			hideAbout();
		}
	}

	@Override
	protected View createRootView(ViewGroup parent)
	{
		View rootView =
				LayoutInflater.from(this).inflate(
						R.layout.settings_activity, null);

		m_displayer =
				new PopupInformationDisplayer(this, rootView);

		m_info = new PagerPageCollection();
		m_txtAccount =
				(TextView) rootView.findViewById(R.id.logout);

		setLogoutButton(rootView);
		setAboutButton(rootView);

		ExercisesManager exercisesManager =
				AppLibraryServiceProvider.getInstance().get(
						R.service.exercises_manager);

		UserProfileConnectorBase connector =
				exercisesManager.getActiveProfile()
						.getConnector();

		if (connector.getSessionType() == UserProfilesManager.INTERNAL_SESSION)
		{
			hideLogout();
		}
		else
		{
			m_txtAccount.setText(connector.getSessionTypeName());
		}
		String title = getString(R.string.settings_about_title);
		String content =
				getString(R.string.settings_about_content);
		m_info.addPage(title, content);
		m_info.setIsPrePlay(false);
		return rootView;
	}

	private void hideLogout()
	{
		m_txtAccount.setVisibility(View.GONE);
		m_btnLogout.setVisibility(View.GONE);
	}

	/**
	 * This function logs out the user.
	 */
	private void logout()
	{
		ExercisesManager exercisesManager =
				AppLibraryServiceProvider.getInstance().get(
						R.service.exercises_manager);
		exercisesManager.getActiveProfile().getConnector()
				.logout();
		hideLogout();
	}
}
