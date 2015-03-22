package com.mad.tunerlib.chordGeneration;

import java.util.ArrayList;
import java.util.List;

import android.util.Pair;

import com.mad.lib.musicalBase.FretFingerPairBase;
import com.mad.lib.musicalBase.IHandPositioning;
import com.mad.tunerlib.musicalBase.BasicHandPositioning;
import com.mad.tunerlib.musicalBase.FretFingerPair;

public class GuitarPositionManager
{
	/**
	 * @param fOpenStrings
	 * @return
	 */
	/*
	 * private ArrayList<ScalePosition> buildScalePosition(boolean fOpenStrings)
	 * { ArrayList<ScalePosition> scalePositions = null; scalePositions = new
	 * ArrayList<ScalePosition>(); if (fOpenStrings) {
	 * scalePositions.addAll(arOpenStringPositions);
	 * scalePositions.addAll(arPositions); } else {
	 * scalePositions.addAll(arPositions);
	 * scalePositions.addAll(arOpenStringPositions); } return scalePositions; }
	 */

	// TODO: This is horrible! find a better way when you dont have to go to the
	// bathroom
	// so bad.
	class PositionMetadata
	{
		public int	FingersMatched;

		public int	FretDifference;

		public PositionMetadata(final int fingersMatched,
								final int fretDifference)
		{
			FingersMatched = fingersMatched;
			FretDifference = fretDifference;
		}
	}

	// TODO: Should not be a const!!!
	final int						MAX_FRET_DISTANCE			=
																		4;
	static ArrayList<ScalePosition>	arPositions					=
																		new ArrayList<ScalePosition>();
	static ArrayList<ScalePosition>	arOpenStringPositions		=
																		new ArrayList<ScalePosition>();
	static int[][][]				CHORD_HAND_POSITIONS		=
																		{
																		// Just
																		// the D
																		// chord.
																		{
			{ -1, -1, -1, -1, -1 }, { -1, -1, -1, -1, -1 },
			{ 0, -1, -1, -1, -1 }, { -1, -1, 1, -1, -1 },
			{ -1, -1, -1, 3, -1 }, { -1, -1, 2, -1, -1 }				}
																		// {{
																		// 0,-1,
																		// -1,
																		// 3,
																		// -1},
																		// {-1,
																		// -1,
																		// 2,
																		// -1,-1},
																		// {0,
																		// -1,
																		// 3,
																		// -1,
																		// -1},
																		// {0,
																		// 1, 1,
																		// -1,-1},
																		// {0,
																		// -1,
																		// -1,
																		// 3,
																		// -1},
																		// {0,
																		// -1,
																		// 2, 4,
																		// -1}}
																		};

	static int[][][]				OPEN_STRING_HAND_POSITIONS	=
																		{
			{ { 0, 1, -1, 3, -1 }, { 0, -1, 2, 3, -1 },
			{ 0, -1, 2, 3, -1 }, { 0, -1, 2, -1, -1 },
			{ 0, 1, -1, 3, -1 }, { 0, 1, -1, 3, -1 } },
			{ { 0, -1, 2, -1, 4 }, { -1, 1, 2, -1, 4 },
			{ -1, 1, 2, -1, 4 }, { -1, 1, -1, 3, 4 },
			{ -1, -1, 2, -1, 4 }, { 0, -1, 2, -1, 4 }, },
			{ { 0, -1, 2, -1, 4 }, { 0, -1, 2, -1, 4 },
			{ 0, -1, 2, -1, 4 }, { -1, 1, 2, -1, 4 },
			{ -1, -1, 2, 3, -1 }, { 0, -1, 2, -1, 4 }, },
			{ { -1, 1, -1, 3, 4 }, { -1, 1, -1, 3, 4 },
			{ -1, 1, -1, 3, -1 }, { 0, 1, -1, 3, -1 },
			{ -1, 1, 2, -1, 4 }, { -1, 1, -1, 3, 4 }, },
			{ { -1, 1, -1, 3, 4 }, { -1, 1, -1, 3, -1 },
			{ 0, 1, -1, 3, -1 }, { 0, 1, -1, 3, -1 },
			{ -1, 1, -1, 3, -1 }, { -1, 1, -1, 3, 4 }, },
			{ { 0, 1, -1, 3, -1 }, { 0, 1, -1, 3, -1 },
			{ 0, -1, 2, 3, -1 }, { 0, -1, 2, 3, -1 },
			{ -1, 1, -1, 3, -1 }, { 0, 1, -1, 3, -1 }, },
			{ { 0, -1, 2, -1, 4 }, { 0, -1, 2, -1, 4 },
			{ -1, 1, 2, -1, 4 }, { -1, 1, 2, -1, 4 },
			{ -1, 1, -1, 3, -1 }, { 0, -1, 2, -1, 4 } },				};

