package com.mad.metronomelib.app;

import android.content.Context;

import com.mad.lib.app.IServiceProviderRegistrar;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.lib.utils.bitmaps.BitmapsStore;
import com.mad.metronomelib.activities.MetronomeActivity;
import com.mad.metronomelib.utils.Bitmaps.MetronomeActivityBitmapStore;

public class MetronomeServiceProviderRegistrar implements
		IServiceProviderRegistrar
{

	@Override
	public AppLibraryServiceProvider build(	AppLibraryServiceProvider provider,
											Context context)
	{
		BitmapsStore.register(MetronomeActivity.class,
				new MetronomeActivityBitmapStore());
		return provider;
	}
}
