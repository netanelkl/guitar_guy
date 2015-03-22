package com.mad.lib.utils;

import android.util.SparseArray;

public class AppLibraryServiceProvider
{
	protected static AppLibraryServiceProvider	s_Instance;
	private final SparseArray<Object>			m_Services	=
																	new SparseArray<Object>();

	public static AppLibraryServiceProvider getInstance()
	{
		if (s_Instance == null)
		{
			s_Instance = new AppLibraryServiceProvider();
		}
		return s_Instance;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(int nService)
	{
		return (T) m_Services.get(nService);
	}

	public void register(int nKey, Object service)
	{
		m_Services.append(nKey, service);
	}

}