	static int[][][]				HAND_POSITIONS				=
																		{
			{ { 1, -1, 2, -1, 4 }, { -1, 1, 2, -1, 4 },
			{ -1, 1, 2, -1, 4 }, { -1, 1, -1, 3, 4 },
			{ -1, -1, 2, -1, 4 }, { 1, -1, 2, -1, 4 }, },
			{ { 1, -1, 2, -1, 4 }, { 1, -1, 2, -1, 4 },
			{ 1, -1, 2, -1, 4 }, { -1, 1, 2, -1, 4 },
			{ -1, -1, 2, 3, -1 }, { 1, -1, 2, -1, 4 }, },
			{ { -1, 1, -1, 3, 4 }, { -1, 1, -1, 3, 4 },
			{ -1, 1, -1, 3, -1 }, { 1, 2, -1, 4, -1 },
			{ -1, 1, 2, -1, 4 }, { -1, 1, -1, 3, 4 }, },
			{ { 1, 2, -1, 4, -1 }, { 1, 2, -1, 4, -1 },
			{ 1, -1, 3, 4, -1 }, { 1, -1, 3, 4, -1 },
			{ -1, 2, -1, 4, -1 }, { 1, 2, -1, 4, -1 }, },
			{ { 1, -1, 2, -1, 4 }, { 1, -1, 2, -1, 4 },
			{ -1, 1, 2, -1, 4 }, { -1, 1, 2, -1, 4 },
			{ -1, 2, -1, 4, -1 }, { 1, -1, 2, -1, 4 }, },
			{ { -1, 1, -1, 3, 4 }, { -1, 1, -1, 3, -1 },
			{ 1, 2, -1, 4, -1 }, { 1, 2, -1, 4, -1 },
			{ -1, 1, -1, 3, -1 }, { -1, 1, -1, 3, 4 }, },
			{ { 1, 2, -1, 4, -1 }, { 1, -1, 3, 4, -1 },
			{ 1, -1, 3, 4, -1 }, { 1, -1, 3, -1, -1 },
			{ 1, 2, -1, 4, -1 }, { 1, 2, -1, 4, -1 }, },				};

	static
	{
		/* Path for the D chord!!! */
		// for (int[][] position : CHORD_HAND_POSITIONS)
		// {
		// arOpenStringPositions.add(new ScalePosition(position, 5));
		// }
		/* Path for the D chord!!! */
		for (int[][] position : HAND_POSITIONS)
		{
			arPositions.add(new ScalePosition(position, 5));
		}
		for (int[][] position : OPEN_STRING_HAND_POSITIONS)
		{
			arOpenStringPositions
					.add(new ScalePosition(position, 5));
		}

		// // Get the diatonic scale intervals.
		// int[] arIntervals =
		// ServiceProvider.
		// getInstance().
		// getKeyConverter().
		// getConverter(EKeyConverterType.Major).
		// getIntervals();
		//
		// // Create the halftone interval array.
		// int[] arHalfToneIntervalArray = new int[arIntervals.length];
		//
		// // Go through the intervals.
		// for (int nCurrentInterval = 0;
		// nCurrentInterval < arIntervals.length;
		// nCurrentInterval++)
		// {
		// // Fill the halftone interval array.
		// arHalfToneIntervalArray[nCurrentInterval] =
		// ((arIntervals[(nCurrentInterval + 1) % arIntervals.length] -
		// arIntervals[nCurrentInterval % arIntervals.length]) +
		// Notes.NumberOfNotes) %
		// Notes.NumberOfNotes;
		// }
		//
		// // Go through the intervals.
		// for (int nCurrentInterval = 0;
		// nCurrentInterval < arIntervals.length;
		// nCurrentInterval++)
		// {
		// // Build a sub interval list.
		// int nSubIntervalIndex = nCurrentInterval;
		// do
		// {
		// // Build the sub interval array.
		// int[] arSubIntervalArray = new int[arIntervals.length];
		//
		// int nInsertedIndex = 0;
		// for (int nCurrentSubIntervalIndex = 0;
		// nCurrentSubIntervalIndex < arIntervals.length;
		// nCurrentSubIntervalIndex++)
		// {
		//
		// int nInterval =
		// arHalfToneIntervalArray[(nCurrentSubIntervalIndex + nCurrentInterval)
		// %
		// arHalfToneIntervalArray.length];
		//
		// arSubIntervalArray[nInsertedIndex] = nInterval;
		// nInsertedIndex++;
		// }
		//
		// // Create a scale position.
		// arPositions.add(new ScalePosition(arSubIntervalArray));
		//
		// nSubIntervalIndex++;
		// }while((nSubIntervalIndex % arIntervals.length) == nCurrentInterval);
		// }
	}

