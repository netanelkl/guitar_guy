package com.mad.metronomeapp.app;

import android.content.Context;

import com.mad.lib.app.ApplicationBase;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.metronomelib.app.MetronomeServiceProviderRegistrar;

public class MetronomeApplication extends ApplicationBase
{

	@Override
	public boolean initServiceProvider(Context context)
	{
		AppLibraryServiceProvider.getInstance();
		super.initServiceProvider(context);
		AppLibraryServiceProvider provider =
				AppLibraryServiceProvider.getInstance();
		new MetronomeServiceProviderRegistrar().build(provider,
				context);
		return true;
	}
}
