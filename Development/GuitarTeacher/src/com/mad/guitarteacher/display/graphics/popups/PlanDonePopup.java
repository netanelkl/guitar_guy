package com.mad.guitarteacher.display.graphics.popups;

import android.content.Context;

import com.mad.guitarteacher.display.graphics.PauseMenuButton;
import com.mad.guitarteacher.display.views.PausingMenuManager;
import com.mad.guitarteacher.services.IGameManager;

public class PlanDonePopup extends StarsPausingMenu
{
	private static int[]				s_arButtons	= {
			PauseMenuButton.BUTTON_TYPE_EXERCISES,
			PauseMenuButton.BUTTON_TYPE_HOME		};
	private final PausingMenuManager	m_Manager;
	private final IGameManager			m_GameManager;

	public PlanDonePopup(	Context context,
							float sfCenterLeft,
							float sfCenterTop,
							PausingMenuManager touchManager,
							IGameManager gameManager)
	{
		super(context, sfCenterLeft, sfCenterTop, s_arButtons);
		m_Manager = touchManager;
		m_GameManager = gameManager;
	}

	@Override
	public boolean handleTouched(	Context context,
									int nTouchX,
									int nTouchY)
	{
		return m_Manager.handlePausingMenuTouch(context,
				nTouchX, nTouchY, m_GameManager, this);
	}
}