	/**
	 * Build the position map.
	 * 
	 * @param arPositionsClone
	 *            - The positions to set fingers for.
	 * @param fretRange
	 *            - The current fret range measured.
	 * @param maxFretDistance
	 *            - The max distance between frets in this position.
	 * @param fOpenStrings
	 *            - Are there any open strings, requested.
	 * @param lastPosition
	 *            - The last position that was used.
	 * @return
	 */
	private ArrayList<IHandPositioning> buildPositionMap(	final ArrayList<FingerPosition> arPositionsClone,
															final int fretRange,
															final boolean fOpenStrings,
															final int lastPosition)
	{
		PositionMetadata bestMatch =
				new PositionMetadata(0, Integer.MAX_VALUE);
		List<List<FingerPosition>> fingers =
				new ArrayList<List<FingerPosition>>(4);

		ArrayList<IHandPositioning> positionMaps =
				new ArrayList<IHandPositioning>();

		if (fOpenStrings)
		{
			// Go through the open string scale positions to find a fitting
			// position.
			for (ScalePosition scalePosition : arOpenStringPositions)
			{
				matchForScale(arPositionsClone, bestMatch,
						fingers, positionMaps, scalePosition, 0);
			}
			cleanPositions(positionMaps);

		}
		else
		{
			// Go through all positions.
			for (int fretIndent = 0; fretIndent < ((MAX_FRET_DISTANCE - fretRange) + 1); fretIndent++)
			{
				// Check the current position.
				matchForScale(arPositionsClone, bestMatch,
						fingers, positionMaps, arPositions
								.get(lastPosition), fretIndent);

				int positionUpOne = lastPosition;
				int positionDownOne = lastPosition;

				while ((positionDownOne >= 0)
						|| (positionUpOne < arPositions.size()))
				{
					// Check the next position.
					positionUpOne++;
					if (positionUpOne < arPositions.size())
					{
						matchForScale(arPositionsClone,
								bestMatch, fingers,
								positionMaps, arPositions
										.get(positionUpOne),
								fretIndent);
					}

					// Check the previous position.
					positionDownOne--;
					if (positionDownOne >= 0)
					{
						matchForScale(arPositionsClone,
								bestMatch, fingers,
								positionMaps, arPositions
										.get(positionDownOne),
								fretIndent);
					}
				}
			}

		}

		return positionMaps;
	}

	/**
	 * Clean the fingers generated from the open string positions.
	 * 
	 * @param positionMaps
	 */
	private void cleanPositions(final ArrayList<IHandPositioning> positionMaps)
	{
		for (IHandPositioning iHandPositioning : positionMaps)
		{
			int fingersPressed = 0;
			boolean firstFingerUsed = false;
			for (FretFingerPairBase fretFingerPair : iHandPositioning
					.getFingerPositions())
			{
				if (fretFingerPair.getFinger() == 1)
				{
					firstFingerUsed = true;
					break;
				}

				if (fretFingerPair.getString() != 0)
				{
					fingersPressed++;
				}
			}

			if (firstFingerUsed)
			{
				continue;
			}

			if (fingersPressed == 3)
			{
				for (FretFingerPairBase fretFingerPair : iHandPositioning
						.getFingerPositions())
				{
					FretFingerPair pair =
							(FretFingerPair) fretFingerPair;
					pair.Finger--;
				}
			}
		}
	}

