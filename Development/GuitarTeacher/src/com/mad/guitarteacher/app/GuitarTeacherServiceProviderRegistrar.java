package com.mad.guitarteacher.app;

import android.content.Context;

import com.mad.guitarteacher.activities.ExercisePickerActivity;
import com.mad.guitarteacher.activities.ExercisePlanActivity;
import com.mad.guitarteacher.activities.GameActivity;
import com.mad.guitarteacher.activities.MainActivity;
import com.mad.guitarteacher.activities.SettingsActivity;
import com.mad.guitarteacher.connect.UserProfile;
import com.mad.guitarteacher.connect.UserProfilesManager;
import com.mad.guitarteacher.dataContract.SettingsManager;
import com.mad.guitarteacher.practice.ExercisesManager;
import com.mad.guitarteacher.services.TotalPointsManager;
import com.mad.guitarteacher.utils.Bitmaps.ExercisePickerBitmapStore;
import com.mad.guitarteacher.utils.Bitmaps.ExercisePlanActivityBitmapStore;
import com.mad.guitarteacher.utils.Bitmaps.GameActivityBitmapStore;
import com.mad.guitarteacher.utils.Bitmaps.MainActivityBitmapsStore;
import com.mad.guitarteacher.utils.Bitmaps.SettingsActivityBitmapStore;
import com.mad.lib.R;
import com.mad.lib.app.IServiceProviderRegistrar;
import com.mad.lib.musicalBase.keyConverters.KeyConverterFactory;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.lib.utils.BitmapsLoader;
import com.mad.lib.utils.ErrorHandler;
import com.mad.lib.utils.bitmaps.BitmapsStore;
import com.mad.tunerlib.utils.GuitarNotePlayer;

public class GuitarTeacherServiceProviderRegistrar implements
		IServiceProviderRegistrar
{

	@Override
	public AppLibraryServiceProvider build(	AppLibraryServiceProvider provider,
											Context context)
	{
		provider.register(R.service.settings_manager,
				new SettingsManager(context));
		provider.register(R.service.total_points_manager,
				new TotalPointsManager());
		provider.register(R.service.bitmaps_loader,
				new BitmapsLoader());
		provider.register(R.service.user_profiles_manager,
				new UserProfilesManager());
		provider.register(R.service.key_converter_factory,
				new KeyConverterFactory());
		provider.register(R.service.exercises_manager,
				new GuitarNotePlayer(context));
		BitmapsStore.register(MainActivity.class,
				new MainActivityBitmapsStore());
		BitmapsStore.register(GameActivity.class,
				new GameActivityBitmapStore());
		BitmapsStore.register(ExercisePickerActivity.class,
				new ExercisePickerBitmapStore());
		BitmapsStore.register(ExercisePlanActivity.class,
				new ExercisePlanActivityBitmapStore());

		BitmapsStore.register(SettingsActivity.class,
				new SettingsActivityBitmapStore());
		return provider;
	}

	public static boolean initManager(	final AppLibraryServiceProvider provider,
										final Context context,
										UserProfile profile)
	{
		try
		{
			provider.register(R.service.exercises_manager,
					new ExercisesManager(context, profile));

			// This just seems like a suitable place to init the
			// guitarNotePlayer.
			new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					provider.register(
							R.service.guitar_note_player,
							new GuitarNotePlayer(context));
				}
			}).start();
		}
		catch (Exception ex)
		{
			ErrorHandler.HandleError(ex);
			return false;
		}

		return true;
	}
}
