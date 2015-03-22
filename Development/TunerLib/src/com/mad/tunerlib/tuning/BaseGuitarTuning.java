package com.mad.tunerlib.tuning;

import com.mad.lib.musicalBase.Notes;
import com.mad.lib.musicalBase.OctavedNote;

/**
 * Class implementing the ITuning interface of a basic guitar.
 * 
 * @author Tom
 * 
 */
public class BaseGuitarTuning implements ITuning
{
	/**
	 * Array to hold the tuning.
	 */
	OctavedNote[]	m_arTuning;

	/**
	 * Create a new instance of the BaseGuitarTuning.
	 */
	public BaseGuitarTuning()
	{
		m_arTuning = new OctavedNote[6];
		m_arTuning[0] = new OctavedNote(Notes.E, 0);
		m_arTuning[1] = new OctavedNote(Notes.A, 1);
		m_arTuning[2] = new OctavedNote(Notes.D, 1);
		m_arTuning[3] = new OctavedNote(Notes.G, 1);
		m_arTuning[4] = new OctavedNote(Notes.B, 2);
		m_arTuning[5] = new OctavedNote(Notes.E, 2);
	}

	@Override
	public OctavedNote[] getTuning()
	{
		return m_arTuning;
	}

}
