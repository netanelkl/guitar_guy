package com.mad.guitarteacher.display.graphics.popups;

import android.content.Context;
import android.graphics.Rect;

import com.mad.guitarteacher.R;
import com.mad.guitarteacher.display.graphics.PauseMenuButton;
import com.mad.guitarteacher.display.graphics.TextDrawable;

public class PauseMenu extends PausingMenu
{
	private static int[]		s_arButtons							=
																			{
			PauseMenuButton.BUTTON_TYPE_RESUME,
			PauseMenuButton.BUTTON_TYPE_RESTART,
			PauseMenuButton.BUTTON_TYPE_EXERCISES,
			PauseMenuButton.BUTTON_TYPE_HOME								};

	private static final float	TEXT_PAUSE_TOP_IN_DRAWING_SPACE		=
																			0.3f;
	private static final float	TEXT_PAUSE_HEIGHT_IN_DRAWING_SPACE	=
																			0.3f;

	/**
	 * A text to display the word 'pause'.
	 */
	private final TextDrawable	m_textPause;

	public PauseMenu(	final Context context,
						final float sfCenterLeft,
						final float sfCenterTop)
	{
		super(context, sfCenterLeft, sfCenterTop, s_arButtons);

		int nTop =
				(int) (m_rectDrawingSpace.top + (TEXT_PAUSE_TOP_IN_DRAWING_SPACE * m_rectDrawingSpace
						.height()));
		int nBottom =
				(int) (nTop + (TEXT_PAUSE_HEIGHT_IN_DRAWING_SPACE * m_rectDrawingSpace
						.height()));
		m_textPause =
				new TextDrawable(new Rect(	m_rectDrawingSpace.left,
											nTop,
											m_rectDrawingSpace.right,
											nBottom));
		m_textPause.setText(context.getString(R.string.pause));
		m_arButtonsCollection.add(m_textPause);
	}
}
