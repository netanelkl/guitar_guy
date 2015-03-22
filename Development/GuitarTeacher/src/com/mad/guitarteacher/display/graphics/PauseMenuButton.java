package com.mad.guitarteacher.display.graphics;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;

import com.mad.guitarteacher.R;
import com.mad.guitarteacher.display.graphics.animation.OvershootBitmapDrawable;

/**
 * A button inside a PauseMenu.
 * 
 * @author Nati
 * 
 */
public class PauseMenuButton extends OvershootBitmapDrawable
{

	/**
	 * The ids of all possible drawables.
	 */
	private static int[]	arResIds				= new int[] {
			R.drawable.game_pause_menu_resume,
			R.drawable.game_pause_menu_restart,
			R.drawable.game_pause_menu_exercises,
			R.drawable.game_pause_menu_home, R.drawable.game_pause_menu_resume };

	/**
	 * Consts for the type of the button.
	 */
	public static final int	BUTTON_TYPE_EXERCISES	= 2;
	public static final int	BUTTON_TYPE_HOME		= 3;
	public static final int	BUTTON_TYPE_NEXT_STAGE	= 4;
	public static final int	BUTTON_TYPE_NONE		= -1;
	public static final int	BUTTON_TYPE_RESTART		= 1;
	public static final int	BUTTON_TYPE_RESUME		= 0;

	/**
	 * The current button type.
	 */
	private final int		m_nButtonType;

	/**
	 * Creates a button that's inside a PauseMenu.
	 * 
	 * @param context
	 *            The context.
	 * @param nButtonType
	 *            The button type.
	 * @param pntPosition
	 *            The absolute position in which to draw the button.
	 * @param pntScale
	 *            The screen scaling.
	 */
	public PauseMenuButton(final Context context, final int nButtonType,
							final PointF pntPosition)
	{
		super(context, arResIds[nButtonType], pntPosition);
		m_nButtonType = nButtonType;

	}

	@Override
	protected ValueAnimator createAnimationImpl()
	{
		return super.createAnimationImpl();
	}

	/**
	 * Gets the button type.
	 * 
	 * @return The button type.
	 */
	public int getButtonType()
	{
		return m_nButtonType;
	}

}
