package com.mad.guitarteacher.musicalBase.picking.patterns;

import com.mad.guitarteacher.musicalBase.picking.PickingFinger;
import com.mad.guitarteacher.musicalBase.picking.FingerPicking;

;

/**
 * Represents a round type of picking.
 * 
 * @author Tom
 * 
 */
public class RoundSweepPicking extends SweepPicking
{
	/**
	 * Create a new instance of OrganPointSweepPicking
	 * 
	 * @param fingers
	 */

	@Override
	protected FingerPicking setFingers(PickingFinger bass, PickingFinger index,
										PickingFinger middle, PickingFinger ring)
	{
		FingerPicking fingerPickingResult = new FingerPicking();
		fingerPickingResult.addSingleFinger(bass);
		fingerPickingResult.addSingleFinger(index);
		fingerPickingResult.addSingleFinger(middle);
		fingerPickingResult.addSingleFinger(ring);
		fingerPickingResult.addSingleFinger(middle);
		fingerPickingResult.addSingleFinger(index);

		return fingerPickingResult;
	}
}
