package com.mad.guitarteacher.practice.Acts;

public interface OnActDoneListener
{
	/**
	 * Notifies that the current act has ended. It also indicates whether the
	 * next acts should run in simulation mode.
	 * 
	 * @param act
	 * @param fStartSimulation
	 */
	public void onActDone(ActBase act, boolean fStartSimulation);
}
