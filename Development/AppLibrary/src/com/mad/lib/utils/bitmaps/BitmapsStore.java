package com.mad.lib.utils.bitmaps;

import java.util.HashMap;

import android.app.Activity;
import android.content.res.Resources;
import android.util.SparseBooleanArray;

import com.mad.lib.R;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.lib.utils.BitmapsLoader;

public abstract class BitmapsStore
{

	private static final HashMap<Class<?>, BitmapsStore>	m_mapActivities	=
																					new HashMap<Class<?>, BitmapsStore>();

	public abstract SparseBooleanArray getIds();

	public static BitmapsStore ofType(Class<? extends Activity> activity)
	{
		return m_mapActivities.get(activity);
	}

	public static void register(Class<? extends Activity> type,
								BitmapsStore store)
	{
		m_mapActivities.put(type, store);
	}

	public void unloadBitmaps(Resources resources)
	{
		SparseBooleanArray arIds = getIds();
		if (arIds == null)
		{
			return;
		}
		BitmapsLoader loader =
				AppLibraryServiceProvider.getInstance().get(
						R.service.bitmaps_loader);

		for (int i = 0; i < arIds.size(); i++)
		{
			int nKey = arIds.keyAt(i);
			loader.unloadBitmap(resources, nKey);
		}
	}

	/**
	 * Preloads bitmaps necessary for this activity h Notice that this method is
	 * static and so it doesnt have the resources yet.
	 * 
	 * @param resources
	 */
	public void preloadBitmaps(final Resources resources)
	{
		if (getIds() == null)
		{
			return;
		}
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				BitmapsLoader loader =
						AppLibraryServiceProvider.getInstance()
								.get(R.service.bitmaps_loader);

				SparseBooleanArray arIds = getIds();

				for (int i = 0; i < arIds.size(); i++)
				{
					int nKey = arIds.keyAt(i);
					boolean fValue = arIds.get(nKey);
					loader.loadBitmap(resources, nKey, fValue);
				}
			}
		}).start();
	}
}
