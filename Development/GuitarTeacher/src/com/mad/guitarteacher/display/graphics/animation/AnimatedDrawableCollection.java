package com.mad.guitarteacher.display.graphics.animation;

import java.util.ListIterator;

import com.mad.guitarteacher.display.graphics.DisplayState;
import com.mad.guitarteacher.display.graphics.GraphicsCollection;
import com.mad.guitarteacher.display.graphics.GraphicsDrawableBase;

/**
 * A collection of animated drawables (basically IShowHideDrawable s).
 * 
 * @author Nati
 * 
 */
public class AnimatedDrawableCollection extends
		GraphicsCollection implements IShowHideDrawable
{
	private OnHiddenListener		m_OnHiddenListener	= null;
	private OnShownListener			m_OnShownListener	= null;

	private final OnHiddenListener	m_OnElementHidden	=
																new OnHiddenListener()
																{

																	@Override
																	public void onHidden(IShowHideDrawable drawable)
																	{
																		if ((m_OnHiddenListener != null)
																				&& (getDisplayState() == DisplayState.Hidden))
																		{
																			m_OnHiddenListener
																					.onHidden(drawable);
																		}
																	}
																};
	private final OnShownListener	m_OnElementShown	=
																new OnShownListener()
																{

																	@Override
																	public void onShown(IShowHideDrawable drawable)
																	{

																		if ((m_OnShownListener != null)
																				&& (getDisplayState() == DisplayState.Shown))
																		{
																			m_OnShownListener
																					.onShown(drawable);
																		}
																	}
																};

	@Override
	public synchronized DisplayState getDisplayState()
	{
		DisplayState globalState = null;
		for (GraphicsDrawableBase drawable : m_arCollection)
		{
			IShowHideDrawable asAnimated =
					(IShowHideDrawable) drawable;
			DisplayState currentState =
					asAnimated.getDisplayState();
			if (globalState != null)
			{
				if (currentState != globalState)
				{
					return DisplayState.Undetermined;
				}
			}
			globalState = currentState;
		}

		return globalState;
	}

	/**
	 * Hides the collection of objects.
	 * 
	 * @param listener
	 */
	@Override
	public synchronized void hide(	boolean fViolent,
									final OnHiddenListener listener)
	{
		if (m_arCollection.size() == 0)
		{
			listener.onHidden(this);
		}
		else
		{
			m_OnHiddenListener = listener;
			for (GraphicsDrawableBase drawable : m_arCollection)
			{
				IShowHideDrawable asAnimated =
						(IShowHideDrawable) drawable;
				asAnimated.hide(fViolent, m_OnElementHidden);
			}
		}
	}

	/**
	 * Shows the collection of objects.
	 * 
	 * @param listener
	 */
	@Override
	public synchronized void show(final OnShownListener listener)
	{
		m_OnShownListener = listener;
		for (GraphicsDrawableBase drawable : m_arCollection)
		{
			IShowHideDrawable asAnimated =
					(IShowHideDrawable) drawable;
			asAnimated.show(m_OnElementShown);
		}
	}

	public synchronized void pause()
	{
		ListIterator<GraphicsDrawableBase> iterator =
				m_arCollection.listIterator();
		while (iterator.hasNext())
		{
			AnimatedDrawable asAnimated =
					(AnimatedDrawable) iterator.next();
			asAnimated.pause();
		}
	}

	public synchronized void resume()
	{
		for (GraphicsDrawableBase drawable : m_arCollection)
		{
			AnimatedDrawable asAnimated =
					(AnimatedDrawable) drawable;
			asAnimated.resume();
		}
	}
}