	private ArrayList<FretFingerPairBase> createHandPositioning(final List<List<FingerPosition>> allFingers)
	{
		ArrayList<FretFingerPairBase> result =
				new ArrayList<FretFingerPairBase>();

		// int numberOfFingersNeeded = 0;
		// for (List<FingerPosition> lstPosition : allFingers)
		// {
		// numberOfFingersNeeded += lstPosition.size();
		// }
		// boolean isCapoNeeded = (numberOfFingersNeeded > 4);

		// Number of fingers.
		for (int i = 0; i < 4; i++)
		{
			FingerPosition best = null;
			for (int j = 0; j < allFingers.get(i).size(); j++)
			{
				FingerPosition pos = allFingers.get(i).get(j);

				if (pos.Fret == 0)
				{
					// Put the position.
					result.add(new FretFingerPair(0, pos));

					// Remove the best.
					allFingers.get(i).remove(pos);

					j--;
					continue;
				}

				// Get the best pos.
				if (best == null)
				{
					best = pos;
					continue;
				}

				// Check if this position is better.
				if (best.Fret > pos.Fret)
				{
					// Switch.
					best = pos;
				}
				else if ((best.Fret == pos.Fret)
						&& (best.String > pos.String))
				{
					// Switch.
					best = pos;
				}
			}

			if (best != null)
			{
				// Put the position.
				result.add(new FretFingerPair(i + 1, best));

				// Remove the best.
				allFingers.get(i).remove(best);

				if (i < 3)
				{
					// Add all the others to the next.
					allFingers.get(i + 1).addAll(
							allFingers.get(i));
				}
			}
		}

		return result;
	}

	/**
	 * Get the fingers to play the requested positions.
	 * 
	 * @param fingerPositions
	 *            - Positions to play.
	 * @param lastPosition
	 *            - The last position that was played.
	 * @return Map of positions and fingers.
	 */
	public ArrayList<IHandPositioning> getFingersForPostions(	final ArrayList<FingerPosition> fingerPositions,
																final int lastPosition)
	{
		ArrayList<FingerPosition> arPositionsClone =
				new ArrayList<FingerPosition>(fingerPositions);

		Pair<Integer, Boolean> fretRangeAndIsOpen =
				orderPositions(arPositionsClone);

		// Validate the last position received.
		if ((lastPosition < 0)
				|| (lastPosition >= arPositions.size()))
		{
			return null;
		}

		// If to far, just abort.
		if (fretRangeAndIsOpen.first > MAX_FRET_DISTANCE)
		{
			return null;
		}

		return buildPositionMap(arPositionsClone,
				fretRangeAndIsOpen.first,
				fretRangeAndIsOpen.second, lastPosition);

	}

	/**
	 * Match fingers to array of positions.
	 * 
	 * @param arPositionsClone
	 * @param fingers
	 * @param scalePosition
	 * @param fingersUsed
	 * @param fingersMatched
	 * @param openStringPlayed
	 * @return
	 */
	private PositionMetadata matchFingers(	final ArrayList<FingerPosition> arPositionsClone,
											final List<List<FingerPosition>> allFingers,
											final ScalePosition scalePosition,
											final int fretIndent)
	{
		int fingersMatched = 0;
		int stringPlayed = -1;
		boolean openString = false;
		int minFret = FretFingerPair.NUM_FRETS;
		int maxFret = 0;

		// Go through the received positions.
		for (FingerPosition fingerPosition : arPositionsClone)
		{
			openString = false;
			if (fingerPosition.Fret == 0)
			{
				openString = true;
			}

			if (stringPlayed == fingerPosition.String)
			{
				continue;
			}
			else
			{
				stringPlayed = fingerPosition.String;
			}

			if ((fingerPosition.Fret != 0)
					&& (fingerPosition.Fret < fretIndent))
			{
				continue;
			}

			if ((fingerPosition.String < 0)
					|| (fingerPosition.Fret > FretFingerPair.NUM_FRETS))
			{
				continue;
				// TODO: Critical error.
			}

			int fingerIndex = 0;
			if (!openString)
			{
				// Remove the indent.
				FingerPosition tempPos =
						new FingerPosition(fingerPosition);
				tempPos.Fret -= fretIndent;
				fingerIndex = scalePosition.getIndex(tempPos);

				if ((fingerIndex == 0) && (fretIndent > 0))
				{
					fingerIndex = 1;
				}
			}

			// If found.
			// TODO: Make -1 const.
			if (fingerIndex != -1)
			{
				fingersMatched++;
				int fingerIndexToAdd =
						(fingerIndex == 0) ? (0)
								: (fingerIndex - 1);

				allFingers.get(fingerIndexToAdd).add(
						fingerPosition);
			}

			// Check if the finger was already used.
			// if(fingerIndex == 0 || fingersUsed[fingerIndex] == null)
			// {
			// FretFingerPair toAdd =
			// new FretFingerPair( fingerIndex,
			// fingerPosition.String,
			// fingerPosition.Fret);
			// fingers.add(toAdd);
			// fingersMatched++;
			//
			// // Signal that the finger is used.
			// fingersUsed[fingerIndex] = toAdd;
			//
			if (fingerPosition.Fret > 0)
			{
				if (maxFret < fingerPosition.Fret)
				{
					maxFret = fingerPosition.Fret;
				}

				if (minFret > fingerPosition.Fret)
				{
					minFret = fingerPosition.Fret;
				}
			}
			// }
			// }
		}
		return new PositionMetadata(fingersMatched, maxFret
				- minFret);
	}

