package com.mad.guitarteacher.display.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * A simple static bitmap drawable.
 * 
 * This supports a basic hide and show methods.
 * 
 * @author Nati
 * 
 */
public class SemiTransparentRectDrawable extends
		GraphicsDrawableBase
{

	private final Rect	m_Rect;
	private final Paint	m_Paint;

	/**
	 * Creates a new StaticBitmapDrawable.
	 * 
	 * @param bmpImage
	 *            The bitmap used to create the drawable.
	 * @param sfLeft
	 *            The left position to draw.
	 * @param sfTop
	 *            Top position to draw.
	 */
	public SemiTransparentRectDrawable(Rect rect)
	{
		m_Rect = new Rect(rect);
		m_Paint = new Paint();
		m_Paint.setColor(Color.BLACK);
		m_Paint.setAlpha(127);
	}

	public SemiTransparentRectDrawable()
	{
		this(new Rect());
	}

	public void setRect(Rect rect)
	{
		m_Rect.set(rect);
	}

	@Override
	public void draw(final Canvas canvas)
	{
		canvas.drawRect(m_Rect, m_Paint);
	}

}
