package com.mad.metronomelib.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mad.lib.display.utils.DisplayHelper;
import com.mad.metronomelib.R;

public class MySpinnerAdapter extends ArrayAdapter<Integer>
{
	public MySpinnerAdapter(Context context, Integer[] values)
	{
		super(context, R.layout.spinner_text_item, values);
		setDropDownViewResource(R.layout.spinner_text_item);
	}

	@Override
	public View getView(int position,
						View convertView,
						ViewGroup parent)
	{
		TextView v =
				(TextView) super.getView(position, convertView,
						parent);
		if (convertView == null)
		{
			DisplayHelper.scaleFontSize(v);
			DisplayHelper.scalePadding(v);
		}
		return v;
	}

	@Override
	public View getDropDownView(int position,
								View convertView,
								ViewGroup parent)
	{
		TextView v =
				(TextView) super.getDropDownView(position,
						convertView, parent);
		if (convertView == null)
		{
			DisplayHelper.scaleFontSize(v);
			DisplayHelper.scalePadding(v);
		}
		return v;
	}
};
