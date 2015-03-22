package com.mad.guitarteacher.musicalBase.picking.patterns;

import java.util.ArrayList;

import com.mad.guitarteacher.musicalBase.picking.FingerPicking;
import com.mad.lib.musicalBase.FretFingerPairBase;

public interface IFingerPickingCreator
{
	/**
	 * Get the finger picking for input notes.
	 * 
	 * @return Finger picking
	 */
	FingerPicking getPicking(ArrayList<FretFingerPairBase> fingers);
}
