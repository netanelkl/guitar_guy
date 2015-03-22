package com.mad.guitarteacher.display.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.mad.guitarteacher.display.graphics.animation.IShowHideDrawable;
import com.mad.guitarteacher.display.graphics.animation.OnHiddenListener;
import com.mad.guitarteacher.display.graphics.animation.OnShownListener;
import com.mad.lib.display.graphics.GraphicsPoint;

/**
 * A single fret finger mark.
 * 
 * @author Tom
 * 
 */
public class TextDrawable extends GraphicsDrawableBase implements
		IShowHideDrawable
{
	private final Paint		m_paintTitle		= new Paint();
	private final PointF	m_pntTextPosition	=
														new GraphicsPoint();
	private String			m_strText			= null;
	private DisplayState	m_DisplayState;
	private final Rect		m_rectSpace			= new Rect();

	/**
	 * Create a new instance of the TextDrawable class.
	 * 
	 */
	public TextDrawable(final Rect rectSpace)
	{
		m_rectSpace.set(rectSpace);
		m_paintTitle.setTextSize(m_rectSpace.height());
		m_paintTitle.setColor(Color.BLACK);
		m_DisplayState = DisplayState.Hidden;
	}

	public void setText(String strText)
	{
		Rect bounds = new Rect();
		m_paintTitle.getTextBounds(strText, 0, strText.length(),
				bounds);
		m_pntTextPosition.set(m_rectSpace.centerX()
				- (bounds.width() / 2),
				(m_rectSpace.top + bounds.height()));

		m_strText = strText;
		setBounds(m_rectSpace);
	}

	@Override
	public void draw(Canvas canvas)
	{
		if (getDisplayState() != DisplayState.Hidden)
		{
			super.draw(canvas);
			if (m_strText != null)
			{
				canvas.drawText(m_strText, m_pntTextPosition.x,
						m_pntTextPosition.y, m_paintTitle);
			}
		}
	}

	@Override
	public DisplayState getDisplayState()
	{
		return m_DisplayState;
	}

	@Override
	public void hide(boolean fViolent, OnHiddenListener listener)
	{
		m_DisplayState = DisplayState.Hidden;

	}

	@Override
	public void show(OnShownListener listener)
	{
		m_DisplayState = DisplayState.Shown;
	}
}
