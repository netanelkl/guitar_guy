package com.mad.tunerlib.recognition;

/**
 * Maintains a list of incoming audio packet input observers.
 * 
 * @author Nati
 * 
 */
public interface IAudioInputObserverList
{

	/**
	 * Register an observer to the list. Hell be notified when a packet is
	 * received.
	 * 
	 * @param pObserver
	 *            The observer to register.
	 * @return
	 */
	boolean register(IAudioInputObserver pObserver);

	/**
	 * Unregister an observer from the list.
	 * 
	 * @param pObserver
	 *            The observer to unregister.
	 * @return
	 */
	boolean unregister(IAudioInputObserver pObserver);
}
