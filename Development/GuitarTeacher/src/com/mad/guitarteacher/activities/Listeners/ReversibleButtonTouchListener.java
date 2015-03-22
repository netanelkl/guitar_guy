package com.mad.guitarteacher.activities.Listeners;

import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.mad.guitarteacher.display.utils.ReverseInterpolator;

public abstract class ReversibleButtonTouchListener implements
		OnTouchListener
{
	LinearInterpolator			m_regularInterpolator	=
																new LinearInterpolator();
	ReverseInterpolator			m_reverseInterpolator	=
																new ReverseInterpolator();
	final Animation				m_animScaleup;
	private final static int	DURATION_SCALE			= 500;
	// Variable rect to hold the bounds of the view
	private Rect				m_rectBounds;

	/**
	 * Is the touch down and in rect.
	 */
	private boolean				m_fActive				= false;

	public ReversibleButtonTouchListener(	final Context context,
											final int animId)
	{
		m_animScaleup =
				AnimationUtils.loadAnimation(context, animId);
		m_animScaleup.setFillAfter(true);
		m_animScaleup.setDuration(DURATION_SCALE);
	}

	protected abstract void onClick(final View view);

	/**
	 * @param view
	 * @param animScaleup
	 */
	private void onClickCancelled(	final View view,
									final Animation animScaleup)
	{
		if (m_fActive)
		{
			animScaleup.setInterpolator(m_reverseInterpolator);
			view.startAnimation(animScaleup);
			m_fActive = false;
		}
	}

	@Override
	public boolean onTouch(	final View view,
							final MotionEvent event)
	{

		int nAction = event.getAction();
		switch (nAction)
		{
			case MotionEvent.ACTION_DOWN:
			{
				if (m_fActive == false)
				{
					m_fActive = true;
					m_rectBounds =
							new Rect(	view.getLeft(),
										view.getTop(),
										view.getRight(),
										view.getBottom());

					m_animScaleup
							.setInterpolator(m_regularInterpolator);
					view.startAnimation(m_animScaleup);
				}
				break;
			}
			case MotionEvent.ACTION_MOVE:
			{
				if (m_fActive)
				{
					if (!m_rectBounds.contains(view.getLeft()
							+ (int) event.getX(), view.getTop()
							+ (int) event.getY()))
					{
						// User moved outside bounds
						onClickCancelled(view, m_animScaleup);
					}
				}
				break;
			}
			case MotionEvent.ACTION_UP:
			{
				if (!m_rectBounds.contains(view.getLeft()
						+ (int) event.getX(), view.getTop()
						+ (int) event.getY()))
				{
					onClickCancelled(view, m_animScaleup);
				}
				else
				{
					onClick(view);
					m_fActive = false;
				}
				break;
			}
		}

		return true;
	}

}
