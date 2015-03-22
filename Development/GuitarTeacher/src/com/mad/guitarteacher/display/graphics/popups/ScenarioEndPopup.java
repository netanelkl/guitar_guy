package com.mad.guitarteacher.display.graphics.popups;

import android.content.Context;

import com.mad.guitarteacher.display.graphics.PauseMenuButton;

public class ScenarioEndPopup extends StarsPausingMenu
{
	private static int[]	s_arButtons	= {
			PauseMenuButton.BUTTON_TYPE_NEXT_STAGE,
			PauseMenuButton.BUTTON_TYPE_RESTART,
			PauseMenuButton.BUTTON_TYPE_EXERCISES,
			PauseMenuButton.BUTTON_TYPE_HOME };

	public ScenarioEndPopup(Context context,
							float sfCenterLeft,
							float sfCenterTop)
	{
		super(context, sfCenterLeft, sfCenterTop, s_arButtons);
	}
}
