package com.mad.guitarteacher.musicalBase.picking;

import java.util.ArrayList;

import com.mad.guitarteacher.musicalBase.picking.patterns.IFingerPickingCreator;
import com.mad.guitarteacher.musicalBase.picking.patterns.InverseOrganPointSweepPicking1;
import com.mad.guitarteacher.musicalBase.picking.patterns.InverseOrganPointSweepPicking2;
import com.mad.guitarteacher.musicalBase.picking.patterns.InverseRoundSweepPicking;
import com.mad.guitarteacher.musicalBase.picking.patterns.OrganPointSweepPicking1;
import com.mad.guitarteacher.musicalBase.picking.patterns.OrganPointSweepPicking2;
import com.mad.guitarteacher.musicalBase.picking.patterns.RoundSweepPicking;
import com.mad.lib.musicalBase.FretFingerPairBase;

/**
 * Factory for getting finger picking values.
 * 
 * @author Tom
 * 
 */
public class FingerPickingFactory
{
	static ArrayList<IFingerPickingCreator>	s_pickingCreators	=
																		new ArrayList<IFingerPickingCreator>();

	static
	{
		s_pickingCreators.add(new RoundSweepPicking());
		s_pickingCreators.add(new InverseRoundSweepPicking());
		s_pickingCreators
				.add(new InverseOrganPointSweepPicking1());
		s_pickingCreators
				.add(new InverseOrganPointSweepPicking2());
		s_pickingCreators.add(new OrganPointSweepPicking1());
		s_pickingCreators.add(new OrganPointSweepPicking2());
	}

	/**
	 * Get a finger picking.
	 * 
	 * @param fingers
	 *            - Fingers to hold (left hand).
	 * @param creatorId
	 *            - Id for the picking type.
	 * @return Finger picking (right hand).
	 */
	public static FingerPicking getPicking(	ArrayList<FretFingerPairBase> fingers,
											int creatorId)
	{
		IFingerPickingCreator creator =
				s_pickingCreators.get(creatorId
						% s_pickingCreators.size());

		return creator.getPicking(fingers);
	}
}
