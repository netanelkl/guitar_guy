package com.mad.guitarteacher.musicalBase.picking.patterns;

import java.util.ArrayList;

import com.mad.guitarteacher.musicalBase.picking.EPickingFinger;
import com.mad.guitarteacher.musicalBase.picking.FingerPicking;
import com.mad.guitarteacher.musicalBase.picking.PickingFinger;
import com.mad.lib.musicalBase.FretFingerPairBase;

/**
 * Specific implementation for finger picking.
 * 
 * @author Tom
 * 
 */
public abstract class SweepPicking implements
		IFingerPickingCreator
{
	/**
	 * Create a new instance of the SweepPicking.
	 * 
	 * @param fingers
	 *            - Assumed sorted by string! (from 6th to 1fst!).
	 */
	@Override
	public FingerPicking getPicking(ArrayList<FretFingerPairBase> fingers)
	{
		// TODO: Validate the fingers array size!
		// (will not work if under 4 strings).

		// Get the bass note.
		PickingFinger bass =
				new PickingFinger(	EPickingFinger.Thumb
											.ordinal(),
									fingers.get(0).getString());

		// Get the next three.
		PickingFinger index =
				new PickingFinger(	EPickingFinger.Index
											.ordinal(),
									fingers.get(
											fingers.size() - 3)
											.getString());
		PickingFinger middle =
				new PickingFinger(EPickingFinger.Middle
						.ordinal(), fingers.get(
						fingers.size() - 2).getString());
		PickingFinger ring =
				new PickingFinger(	EPickingFinger.Index
											.ordinal(),
									fingers.get(
											fingers.size() - 1)
											.getString());

		return setFingers(bass, index, middle, ring);
	}

	/**
	 * Set the fingers in order.
	 * 
	 * @param bass
	 * @param index
	 * @param middle
	 * @param ring
	 */
	protected abstract FingerPicking setFingers(PickingFinger bass,
												PickingFinger index,
												PickingFinger middle,
												PickingFinger ring);
}
