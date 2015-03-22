package com.mad.tunerapp.app;

import android.content.Context;

import com.mad.lib.app.ApplicationBase;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.tunerlib.app.TunerServiceProviderRegistrar;

public class TunerApplication extends ApplicationBase
{

	@Override
	public boolean initServiceProvider(Context context)
	{
		AppLibraryServiceProvider.getInstance();
		super.initServiceProvider(context);
		AppLibraryServiceProvider provider =
				AppLibraryServiceProvider.getInstance();
		new TunerServiceProviderRegistrar().build(provider,
				context);
		return true;
	}
}
