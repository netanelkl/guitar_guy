package com.mad.guitarteacher.musicalBase.picking.patterns;

import com.mad.guitarteacher.musicalBase.picking.FingerPicking;
import com.mad.guitarteacher.musicalBase.picking.PickingFinger;

/**
 * An organ point.
 * @author Tom
 *
 */
public class OrganPointSweepPicking1 extends SweepPicking
{
	@Override
	protected FingerPicking setFingers(PickingFinger bass, PickingFinger index,
								PickingFinger middle, PickingFinger ring)
	{
		FingerPicking result = new FingerPicking();
		result.addSingleFinger(bass);
		result.addSingleFinger(index);
		result.addSingleFinger(middle);
		result.addSingleFinger(index);
		result.addSingleFinger(ring);
		result.addSingleFinger(index);
		result.addSingleFinger(middle);
		result.addSingleFinger(index);
		
		return result;
	}

}
