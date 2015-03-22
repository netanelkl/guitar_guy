package com.mad.guitarteacher.activities;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.widget.ProfilePictureView;
import com.mad.guitarteacher.R;
import com.mad.guitarteacher.activities.Listeners.ReversibleButtonTouchListener;
import com.mad.guitarteacher.connect.UserProfileConnectorBase;
import com.mad.guitarteacher.connect.UserProfilesManager;
import com.mad.guitarteacher.display.views.TotalPointsView;
import com.mad.guitarteacher.services.TotalPointsManager;
import com.mad.guitarteacher.utils.Definitions;
import com.mad.lib.activities.LayoutActivityBase;
import com.mad.lib.activities.listeners.OnFadeOutDoneListener;
import com.mad.lib.display.graphics.GraphicsPoint;
import com.mad.lib.display.utils.DisplayHelper;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.lib.utils.BitmapsLoader;
import com.mad.lib.utils.bitmaps.BitmapsStore;
import com.mad.metronomelib.activities.MetronomeActivity;
import com.mad.tunerlib.activities.TunerActivity;

/**
 * Our main activity. Displays the main menu.
 * 
 * @author Nati
 * 
 */
public class MainActivity extends LayoutActivityBase
{
	private static final String			TAG				=
																"PROFILING";
	ImageView							m_btnExercises;
	ImageView							m_btnStart;
	ImageView							m_btnTuner;
	ImageView							m_btnMetronome;
	ImageView							m_btnSettings;
	View								m_ClickedButton;
	List<ImageView>						m_arViewsToScale;
	int[]								arViewsIds;
	List<Class<? extends Activity>>		m_arClasses;
	private final OnFadeOutDoneListener	m_onFadeOutDone	=
																new MyOnFadeOutDoneListener();
	private boolean						m_fAllowClicks	= false;

	private class MyOnFadeOutDoneListener implements
			OnFadeOutDoneListener
	{

		@Override
		public void onFadeOutDone()
		{
			View view = m_ClickedButton;
			Intent intent =
					new Intent(MainActivity.this, m_arClasses
							.get(m_arViewsToScale.indexOf(view)));
			if (view == m_btnStart)
			{
				intent.putExtra(
						Definitions.Intents.INTENT_PLAN_ACTION,
						Definitions.Intents.PLAN_ACTION_GENERAL_PLAN_START);
			}

			// Unload the bitmaps for all of the other pages.
			for (int i = 1; i < m_arViewsToScale.size(); i++)
			{
				if (view != m_arViewsToScale.get(i))
				{
					BitmapsStore.ofType(m_arClasses.get(i))
							.unloadBitmaps(getResources());
				}
			}
			MainActivity.this.startActivity(intent);
		}
	};

	private ProfilePictureView	m_viewFacebookProfilePicture;
	private ImageView			m_imgBgGuitar;
	private TotalPointsView		viewPoints;
	protected ImageView			m_viewProfilePicture;

	@Override
	protected View createRootView(final ViewGroup parent)
	{
		return LayoutInflater.from(this).inflate(
				R.layout.main_activity, null);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		Log.d(TAG, "Before onCreate "
				+ Long.toString(System.nanoTime()));
		long currTime = System.nanoTime();
		super.onCreate(savedInstanceState);
		if (!s_fIsManagerInitialized)
		{
			return;
		}
		Log.d(TAG, "after layout inflation "
				+ Long.toString(System.nanoTime()));
		m_imgBgGuitar =
				(ImageView) findViewById(R.id.background);
		viewPoints = (TotalPointsView) findViewById(R.id.points);

		TotalPointsManager pointsManager =
				AppLibraryServiceProvider.getInstance().get(
						R.service.total_points_manager);
		viewPoints
				.setTotalPoints(pointsManager.getTotalPoints());
		initProfilePicture();

		initButtons();
		Log.d(TAG,
				Long.toString((System.nanoTime() - currTime) / 1000000)
						+ "ms");

		Log.d(TAG, "After onCreate "
				+ Long.toString(System.nanoTime()));

		BitmapsStore.ofType(GameActivity.class).preloadBitmaps(
				getResources());
	}

	@Override
	public void onBackPressed()
	{
		if (m_fAllowClicks)
		{
			m_fAllowClicks = false;
			super.onBackPressed();
		}
	}

