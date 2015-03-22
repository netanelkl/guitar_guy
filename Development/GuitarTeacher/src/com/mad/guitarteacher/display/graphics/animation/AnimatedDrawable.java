package com.mad.guitarteacher.display.graphics.animation;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.os.Build;

import com.mad.guitarteacher.display.graphics.DisplayState;
import com.mad.guitarteacher.display.graphics.GraphicsDrawableBase;
import com.mad.lib.display.graphics.GraphicsPoint;

/**
 * An animated drawable.
 * 
 * Provides basic logic to manage and run the Showing/Hiding of a graphics
 * drawable with a sort of a moving based animation. This can mean scaling and
 * shifting.
 * 
 * @author Nati
 * 
 */
public abstract class AnimatedDrawable extends
		GraphicsDrawableBase implements AnimatorUpdateListener,
		AnimatorListener, IShowHideDrawable
{
	/**
	 * The animator.
	 */
	protected ValueAnimator		m_anim;
	/**
	 * The current object display state.
	 */
	protected DisplayState		m_DisplayState			=
																DisplayState.Hidden;

	/**
	 * The listener for the onHidden Event.
	 */
	private OnHiddenListener	m_HiddenListener		= null;

	/**
	 * The current position of the drawable.
	 */
	protected GraphicsPoint		m_pntCurrentPosition	=
																new GraphicsPoint();

	/**
	 * The initial position of the drawable.
	 */
	protected GraphicsPoint		m_pntInitialPosition	=
																new GraphicsPoint();

	/**
	 * The listener for the onShown Event.
	 */
	private OnShownListener		m_ShownListener			= null;

	/**
	 * Creates the show-hide animation.
	 * 
	 * @return the animator object.
	 */
	protected void createAnimation()
	{
		if (m_anim == null)
		{
			m_anim = createAnimationImpl();

			if (m_anim == null)
			{
				onAnimationEnd(null);
				return;
			}

			m_anim.addUpdateListener(this);
			m_anim.addListener(this);
		}
	}

	/**
	 * Creates the animator.
	 * 
	 * @return
	 */
	protected abstract ValueAnimator createAnimationImpl();

	@Override
	public DisplayState getDisplayState()
	{
		return m_DisplayState;
	}

	public void hide()
	{
		hide(false, null);
	}

	/**
	 * Hide the drawable from the screen.
	 */
	@Override
	public void hide(	boolean fViolent,
						final OnHiddenListener listener)
	{
		if (!fViolent)
		{
			m_DisplayState = DisplayState.Hiding;
			createAnimation();
			m_HiddenListener = listener;
			m_fInvalidate = true;
			m_anim.reverse();
		}
		else
		{
			m_DisplayState = DisplayState.Hidden;
			m_fInvalidate = true;
			if (listener != null)
			{
				listener.onHidden(this);
			}
		}
	}

	@Override
	public void onAnimationCancel(final Animator animation)
	{
	}

	@Override
	public void onAnimationEnd(final Animator animation)
	{
		if (m_DisplayState == DisplayState.Hiding)
		{
			m_DisplayState = DisplayState.Hidden;
			if (m_HiddenListener != null)
			{
				m_HiddenListener.onHidden(this);
			}
		}
		else
		{
			m_DisplayState = DisplayState.Shown;
			if (m_ShownListener != null)
			{
				m_ShownListener.onShown(this);
			}
		}
	}

	@Override
	public void onAnimationRepeat(final Animator animation)
	{
	}

	@Override
	public void onAnimationStart(final Animator animation)
	{
	}

	@Override
	public void onAnimationUpdate(final ValueAnimator animator)
	{
		m_fInvalidate = true;
	}

	public final void show()
	{
		show(null);
	}

	@Override
	public void show(final OnShownListener listener)
	{
		m_pntCurrentPosition.set(m_pntInitialPosition);
		m_DisplayState = DisplayState.Showing;
		createAnimation();
		m_ShownListener = listener;
		m_fInvalidate = true;

		if (m_anim != null)
		{
			m_anim.start();
		}
	}

	public void pause()
	{
		if (m_anim != null)
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
			{
				m_anim.pause();
			}
			else
			{
				if (m_anim.isStarted())
				{
					m_anim.cancel();
					m_fIsCanceled = true;
				}
			}

		}
	}

	private boolean	m_fIsCanceled	= false;

	public void resume()
	{
		if (m_anim != null)
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
			{
				m_anim.resume();
			}
			else
			{
				if (m_fIsCanceled)
				{
					m_anim.start();
				}
			}

		}
	}
}
