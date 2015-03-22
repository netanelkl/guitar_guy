package com.mad.guitarteacher;

import android.content.Context;

import com.mad.guitarteacher.app.GuitarTeacherServiceProviderRegistrar;
import com.mad.lib.app.ApplicationBase;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.metronomelib.app.MetronomeServiceProviderRegistrar;
import com.mad.tunerlib.app.TunerServiceProviderRegistrar;

public class GuitarTeacherApplication extends ApplicationBase
{
	@Override
	public boolean isSupportingLogin()
	{
		return true;
	}

	@Override
	public boolean initServiceProvider(Context context)
	{
		AppLibraryServiceProvider.getInstance();
		super.initServiceProvider(context);
		AppLibraryServiceProvider provider =
				AppLibraryServiceProvider.getInstance();
		new TunerServiceProviderRegistrar().build(provider,
				context);
		new MetronomeServiceProviderRegistrar().build(provider,
				context);
		new GuitarTeacherServiceProviderRegistrar().build(
				provider, context);
		return true;
	}
}
