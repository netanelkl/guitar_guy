package com.mad.lib.chordGeneration;

import com.mad.lib.musicalBase.IHandPositioning;
import com.mad.lib.musicalBase.chords.Chord;

/**
 * This class generates chords, it is the chord generator.
 * 
 * @author Tom
 * 
 */
public interface IChordGenerator
{

	public abstract IHandPositioning getChordHandPositioning(	final Chord chord,
																final int lastScale);

	// public abstract IHandPositioning getFingerPosition( final int[] lstNotes,
	// final FingerPosition lastPosition,
	// final int lastScale);

	public abstract IHandPositioning getFingerPosition(	final int[] lstNotes,
														final int lastScale);
}
