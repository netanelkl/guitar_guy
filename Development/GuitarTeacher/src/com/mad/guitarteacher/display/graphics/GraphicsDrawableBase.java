package com.mad.guitarteacher.display.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Represents a basic drawable object
 * 
 * My drawable objects suppors too main methods, update and draw.
 * 
 * @author Nati
 * 
 */
public abstract class GraphicsDrawableBase
{
	protected boolean				m_fInvalidate	= false;
	protected Rect					m_rectBounds	= new Rect();
	private OnBoundsChangedListener	m_BoundsChangedListener;

	public void setOnBoundsChangedListener(OnBoundsChangedListener listener)
	{
		m_BoundsChangedListener = listener;
	}

	/**
	 * Sets the bounds of this graphics object. Will help determine what needs
	 * to be redrawn.
	 * 
	 * @param rect
	 *            The new bounds of this object.
	 */
	protected void setBounds(Rect rect)
	{
		Rect rectOldBounds = new Rect(m_rectBounds);
		m_rectBounds.set(rect);

		if (m_Border != null)
		{
			m_Border.setRect(m_rectBounds);
		}
		if (m_BoundsChangedListener != null)
		{
			m_BoundsChangedListener.onBoundsChanged(
					rectOldBounds, m_rectBounds);
		}
	}

	protected Rect getBounds()
	{
		return m_rectBounds;
	}

	private SemiTransparentRectDrawable	m_Border	= null;

	public GraphicsDrawableBase()
	{
		if (com.mad.lib.utils.Definitions.DebugFlags.DEBUG_SHOW_LAYOUT_BORDERS
				&& !(this instanceof SemiTransparentRectDrawable))
		{
			m_Border = new SemiTransparentRectDrawable();
		}
	}

	/**
	 * Draws the object onto the canvas
	 * 
	 * @param canvas
	 */
	public void draw(Canvas canvas)
	{
		if (m_Border != null)
		{
			m_Border.draw(canvas);
		}
	}

	/**
	 * Decides if the drawable was touched.
	 * 
	 * @param context
	 *            The context.
	 * @param nTouchX
	 *            the x of the touch point.
	 * @param nTouchY
	 *            the y of the touch point.
	 * @return
	 */
	public boolean handleTouched(	final Context context,
									final int nTouchX,
									final int nTouchY)
	{
		return false;
	}

	/**
	 * Updates the logic of the drawable.
	 * 
	 * For example, given an animation from 0 -> 1, it would increase the
	 * current position from 0.1 to 0.2 . You should always call the base's
	 * update method in the end and return its value.
	 * 
	 * @return Whether or not to invalidate the view.
	 */
	public boolean update()
	{
		boolean fToInvalidate = m_fInvalidate;
		m_fInvalidate = false;
		return fToInvalidate;
	}
}
