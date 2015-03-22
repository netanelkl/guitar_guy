package com.mad.tunerapp.app;

import android.content.Context;

import com.mad.lib.app.IServiceProviderRegistrar;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.lib.utils.bitmaps.BitmapsStore;
import com.mad.tunerlib.activities.TunerActivity;
import com.mad.tunerlib.utils.Bitmaps.TunerActivityBitmapStore;

public class TunerServiceProviderRegistrar implements
		IServiceProviderRegistrar
{

	@Override
	public AppLibraryServiceProvider build(	AppLibraryServiceProvider provider,
											Context context)
	{
		BitmapsStore.register(TunerActivity.class,
				new TunerActivityBitmapStore());
		return provider;
	}

}
