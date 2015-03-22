package com.mad.lib.app;

import android.content.Context;

import com.mad.lib.utils.AppLibraryServiceProvider;

public interface IServiceProviderRegistrar
{
	public AppLibraryServiceProvider build(	AppLibraryServiceProvider provider,
											Context context);
}
