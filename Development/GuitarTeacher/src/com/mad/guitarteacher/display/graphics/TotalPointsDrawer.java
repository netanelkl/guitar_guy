package com.mad.guitarteacher.display.graphics;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PointF;

import com.mad.guitarteacher.R;
import com.mad.lib.display.utils.DisplayHelper;

/**
 * A drawable for the total points star thing in the right top of the screen.
 * 
 * @author Nati
 * 
 */
public class TotalPointsDrawer extends GraphicsDrawableBase
{
	private final Paint								m_Paint				=
																				new Paint();

	/**
	 * The image of the stage.
	 */
	private static Bitmap							s_bmpStarImage		=
																				null;

	/**
	 * The position right to the star.
	 */
	private final PointF							m_pntStartPosition	=
																				new PointF();

	private String									m_strPoints			=
																				"0";
	private int										m_points			=
																				0;

	private final TotalPointsAnimatorUpdateListener	m_totalPointsAnimatorListener;

	private final Activity							m_activity;

	/**
	 * Creates the TotalPointsDrawer (star and value indicator) at the given
	 * location.
	 * 
	 * @param activity
	 *            - The current activity. The context.
	 * @param sfRight
	 *            The right of the current drawable.
	 * @param sfTop
	 *            The top of the current drawable.
	 */
	public TotalPointsDrawer(	Activity activity,
								final float sfRight,
								final float sfTop)
	{
		if (s_bmpStarImage == null)
		{
			s_bmpStarImage =
					DisplayHelper.getScaledBitmap(activity
							.getResources(),
							R.drawable.game_total_points_star);

		}

		m_activity = activity;

		m_Paint.setColor(Color.BLACK);
		m_Paint.setTextAlign(Align.RIGHT);
		int nFontSize =
				(int) (s_bmpStarImage.getHeight() * 0.7f);
		m_Paint.setTextSize(nFontSize);

		m_pntStartPosition.set(sfRight
				- s_bmpStarImage.getWidth(), sfTop);

		m_totalPointsAnimatorListener =
				new TotalPointsAnimatorUpdateListener();

		m_Paint.getTextBounds(m_strPoints, 0, m_strPoints
				.length(), m_rectBounds);
		setBounds(m_rectBounds);
	}

	@Override
	public void draw(final Canvas canvas)
	{
		super.draw(canvas);
		canvas.drawBitmap(s_bmpStarImage, m_pntStartPosition.x,
				m_pntStartPosition.y, null);

		canvas.drawText(
				m_strPoints,
				m_pntStartPosition.x,
				m_pntStartPosition.y
						+ (s_bmpStarImage.getHeight() / 2)
						+ ((m_rectBounds.bottom - m_rectBounds.top) / 2),
				m_Paint);
	}

	/**
	 * Update the total points value.
	 * 
	 * @param totalPoints
	 *            - Total points of the user.
	 */
	public void updateTotalPoints(int totalPoints)
	{
		final ObjectAnimator animator =
				ObjectAnimator.ofInt(this, "Points", m_points,
						totalPoints);
		animator.addUpdateListener(m_totalPointsAnimatorListener);

		m_activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				animator.start();
			}
		});
	}

	/**
	 * Class implementing the update listener for the total points manager.
	 * 
	 * @author Tom
	 * 
	 */
	class TotalPointsAnimatorUpdateListener implements
			AnimatorUpdateListener
	{

		@Override
		public void onAnimationUpdate(ValueAnimator animation)
		{
			m_strPoints = Integer.toString(m_points);

			// This centers the texts. God thank stack overflow.

			m_Paint.getTextBounds(m_strPoints, 0, m_strPoints
					.length(), m_rectBounds);
			setBounds(m_rectBounds);

		}

	}

	/**
	 * Sets the total points.
	 * 
	 * @param newPoints
	 *            - New value to set as total points.
	 */
	public void setPoints(int newPoints)
	{
		m_points = newPoints;
	}
}