	/**
	 * @param arPositionsClone
	 * @param bestMatch
	 * @param fingers
	 * @param positionMaps
	 * @param scalePosition
	 * @param fretIndent
	 */
	private void matchForScale(	final ArrayList<FingerPosition> arPositionsClone,
								final PositionMetadata bestMatch,
								final List<List<FingerPosition>> allFingers,
								final ArrayList<IHandPositioning> positionMaps,
								final ScalePosition scalePosition,
								final int fretIndent)
	{
		allFingers.clear();
		for (int i = 0; i < 4; i++)
		{
			allFingers.add(new ArrayList<FingerPosition>());
		}

		PositionMetadata metadata =
				matchFingers(arPositionsClone, allFingers,
						scalePosition, fretIndent);

		// Now create the positioning.
		ArrayList<FretFingerPairBase> fingers =
				createHandPositioning(allFingers);

		// Check if matched the same number.
		if (bestMatch.FingersMatched == metadata.FingersMatched)
		{
			if (bestMatch.FretDifference == metadata.FretDifference)
			{
				BasicHandPositioning toAdd =
						new BasicHandPositioning(fingers);

				// if(!positionMaps.contains(toAdd))
				{
					// Add the current position.
					positionMaps.add(toAdd);
				}
			}
			else if (bestMatch.FretDifference > metadata.FretDifference)
			{
				bestMatch.FretDifference =
						metadata.FretDifference;

				// Empty the array.
				positionMaps.clear();

				// Add the current position.
				positionMaps
						.add(new BasicHandPositioning(fingers));
			}
		}
		// Check if the fingers matched are better.
		else if (metadata.FingersMatched > bestMatch.FingersMatched)
		{
			// Empty the array.
			positionMaps.clear();

			// Add the current position.
			positionMaps.add(new BasicHandPositioning(fingers));

			bestMatch.FingersMatched = metadata.FingersMatched;
			bestMatch.FretDifference = metadata.FretDifference;
		}
	}

	/**
	 * Order the incoming positions before matching fingers.
	 * 
	 * @param arPositionsClone
	 *            - The array of finger position.
	 * @return Metadata to return, indication to what the fret range is and to
	 *         weather we need to play any open strings.
	 */
	private Pair<Integer, Boolean> orderPositions(final ArrayList<FingerPosition> arPositionsClone)
	{
		Boolean fIsOpen = false;
		Integer fretRange = Integer.MAX_VALUE;
		while (arPositionsClone.size() > 0)
		{
			// Find the largest received fret, and smallest.
			int minFret = FretFingerPair.NUM_FRETS, maxFret = -1;
			int lastMinFret = 0, lastMaxFret = 0;
			int currentString = -1;

			// Go through the keys.
			for (FingerPosition fingerPosition : arPositionsClone)
			{
				if (fingerPosition.String == currentString)
				{
					continue;
				}

				currentString = fingerPosition.String;

				if (fingerPosition.Fret == 0)
				{
					fIsOpen = true;
					continue;
				}

				if (fingerPosition.Fret > maxFret)
				{
					lastMaxFret = maxFret;
					maxFret = fingerPosition.Fret;
				}
				else
				{
					lastMaxFret = maxFret;
				}
				if ((fingerPosition.Fret > 0)
						&& (fingerPosition.Fret < minFret))
				{
					lastMinFret = minFret;
					minFret = fingerPosition.Fret;
				}
				else
				{
					lastMinFret = minFret;
				}
			}

			fretRange = maxFret - minFret;

			if (fretRange <= MAX_FRET_DISTANCE)
			{
				break;
			}

			minFret = lastMinFret;
			maxFret = lastMaxFret;
			arPositionsClone.remove(arPositionsClone.size() - 1);
		}

		return new Pair<Integer, Boolean>(fretRange, fIsOpen);
	}
}
