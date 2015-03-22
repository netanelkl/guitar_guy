package com.mad.lib.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.mad.lib.R;
import com.mad.lib.app.ApplicationBase;
import com.mad.lib.display.graphics.GraphicsPoint;
import com.mad.lib.display.utils.DisplayHelper;
import com.mad.lib.utils.ErrorHandler;

/**
 * This is the base activity for all of our activities.
 * 
 * It will make sure that all of our services are initialized.
 * 
 * @author Nati
 * 
 */
public abstract class ActivityBase extends Activity
{
	protected static Drawable	s_imgBackground			= null;

	/**
	 * A static isInit var stating whether or not the service provider has been
	 * initialized.
	 */
	protected static boolean	s_fIsManagerInitialized	= false;

	protected abstract View createRootView(final ViewGroup parent);

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Make sure the screen is full screen.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setWindowAnimations(android.R.anim.fade_in);
		// Initialize the service provider.
		if (!s_fIsManagerInitialized)
		{
			updateGraphicsDimensions();

			ApplicationBase app =
					(ApplicationBase) getApplication();
			if (app.initServiceProvider(this))
			{
				s_fIsManagerInitialized = true;
			}
			else
			{
				ErrorHandler
						.HandleError(new ExceptionInInitializerError());
				// TODO: Display on the screen an error info.
			}
			if (s_imgBackground == null)
			{
				s_imgBackground =
						new BitmapDrawable(	getResources(),
											DisplayHelper
													.getScaledBitmap(
															getResources(),
															R.drawable.general_bg));
			}

		}

	}

	protected void updateGraphicsDimensions()
	{
		Point pntScreenSize = new Point();
		getWindowManager().getDefaultDisplay().getSize(
				pntScreenSize);
		if ((getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT))
		{
			int x = pntScreenSize.x;
			pntScreenSize.x = pntScreenSize.y;
			pntScreenSize.y = x;
		}
		GraphicsPoint.setScalePoint(
				DisplayHelper.ORIGINAL_SCREEN_WIDTH
						/ pntScreenSize.x,
				DisplayHelper.ORIGINAL_SCREEN_HEIGHT
						/ pntScreenSize.y);
		GraphicsPoint.setScreenDimensions(pntScreenSize.x,
				pntScreenSize.y);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		updateGraphicsDimensions();
	}
}
