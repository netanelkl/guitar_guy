package com.mad.guitarteacher.display.graphics;

import android.content.Context;
import android.graphics.PointF;

import com.mad.guitarteacher.R;
import com.mad.guitarteacher.display.graphics.animation.HangingBitmapDrawable;
import com.mad.lib.display.graphics.GraphicsPoint;
import com.mad.lib.display.utils.DisplayHelper;

/**
 * A drawable for the pause button.
 * 
 * @author Nati
 * 
 */
public class PauseButton extends HangingBitmapDrawable
{
	private static final float	MARGIN	= 20;

	/**
	 * Creates a pause button.
	 * 
	 * @param context
	 *            The context.
	 * @param pntInitialPosition
	 *            Top left position of the button.
	 * @param pointScale
	 *            The screen scale.
	 */
	public PauseButton(	final Context context,
						final PointF pntInitialPosition)
	{
		super(	DisplayHelper.getScaledBitmap(context
						.getResources(),
						R.drawable.game_btn_pause),
				pntInitialPosition,
				HangingDirection.Top,
				MARGIN / GraphicsPoint.getScalePoint().x,
				MARGIN / GraphicsPoint.getScalePoint().y,
				false);

	}

}
