package com.mad.guitarteacher.utils.Bitmaps;

import android.annotation.SuppressLint;
import android.util.SparseBooleanArray;

import com.mad.guitarteacher.R;
import com.mad.lib.utils.bitmaps.BitmapsStore;

public class GameActivityBitmapStore extends BitmapsStore
{
	@SuppressLint("UseSparseArrays")
	final SparseBooleanArray	m_arIds	=
												new SparseBooleanArray();

	public GameActivityBitmapStore()
	{
		m_arIds.put(R.drawable.game_activity_bg, false);
		m_arIds.put(R.drawable.game_bg_frets, false);
		m_arIds.put(R.drawable.game_string_notes, false);
	}

	@Override
	public SparseBooleanArray getIds()
	{
		return m_arIds;
	}

}
