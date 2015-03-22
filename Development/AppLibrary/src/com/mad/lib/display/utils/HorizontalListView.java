package com.mad.lib.display.utils;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class HorizontalListView extends HorizontalScrollView
{
	BaseAdapter		m_Adapter;
	LinearLayout	m_LinearList;

	public HorizontalListView(final Context context)
	{
		super(context);
		m_LinearList = new LinearLayout(context);
		addView(m_LinearList);
	}

	public HorizontalListView(	final Context context,
								final AttributeSet attributes)
	{
		super(context, attributes);
		m_LinearList = new LinearLayout(context);
		addView(m_LinearList);
	}

	public HorizontalListView(	final Context context,
								final AttributeSet attributes,
								final int arg)
	{
		super(context, attributes, arg);
		m_LinearList = new LinearLayout(context);
		addView(m_LinearList);
	}

	/**
	 * @param adapter
	 */
	private void populateList(final BaseAdapter adapter)
	{
		m_LinearList.removeAllViews();
		if (adapter != null)
		{
			for (int i = 0; i < adapter.getCount(); i++)
			{
				m_LinearList.addView(adapter.getView(i, null,
						m_LinearList));
			}
		}
	}

	private void redraw()
	{
		for (int i = 0; i < m_Adapter.getCount(); i++)
		{
			m_Adapter.getView(i, m_LinearList.getChildAt(i),
					m_LinearList);
		}
	}

	public void setAdapter(final BaseAdapter adapter)
	{
		m_Adapter = adapter;
		if (adapter != null)
		{
			adapter.registerDataSetObserver(new DataSetObserver()
			{
				@Override
				public void onChanged()
				{
					super.onChanged();
					redraw();
				}
			});
		}
		populateList(adapter);
	}
}
