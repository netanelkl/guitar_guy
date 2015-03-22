package com.mad.guitarteacher.musicalBase.picking;

import java.util.ArrayList;

/**
 * Represents a finger picking pattern
 * @author Tom
 *
 */
public class FingerPicking
{
	// Fingers to pick.
	protected ArrayList<ArrayList<PickingFinger>> m_pickingFingers;
	
	public ArrayList<ArrayList<PickingFinger>> getPickingFingers()
	{
		return m_pickingFingers;
	}
	
	/**
	 * Protected empty constructor.
	 */
	public FingerPicking()
	{
		m_pickingFingers = new ArrayList<ArrayList<PickingFinger>>();
	}
	
	/**
	 * Adds a single finger picking.
	 * @param fingers
	 */
	public void addFingerPicking(ArrayList<PickingFinger> fingers)
	{
		m_pickingFingers.add(new ArrayList<PickingFinger>(fingers));
	}
	
	/**
	 * Create a new instance of a FingerPicking.
	 * @param fingers - Fingers to play in the pattern.
	 */
	public FingerPicking(ArrayList<ArrayList<PickingFinger>> fingers)
	{
		m_pickingFingers = new ArrayList<ArrayList<PickingFinger>>();
		
		for (ArrayList<PickingFinger> pickingFingers : fingers)
		{
			addFingerPicking(pickingFingers);
		}
	}
	
	/**
	 * Create a new instance of FingerPicking.
	 * @param fingers - Fingers to play in the pattern.
	 */
	public FingerPicking(PickingFinger[] fingers)
	{
		m_pickingFingers = new ArrayList<ArrayList<PickingFinger>>();
		addFingerArray(fingers);
	}
	
	/**
	 * Create a new instance of FingerPicking.
	 * @param fingers - Fingers to play in the pattern.
	 */
	public FingerPicking(PickingFinger[][] fingers)
	{
		m_pickingFingers = new ArrayList<ArrayList<PickingFinger>>();
		
		for (PickingFinger[] pickingFingers : fingers)
		{
			addFingerArray(pickingFingers);
		}
	}

	/**
	 * Adds a single finger 
	 * @param finger
	 */
	public void addSingleFinger(PickingFinger finger)
	{
		ArrayList<PickingFinger> arrFingerPickings = 
				new ArrayList<PickingFinger>();
		arrFingerPickings.add(finger);
		m_pickingFingers.add(arrFingerPickings);
	}
	
	/**
	 * Adds an array of fingers.
	 * @param fingers
	 */
	private void addFingerArray(PickingFinger[] fingers)
	{
		ArrayList<PickingFinger> pickingArray = new ArrayList<PickingFinger>();
		for (PickingFinger pickingFinger : fingers)
		{
			pickingArray.add(pickingFinger);
		}
		m_pickingFingers.add(pickingArray);
	}
}