	private void initButtons()
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{

				m_btnExercises =
						(ImageView) findViewById(R.id.btn_exercises);
				m_btnStart =
						(ImageView) findViewById(R.id.btn_start);
				m_btnTuner =
						(ImageView) findViewById(R.id.btn_tuner);
				m_btnMetronome =
						(ImageView) findViewById(R.id.btn_metronome);
				m_btnSettings =
						(ImageView) findViewById(R.id.btn_settings);
				m_arViewsToScale =
						new ArrayList<ImageView>(Arrays.asList(
								m_imgBgGuitar, m_btnExercises,
								m_btnStart, m_btnTuner,
								m_btnMetronome, m_btnSettings));
				arViewsIds =
						new int[] { R.drawable.main_bg_guitar,
								R.drawable.main_exercises_btn,
								R.drawable.main_start_btn,
								R.drawable.main_tuner_btn,
								R.drawable.main_metronome_btn,
								R.drawable.main_settings_btn };
				m_arClasses =
						new ArrayList<Class<? extends Activity>>(Arrays
								.asList(null,
										ExercisePickerActivity.class,
										ExercisePlanActivity.class,
										TunerActivity.class,
										MetronomeActivity.class,
										SettingsActivity.class));
				runOnUiThread(new Runnable()
				{

					@Override
					public void run()
					{
						// Get the associated bitmaps store.
						BitmapsStore store =
								BitmapsStore
										.ofType(MainActivity.this
												.getClass());
						BitmapsLoader loader =
								AppLibraryServiceProvider
										.getInstance()
										.get(R.service.bitmaps_loader);
						// Get the ids of bitmaps preloaded.
						// It correlates to the list in arViewsToScale.
						// Doesnt sound like a good design, does it?
						SparseBooleanArray arIds =
								store.getIds();

						// We have some work to do on all of the images. First
						// of all,
						// scale the
						// image to fit the screen. Then add click listeners and
						// animations.
						for (int i = 0; i < m_arViewsToScale
								.size(); i++)
						{
							View curView =
									m_arViewsToScale.get(i);
							// Scale the images to fit the screen.
							final ImageView view =
									(ImageView) curView;
							view.setImageBitmap(loader
									.getBitmap(
											MainActivity.this
													.getResources(),
											arViewsIds[i],
											arIds.get(arViewsIds[i])));
							DisplayHelper.scaleMargin(view);

							if (view != m_imgBgGuitar)
							{
								view.setOnTouchListener(new ReversibleButtonTouchListener(	MainActivity.this,
																							R.anim.scale_down)
								{

									@Override
									protected void onClick(final View view)
									{
										if (m_fAllowClicks)
										{
											m_fAllowClicks =
													false;
											m_ClickedButton =
													view;
											fadeOut(m_onFadeOutDone);
										}
									}

								});
							}
						}

						// We can release the static reference to our bitmaps
						// now.
						store.unloadBitmaps(getResources());

					}

				});
			}
		}).start();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		m_fAllowClicks = true;
	}

	private void initProfilePicture()
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{

				m_viewFacebookProfilePicture =
						(ProfilePictureView) findViewById(R.id.facebook_profile_picture);
				m_viewProfilePicture =
						(ImageView) findViewById(R.id.profile_picture);

				UserProfilesManager manager =
						AppLibraryServiceProvider
								.getInstance()
								.get(R.service.user_profiles_manager);
				final UserProfileConnectorBase activeSession =
						manager.getActiveSession(UserProfilesManager.ANY_READY_SESSION);
				if ((activeSession.getSessionType() != UserProfilesManager.INTERNAL_SESSION))
				{
					runOnUiThread(new Runnable()
					{

						@Override
						public void run()
						{
							loadProfilePicture(activeSession);
						}
					});
				}

			}
		}).start();
	}

	/**
	 * Makes a profile picture out of the view
	 * 
	 * @param view
	 * @param bmpPicture
	 */
	private Bitmap prepareProfilePicture(Bitmap bmpPicture)
	{

		Bitmap bmpCropped = null;
		if (bmpPicture != null)
		{
			bmpCropped =
					DisplayHelper.getCircleMaskedBitmap(
							bmpPicture, bmpPicture.getWidth());
		}

		return bmpCropped;
	}

	private void loadProfilePicture(UserProfileConnectorBase session)
	{
		GraphicsPoint dimension =
				Definitions.Dimensions.MainActivity.PROFILE_PICTURE;
		DisplayHelper.scaleMargin(m_viewFacebookProfilePicture);
		DisplayHelper.scaleMeasuredView(
				m_viewFacebookProfilePicture, false,
				(int) dimension.y, (int) dimension.x);
		DisplayHelper.scaleMargin(m_viewProfilePicture);
		DisplayHelper.scaleMeasuredView(m_viewProfilePicture,
				false, (int) dimension.y, (int) dimension.x);

		if (session.getSessionType() == UserProfilesManager.FACEBOOK_SESSON)
		{
			m_viewFacebookProfilePicture
					.setVisibility(View.VISIBLE);
			m_viewFacebookProfilePicture.setProfileId(session
					.getUserID());

			m_viewFacebookProfilePicture
					.setDefaultProfilePicture(null);
			m_viewFacebookProfilePicture
					.setOnImageChangedEventListener(new ProfilePictureView.OnImageChangedEventListener()
					{
						@Override
						public Bitmap onImageChangedEventListener(Bitmap bmp)
						{
							// m_viewFacebookProfilePicture
							// .setOnImageChangedEventListener(null);

							return prepareProfilePicture(bmp);
						}
					});
		}
		else
		{
			new DownloadImageTask(m_viewProfilePicture)
					.execute(session.getProfilePictureURL());
		}
	}

	@Override
	public void onActivityResult(	int requestCode,
									int resultCode,
									Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		UserProfilesManager manager =
				AppLibraryServiceProvider.getInstance().get(
						R.service.user_profiles_manager);
		manager.onActivityResult(this, requestCode, resultCode,
				data);
	}

	class DownloadImageTask extends
			AsyncTask<String, Void, Bitmap>
	{
		ImageView	bmImage;

		DownloadImageTask(ImageView bmImage)
		{
			this.bmImage = bmImage;
		}

		@Override
		protected Bitmap doInBackground(String... urls)
		{
			String urldisplay = urls[0];
			Bitmap bmpImage = null;
			InputStream in = null;
			try
			{
				in = new java.net.URL(urldisplay).openStream();
				bmpImage = BitmapFactory.decodeStream(in);
			}
			catch (Exception e)
			{
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if (in != null)
					{
						in.close();
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			return bmpImage;
		}

		@Override
		protected void onPostExecute(Bitmap result)
		{
			result = prepareProfilePicture(result);
			bmImage.setImageBitmap(result);
			bmImage.setVisibility(View.VISIBLE);
		}
	}
}
