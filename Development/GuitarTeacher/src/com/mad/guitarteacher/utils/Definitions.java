package com.mad.guitarteacher.utils;

import com.mad.guitarteacher.practice.Acts.ActBase;
import com.mad.lib.display.graphics.GraphicsPoint;

public class Definitions
{
	public static class Intents
	{
		public static final String	INTENT_EXERCISE_ID				=
																			"mad.views.exercise_id";
		public static final String	INTENT_PLAN_ID					=
																			"mad.views.plan_id";
		public static final String	INTENT_PLAN_ACTION				=
																			"mad.views.plan_action";
		public static final String	INTENT_OPTION_ID				=
																			"mad.views.option";

		public static final int		PLAN_ACTION_INVALID				=
																			0;
		public static final int		PLAN_ACTION_EXERCISE_DONE		=
																			1;
		public static final int		PLAN_ACTION_STAGE_PLAN_START	=
																			2;
		public static final int		PLAN_ACTION_GENERAL_PLAN_START	=
																			3;
	}

	public static class Dimensions
	{
		public static class MainActivity
		{
			public static GraphicsPoint	PROFILE_PICTURE	=
																new GraphicsPoint(	59f,
																					56f);
		}
	}

	public static class DebugFlags
	{
		public final static boolean	DEBUG_COMPARE_TUNING_PASSES	=
																		false;
		public final static boolean	DEBUG_ALLOW_CHOOSE_OPTION	=
																		false;

	}

	public static class Scores
	{
		public static final float	THRESHOLD_ONE_STAR		=
																	0.3f;
		public static final float	THRESHOLD_TWO_STARS		=
																	0.5f;
		public static final float	THRESHOLD_THREE_STARS	=
																	0.85f;

		public static int getStars(int nScore)
		{
			float sfScore = (1.0f * nScore) / ActBase.MAX_SCORE;
			int nStars = 0;
			if (sfScore > Definitions.Scores.THRESHOLD_THREE_STARS)
			{
				nStars = 3;
			}
			else if (sfScore > Definitions.Scores.THRESHOLD_TWO_STARS)
			{
				nStars = 2;
			}
			else if (sfScore > Definitions.Scores.THRESHOLD_ONE_STAR)
			{
				nStars = 1;
			}
			return nStars;
		}
	}

	public final static int		MILLISECONDS_IN_MINUTE					=
																				60 * 1000;

	// Storable files
	public final static String	STORABLE_FILE_LEVELS					=
																				"Levels.json";
	public final static String	STORABLE_FILE_GENERAL_USER_INFORMATION	=
																				"UserInfo.json";

	public final static String	STORABLE_FILE_USER_HISTORY_INFORMATION	=
																				"History.json";

}
