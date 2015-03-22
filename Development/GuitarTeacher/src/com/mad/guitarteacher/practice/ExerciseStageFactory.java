package com.mad.guitarteacher.practice;

import java.util.HashMap;

import com.mad.guitarteacher.practice.Scenarios.BarreScenario;
import com.mad.guitarteacher.practice.Scenarios.ExerciseScenario;
import com.mad.guitarteacher.practice.Scenarios.FingerPickingScenario;
import com.mad.guitarteacher.practice.Scenarios.IntroductionScenario;
import com.mad.guitarteacher.practice.Scenarios.PositionScenario;
import com.mad.guitarteacher.practice.Scenarios.RhythmScenario;
import com.mad.guitarteacher.practice.Scenarios.SingleChordPlayingScenario;
import com.mad.guitarteacher.practice.Scenarios.TuningScenario;
import com.mad.lib.utils.HelperMethods;
import com.mad.lib.utils.ErrorHandler;

/**
 * A factory for creating scenarios based on ExerciseTypes.
 * 
 * @author TomF
 * 
 *         This class is a factory for exercise stages.
 */
public class ExerciseStageFactory
{
	/**
	 * Holds all the types of exercise stages.
	 */
	static HashMap<ExerciseTypes, Class<?>>	s_arClasses	= null;

	/**
	 * A static ctor.
	 * 
	 * We can trust this ctor to conclude that the members were created.
	 */
	static
	{
		s_arClasses = new HashMap<ExerciseTypes, Class<?>>();

		new BarreScenario();
		new FingerPickingScenario();
		new SingleChordPlayingScenario();
		new PositionScenario();
		new RhythmScenario();
		new TuningScenario();
		new IntroductionScenario();
	}

	/**
	 * Get an exercise stage to run.
	 * 
	 * @param info
	 *            - Information about the stage to create.
	 * 
	 * @return ExerciseStage - Exercise stage to run.
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static ExerciseScenario getScenario(final ExerciseStageOption info)
	{

		ExerciseScenario scenario = null;

		// Not contained. We'll try to create an instance with those params.
		ExerciseTypes exerciseType = info.getParent().getType();
		// See if we have a proper scenario class.
		if (!s_arClasses.containsKey(exerciseType))
		{
			// Don't know how to create :(
			// TODO: Report error.
			return null;
		}

		// Get the class for that specific Scenario.
		Class<?> stageClass = s_arClasses.get(exerciseType);
		try
		{
			// Create and initialize the scenario.
			scenario =
					(ExerciseScenario) stageClass.newInstance();
			scenario.init(HelperMethods.parseParameters(info
					.getParam(), 1), info);
		}
		catch (Exception ex)
		{
			ErrorHandler.HandleError(ex);
			return null;
		}

		return scenario;
	}

	public static boolean register(	final ExerciseTypes eType,
									final Class<?> pExercise)
	{
		// TODO: Validate the type to.
		if (pExercise == null)
		{
			return false;
		}

		// Check if already exists, and with the same type.
		if (s_arClasses.containsKey(eType))
		{
			return true;
		}

		// Add it.
		s_arClasses.put(eType, pExercise);

		return true;
	}
}
