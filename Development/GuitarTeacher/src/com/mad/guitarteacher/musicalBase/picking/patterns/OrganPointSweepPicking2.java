package com.mad.guitarteacher.musicalBase.picking.patterns;

import com.mad.guitarteacher.musicalBase.picking.FingerPicking;
import com.mad.guitarteacher.musicalBase.picking.PickingFinger;

/**
 * Organ point sweep picking.
 * @author Tom
 *
 */
public class OrganPointSweepPicking2 extends SweepPicking
{
	@Override
	protected FingerPicking setFingers(PickingFinger bass, PickingFinger index,
								PickingFinger middle, PickingFinger ring)
	{
		FingerPicking result = new FingerPicking();
		result.addSingleFinger(bass);
		result.addSingleFinger(index);
		result.addSingleFinger(ring);
		result.addSingleFinger(index);
		result.addSingleFinger(middle);
		result.addSingleFinger(index);
		result.addSingleFinger(ring);
		result.addSingleFinger(index);
		
		return result;
	}

}
