package com.mad.guitarteacher.musicalBase.score;

import android.util.SparseArray;

/**
 * This class will contain information needed to play a certain music piece.
 * 
 * We will hold a "map" of nBeat-TimedNote.
 * 
 * @author Nati
 * 
 */
public class MusicScore
{
	private final SparseArray<TimedMusicalObject>	m_arMusicalObjects;
	public static final int							BEATS_PER_MUSICAL_OBJECT	=
																						1;
	public static final int							DISTANCE_BEATS_PER_NOTE		=
																						2;

	private final int								m_nBeatsPerNote				=
																						BEATS_PER_MUSICAL_OBJECT;
	private final int								m_nDistanceBetweenNotes		=
																						DISTANCE_BEATS_PER_NOTE;

	public MusicScore()
	{
		m_arMusicalObjects =
				new SparseArray<TimedMusicalObject>();
	}

	public boolean add(int nBeatPosition, TimedMusicalObject note)
	{
		if (m_arMusicalObjects.get(nBeatPosition) != null)
		{
			return false;
		}

		m_arMusicalObjects.append(nBeatPosition, note);
		return true;
	}

	public boolean add(TimedMusicalObject note)
	{
		int nBeatsStored = getBeats();
		int nNewPosition =
				roundUp(nBeatsStored, m_nDistanceBetweenNotes);
		m_arMusicalObjects.append(nNewPosition, note);
		return true;
	}

	int roundUp(int nNum, int nMul)
	{
		return ((nNum + (nMul - 1)) / nMul) * nMul;
	}

	public TimedMusicalObject getByBeat(int nBeatPosition)
	{
		return m_arMusicalObjects.get(nBeatPosition);
	}

	public TimedMusicalObject getByIndex(int nNoteIndex)
	{
		return m_arMusicalObjects.get(m_arMusicalObjects
				.keyAt(nNoteIndex));
	}

	/**
	 * Get the number of beats in the score.
	 * 
	 * @return
	 */
	public int getBeats()
	{
		if (m_arMusicalObjects.size() == 0)
		{
			return 0;
		}
		int nKey =
				m_arMusicalObjects.keyAt(m_arMusicalObjects
						.size() - 1);
		return nKey + m_arMusicalObjects.get(nKey).getBeats();
	}

	/**
	 * Gets the number of musical objects in the score.
	 * 
	 * @return
	 */
	public int size()
	{
		return m_arMusicalObjects.size();
	}
}
