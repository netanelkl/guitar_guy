package com.mad.guitarteacher.display.graphics;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * A collection of GraphicsDrawableBase.
 * 
 * @author Nati
 * 
 */
public class GraphicsCollection extends GraphicsDrawableBase
{
	/**
	 * The actual collection of drawables.
	 */
	protected ArrayList<GraphicsDrawableBase>	m_arCollection			=
																				new ArrayList<GraphicsDrawableBase>();
	private final OnBoundsChangedListener		m_BoundsChangedListener	=
																				new OnBoundsChangedListener()
																				{

																					@Override
																					public void onBoundsChanged(Rect rectOldBounds,
																												Rect rectNewBounds)
																					{
																						refreshBounds();
																					}
																				};

	/**
	 * Add an item to the list of drawables.
	 * 
	 * @param toAdd
	 *            The item to add.
	 */
	public synchronized void add(final GraphicsDrawableBase toAdd)
	{
		m_arCollection.add(toAdd);
		toAdd.setOnBoundsChangedListener(m_BoundsChangedListener);
		refreshBounds();
		m_fInvalidate = true;
	}

	protected void refreshBounds()
	{
		Rect rectNewBounds = new Rect();
		boolean fSetInitial = false;
		for (GraphicsDrawableBase drawable : m_arCollection)
		{
			Rect rectDrawableBounds = drawable.getBounds();
			if (!fSetInitial)
			{
				rectNewBounds.set(rectDrawableBounds);
				fSetInitial = true;
			}
			else
			{
				rectNewBounds.set(Math.min(rectNewBounds.left,
						rectDrawableBounds.left), Math.min(
						rectNewBounds.top,
						rectDrawableBounds.top), Math.max(
						rectNewBounds.right,
						rectDrawableBounds.right), Math.max(
						rectNewBounds.bottom,
						rectDrawableBounds.bottom));
			}

		}
		setBounds(rectNewBounds);
	}

	/**
	 * Clears the list.
	 */
	public synchronized void clear()
	{
		m_arCollection.clear();
		m_fInvalidate = true;
	}

	@Override
	public synchronized void draw(final Canvas canvas)
	{
		// super.draw(canvas);
		for (GraphicsDrawableBase item : m_arCollection)
		{
			item.draw(canvas);
		}
	}

	@Override
	public synchronized boolean update()
	{
		boolean fToInvalidate = super.update();
		for (int i = 0; i < m_arCollection.size(); i++)
		{
			GraphicsDrawableBase item = m_arCollection.get(i);
			fToInvalidate = item.update() || fToInvalidate;
		}
		return fToInvalidate;
	}

}
