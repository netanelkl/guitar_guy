package com.mad.guitarteacher.display.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mad.guitarteacher.R;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.lib.utils.BitmapsLoader;

public class TotalPointsView extends LinearLayout
{

	private final TextView	viewText;

	public TotalPointsView(Context context)
	{
		this(context, null, 0);
	}

	public TotalPointsView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public TotalPointsView(	Context context,
							AttributeSet attrs,
							int defStyle)
	{
		super(context, attrs, defStyle);
		LayoutInflater inflater =
				(LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.main_total_points_displayer,
				this, true);
		ImageView view = (ImageView) findViewById(R.id.image);
		viewText = (TextView) findViewById(R.id.text);
		BitmapsLoader bitmapsLoader =
				AppLibraryServiceProvider.getInstance().get(
						R.service.bitmaps_loader);
		view.setImageBitmap(bitmapsLoader.getBitmap(context
				.getResources(),
				R.drawable.game_total_points_star, true));
	}

	public void setTotalPoints(int nPoints)
	{
		viewText.setText(Integer.toString(nPoints));
	}
}
