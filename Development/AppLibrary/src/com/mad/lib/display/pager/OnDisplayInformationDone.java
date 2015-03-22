package com.mad.lib.display.pager;

/**
 * Notifies that the display information window has been dismissed.
 * 
 * @author Nati
 * 
 */
public interface OnDisplayInformationDone
{
	public static final int	PLAY_BUTTON			= 0;
	public static final int	SIMULATION_BUTTON	= 1;
	public static final int	CLOSE_BUTTON		= 2;
	public static final int	BACK_BUTTON			= 3;

	public void onDisplayInformationDone(int nCause);
}
