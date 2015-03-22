package com.mad.lib.utils;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;

import com.mad.lib.display.utils.DisplayHelper;

/**
 * Provides a bitmap loading machenism used primarily for preloading bitmaps to
 * speed up activity boot times.
 * 
 * @author Nati
 * 
 */
@SuppressLint("UseSparseArrays")
public class BitmapsLoader
{
	private final HashMap<Integer, Bitmap>	m_arBitmaps	=
																new HashMap<Integer, Bitmap>();

	public void loadBitmap(	Resources resources,
							int nResId,
							boolean fKeepProportion)
	{
		// No need to do anything if exists.
		if (m_arBitmaps.containsKey(nResId))
		{
			return;
		}

		// Preload the bitmap.
		m_arBitmaps.put(nResId, DisplayHelper.getScaledBitmap(
				resources, nResId, fKeepProportion));

	}

	public void unloadBitmap(Resources resources, int nResId)
	{
		// No need to do anything if exists.
		if (!m_arBitmaps.containsKey(nResId))
		{
			return;
		}

		// Preload the bitmap.
		m_arBitmaps.remove(nResId);

	}

	public Bitmap getBitmap(Resources resources,
							int nResId,
							boolean fKeepProportion)
	{
		Bitmap bmp;
		if ((bmp = m_arBitmaps.get(nResId)) == null)
		{
			// Preload the bitmap.
			m_arBitmaps.put(nResId, bmp =
					DisplayHelper.getScaledBitmap(resources,
							nResId, fKeepProportion));
		}
		return bmp;
	}
}
