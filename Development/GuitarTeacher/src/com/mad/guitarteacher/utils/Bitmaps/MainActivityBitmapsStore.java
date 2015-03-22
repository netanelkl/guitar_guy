package com.mad.guitarteacher.utils.Bitmaps;

import android.util.SparseBooleanArray;

import com.mad.guitarteacher.R;
import com.mad.lib.utils.bitmaps.BitmapsStore;

public class MainActivityBitmapsStore extends BitmapsStore
{
	final SparseBooleanArray	m_arIds	=
												new SparseBooleanArray();

	public MainActivityBitmapsStore()
	{
		m_arIds.put(R.drawable.main_bg_guitar, false);
		m_arIds.put(R.drawable.main_exercises_btn, true);
		m_arIds.put(R.drawable.main_start_btn, true);
		m_arIds.put(R.drawable.main_tuner_btn, true);
		m_arIds.put(R.drawable.main_metronome_btn, true);
		m_arIds.put(R.drawable.main_settings_btn, true);
		m_arIds.put(R.drawable.game_total_points_star, true);

	}

	@Override
	public SparseBooleanArray getIds()
	{
		return m_arIds;
	}

}
