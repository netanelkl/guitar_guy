package com.mad.guitarteacher.musicalBase.picking.patterns;

import com.mad.guitarteacher.musicalBase.picking.FingerPicking;
import com.mad.guitarteacher.musicalBase.picking.PickingFinger;

/**
 * @author Tom
 *
 */
public class InverseOrganPointSweepPicking1 extends SweepPicking
{
	@Override
	protected FingerPicking setFingers(PickingFinger bass, PickingFinger index,
								PickingFinger middle, PickingFinger ring)
	{
		FingerPicking result = new FingerPicking();
		
		result.addSingleFinger(bass);
		result.addSingleFinger(ring);
		result.addSingleFinger(middle);
		result.addSingleFinger(ring);
		result.addSingleFinger(index);
		result.addSingleFinger(ring);
		result.addSingleFinger(middle);
		result.addSingleFinger(ring);
		
		return result;
	}

}
