package com.mad.guitarteacher.display.views;

import android.content.Context;

import com.mad.guitarteacher.display.graphics.PauseMenuButton;
import com.mad.guitarteacher.display.graphics.animation.IShowHideDrawable;
import com.mad.guitarteacher.display.graphics.animation.OnHiddenListener;
import com.mad.guitarteacher.display.graphics.popups.PausingMenu;
import com.mad.guitarteacher.services.IGameManager;

/**
 * This is a mid-level class designed to act as a bridge between the actions
 * coming from the pausing menu and the manager.
 * 
 * @author Nati
 * 
 */
public class PausingMenuManager
{
	/**
	 * @param context
	 * @param nEventX
	 * @param nEventY
	 * @param actionsManager
	 */
	public boolean handlePausingMenuTouch(	final Context context,
											final int nEventX,
											final int nEventY,
											final IGameManager actionsManager,
											final PausingMenu pauseMenu)
	{
		switch (pauseMenu.handleTouchedWithIndex(context,
				nEventX, nEventY))
		{
			case PauseMenuButton.BUTTON_TYPE_HOME:
				hideMenu(pauseMenu, new OnHiddenListener()
				{

					@Override
					public void onHidden(IShowHideDrawable drawable)
					{
						actionsManager.switchToHome(context);
					}
				});
				break;
			case PauseMenuButton.BUTTON_TYPE_RESUME:
				actionsManager.resumeExercise(context);
				break;
			case PauseMenuButton.BUTTON_TYPE_RESTART:
			{
				hideMenu(pauseMenu, new OnHiddenListener()
				{

					@Override
					public void onHidden(IShowHideDrawable drawable)
					{
						actionsManager.restartExercise(context);
					}
				});
				break;
			}
			case PauseMenuButton.BUTTON_TYPE_EXERCISES:
			{
				hideMenu(pauseMenu, new OnHiddenListener()
				{

					@Override
					public void onHidden(IShowHideDrawable drawable)
					{
						actionsManager
								.switchToExercises(context);

					}
				});
				break;
			}
			case PauseMenuButton.BUTTON_TYPE_NEXT_STAGE:
			{
				hideMenu(pauseMenu, new OnHiddenListener()
				{

					@Override
					public void onHidden(IShowHideDrawable drawable)
					{
						actionsManager.nextExercise(context);
					}
				});
				break;
			}
			case PauseMenuButton.BUTTON_TYPE_NONE:
			default:
				return false;
		}

		return true;
	}

	private void hideMenu(	PausingMenu menu,
							final OnHiddenListener listener)
	{
		menu.hide(false, new OnHiddenListener()
		{

			@Override
			public void onHidden(IShowHideDrawable drawable)
			{
				listener.onHidden(drawable);
			}
		});
	}
}
