package com.mad.guitarteacher.connect;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.mad.lib.R;
import com.mad.lib.utils.AppLibraryServiceProvider;

/**
 * An active facebook session.
 * 
 * @author Tom
 * 
 */
public class FacebookProfileConnector extends
		UserProfileConnectorBase
{
	private static final String	FACEBOOK	= "Facebook";
	// Instance of the user.
	private GraphUser			m_user;

	/**
	 * Publish a facebook story.
	 * 
	 * @param activity
	 * @param title
	 * @param caption
	 * @param description
	 * @param link
	 * @param picture
	 */
	public void facebookFeedDialog(	final Activity activity,
									String title,
									String caption,
									String description,
									String link,
									String picture)
	{
		// Set the dialog parameters
		Bundle params = new Bundle();
		params.putString("name", title);
		params.putString("caption", caption);
		params.putString("description", description);
		params.putString("link", link);
		params.putString("picture", picture);

		// Invoke the dialog
		WebDialog feedDialog =
				(new WebDialog.FeedDialogBuilder(	activity,
													Session.getActiveSession(),
													params))
						.setOnCompleteListener(
								new OnCompleteListener()
								{
									@Override
									public void onComplete(	Bundle values,
															FacebookException error)
									{
										if (error == null)
										{
											// When the story is posted, echo
											// the success
											// and the post Id.
											final String postId =
													values.getString("post_id");
											if (postId != null)
											{
												Toast.makeText(
														activity,
														"Story published: "
																+ postId,
														Toast.LENGTH_SHORT)
														.show();
											}
										}
									}

								}).build();
		feedDialog.show();
	}

	private final OnSessionOpen	m_OnSessionOpenListener;

	/**
	 * Matzov android developer facebook status callback.
	 * 
	 * @author Tom
	 * 
	 */
	class MadFacebookStatusCallback implements StatusCallback
	{

		@Override
		public synchronized void call(	Session session,
										SessionState state,
										Exception exception)
		{
			if (session.isOpened())
			{
				requestAsync("/me", new OnResult<GraphUser>()
				{

					@Override
					public void onResult(GraphUser result)
					{
						FacebookProfileConnector.this
								.setUser(result);

						if (m_OnSessionOpenListener != null)
						{
							m_OnSessionOpenListener
									.onSessionEstablished(FacebookProfileConnector.this);
						}
					}
				});

			}

		}

	}

	@Override
	public void sharePicture(	Activity activity,
								Bitmap bitmap,
								String title,
								String caption)
	{
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("image/jpeg");
		share.putExtra(Intent.EXTRA_TEXT, title);
		share.putExtra(Intent.EXTRA_TITLE, caption);

		ByteArrayOutputStream bytes =
				new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		File f =
				new File(Environment
						.getExternalStorageDirectory()
						+ File.separator + "temporary_file.jpg");
		FileOutputStream fo = null;
		try
		{
			f.createNewFile();
			fo = new FileOutputStream(f);
			fo.write(bytes.toByteArray());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (fo != null)
			{
				try
				{
					fo.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///"
				+ Environment.getExternalStorageDirectory()
						.getPath() + "/temporary_file.jpg"));

		activity.startActivity(Intent.createChooser(share,
				"Share Image"));
	}

	/**
	 * Create a new instance of the Facebook station object.
	 * 
	 * @param savedInstanceState
	 *            - The current saved instance state.
	 * @param activity
	 *            - The current running activity.
	 */
	public FacebookProfileConnector(Activity activity,
									OnSessionOpen listener)
	{
		m_OnSessionOpenListener = listener;
	}

	@Override
	public boolean connectBG(Activity activity)
	{
		// Settings.addLoggingBehavior(LoggingBehavior.DEVELOPER_ERRORS);
		// Settings.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
		// Try to get the active session.
		Session session = Session.getActiveSession();

		MadFacebookStatusCallback statusCallback =
				new MadFacebookStatusCallback();

		// If there is no active session.
		if (session == null)
		{
			if (session == null)
			{
				session = new Session(activity);
			}
		}
		// Set the active sesion.
		Session.setActiveSession(session);

		if (session.isOpened() || session.isClosed())
		{
			Session.openActiveSession(activity, false,
					statusCallback);
			return true;
		}

		return false;
	}

	@Override
	public void connect(Activity activity)
	{
		Session session = Session.getActiveSession();

		// TODO: I think we should validate the session here, but to what
		// extent?

		MadFacebookStatusCallback statusCallback =
				new MadFacebookStatusCallback();

		// Check if the session is already open.
		if (!session.isOpened() && !session.isClosed())
		{
			OpenRequest openRequest = new OpenRequest(activity);
			session.openForRead(openRequest
					.setCallback(statusCallback));
		}
		else
		{
			Session.openActiveSession(activity, true,
					statusCallback);
		}

	}

	@Override
	public String getUserName()
	{
		return m_user.getName();
	}

	@Override
	public String getUserID()
	{
		return m_user.getId();
	}

	/**
	 * Request to login async.
	 * 
	 * @param strPath
	 *            - Path of login.
	 * @param callback
	 *            - Call back function.
	 */
	public <T> void requestAsync(	String strPath,
									final OnResult<T> callback)
	{
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		Settings.addLoggingBehavior(LoggingBehavior.REQUESTS);
		Session session = Session.getActiveSession();
		Request request =
				Request.newGraphPathRequest(session, strPath,
						new Request.Callback()
						{
							@Override
							public void onCompleted(Response response)
							{
								if (response.getError() != null)
								{
									Log.i("MainActivity",
											String.format(
													"Error making request: %s",
													response.getError()));
								}
								else
								{
									GraphUser user =
											response.getGraphObjectAs(GraphUser.class);
									callback.onResult((T) user);
									Log.i("MainActivity",
											String.format(
													"Name: %s",
													user.getName()));
								}
							}
						});
		request.executeAsync();
	}

	public void setUser(GraphUser user)
	{
		m_user = user;
	}

	@Override
	public String getProfilePictureURL()
	{
		return "http://graph.facebook.com/" + getUserID()
				+ "/picture?type=small";
	}

	@Override
	public void onActivityResult(	Activity activity,
									int requestCode,
									int resultCode,
									Intent data)
	{
		Session.getActiveSession().onActivityResult(activity,
				requestCode, resultCode, data);
	}

	@Override
	public boolean isUserReady()
	{
		return m_user != null;
	}

	@Override
	public int getSessionType()
	{
		return UserProfilesManager.FACEBOOK_SESSON;
	}

	@Override
	public String getSessionTypeName()
	{
		return FACEBOOK;
	}

	@Override
	public void logout()
	{
		Session.getActiveSession()
				.closeAndClearTokenInformation();
		UserProfilesManager manager =
				AppLibraryServiceProvider.getInstance().get(
						R.service.user_profiles_manager);
		UserProfileConnectorBase connector =
				manager.getActiveSession(UserProfilesManager.INTERNAL_SESSION);
		connector.connect(null);
	}
}
