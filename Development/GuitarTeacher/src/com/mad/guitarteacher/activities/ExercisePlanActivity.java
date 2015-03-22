package com.mad.guitarteacher.activities;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mad.guitarteacher.R;
import com.mad.guitarteacher.display.graphics.animation.IShowHideDrawable;
import com.mad.guitarteacher.display.graphics.animation.OnShownListener;
import com.mad.guitarteacher.display.graphics.popups.PlanDonePopup;
import com.mad.guitarteacher.display.views.GameSurfaceView;
import com.mad.guitarteacher.display.views.PausingMenuManager;
import com.mad.guitarteacher.practice.ExercisePlan;
import com.mad.guitarteacher.practice.ExerciseStage;
import com.mad.guitarteacher.practice.ExerciseStageOption;
import com.mad.guitarteacher.practice.ExercisesManager;
import com.mad.guitarteacher.services.IGameManager;
import com.mad.guitarteacher.utils.Definitions;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.lib.activities.LayoutActivityBase;
import com.mad.lib.display.graphics.GraphicsPoint;
import com.mad.lib.display.utils.DisplayHelper;
import com.mad.lib.utils.ErrorHandler;

/**
 * Our main activity. Displays the main menu.
 * 
 * @author Nati
 * 
 */
public class ExercisePlanActivity extends LayoutActivityBase
{
	private class PlanAdapter extends
			ArrayAdapter<ExerciseStageOption>
	{
		private final static int	TIME_BETWEEN_ANIMATIONS	=
																	100;
		private final static int	TIME_ANIMATE_IN			=
																	1000;
		private final Bitmap		m_bmpChecked;
		private final Bitmap		m_bmpUnchecked;
		private final ExercisePlan	m_Plan;
		private OnShownListener		m_listenerOnShown;

		public PlanAdapter(	final Context context,
							final ExercisePlan plan)
		{
			super(	context,
					R.layout.exercise_plan_list_item,
					R.id.name,
					plan);
			m_Plan = plan;

			m_bmpChecked =
					DisplayHelper
							.getScaledBitmap(
									context.getResources(),
									R.drawable.exerciseplan_checkbox_checked,
									true);

			m_bmpUnchecked =
					DisplayHelper
							.getScaledBitmap(
									context.getResources(),
									R.drawable.exerciseplan_checkbox_unchecked,
									true);

		}

		@Override
		public View getView(final int position,
							final View convertView,
							final ViewGroup parent)
		{
			View view = null;
			if (convertView != null)
			{
				view = convertView;
			}
			else
			{
				view =
						super.getView(position, convertView,
								parent);

				view.setOnClickListener(null);
				view.setLongClickable(false);
				view.setClickable(false);
				ImageView viewCheckbox =
						(ImageView) view
								.findViewById(R.id.checkbox);
				Bitmap bmpCurrent;
				if (position < m_Plan
						.getCurrentStageOptionIndex())
				{
					bmpCurrent = m_bmpChecked;
				}
				else
				{
					bmpCurrent = m_bmpUnchecked;
				}

				viewCheckbox.setImageBitmap(bmpCurrent);

				startAnimation(position, view);
			}
			return view;
		}

		public void setIsShownListener(OnShownListener listener)
		{
			m_listenerOnShown = listener;
		}

		@Override
		public boolean isEnabled(final int position)
		{
			return false;
		}

		/**
		 * @param position
		 * @param view
		 */
		private void startAnimation(final int position,
									final View view)
		{
			// Add the two animations.
			TranslateAnimation anim =
					new TranslateAnimation(	TranslateAnimation.RELATIVE_TO_SELF,
											-1.0f,
											TranslateAnimation.RELATIVE_TO_SELF,
											0.0f,
											TranslateAnimation.RELATIVE_TO_SELF,
											0.0f,
											TranslateAnimation.RELATIVE_TO_SELF,
											0.0f);

			anim.setDuration(TIME_ANIMATE_IN);
			anim.setFillAfter(true);
			anim.setStartOffset(TIME_BETWEEN_ANIMATIONS
					* position);
			anim.setAnimationListener(new AnimationListener()
			{

				@Override
				public void onAnimationStart(Animation animation)
				{
				}

				@Override
				public void onAnimationRepeat(Animation animation)
				{
				}

				@Override
				public void onAnimationEnd(Animation animation)
				{
					if (position == (m_Plan.size() - 1))
					{
						if (m_listenerOnShown != null)
						{
							m_listenerOnShown.onShown(null);
						}
					}
				}
			});
			view.startAnimation(anim);
		}
	}

	private boolean						m_fTouchHandled								=
																							false;

	private ExercisePlan				m_Plan;

	final Timer							m_TimerStartActivity						=
																							new Timer();

	private OnTouchListener				m_OnTouchAnywhere							=
																							new OnTouchListener()
																							{

																								@Override
																								public boolean onTouch(	final View v,
																														final MotionEvent event)
																								{
																									if (!m_fTouchHandled)
																									{
																										moveActivity();
																										m_OnTouchAnywhere =
																												null;
																										m_fTouchHandled =
																												true;
																									}
																									return true;
																								}
																							};

	private final PausingMenuManager	m_PausingMenuManager						=
																							new PausingMenuManager();

	private final IGameManager			m_ActionsManager							=
																							new IGameManager()
																							{

																								@Override
																								public void switchToHome(Context context)
																								{
																									Intent goToNextActivity =
																											new Intent(	context.getApplicationContext(),
																														MainActivity.class);
																									goToNextActivity
																											.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
																									context.startActivity(goToNextActivity);
																								}

																								@Override
																								public void switchToExercises(Context context)
																								{
																									Intent goToNextActivity =
																											new Intent(	context.getApplicationContext(),
																														ExercisePickerActivity.class);

																									context.startActivity(goToNextActivity);
																								}

																								@Override
																								public void startScenario(	String strStageId,
																															String strExerciseOptionId,
																															int nPlanId,
																															IPlayingDisplayer displayer)
																								{
																									ErrorHandler
																											.HandleError(new UnsupportedOperationException());
																								}

																								@Override
																								public void resumeExercise(Context context)
																								{
																									ErrorHandler
																											.HandleError(new UnsupportedOperationException());
																								}

																								@Override
																								public void restartExercise(Context context)
																								{
																									ErrorHandler
																											.HandleError(new UnsupportedOperationException());
																								}

																								@Override
																								public void pauseExercise(Context context)
																								{
																									ErrorHandler
																											.HandleError(new UnsupportedOperationException());
																								}

																								@Override
																								public void onBackPressed(Context context)
																								{
																									ErrorHandler
																											.HandleError(new UnsupportedOperationException());
																								}

																								@Override
																								public void nextExercise(Context context)
																								{
																									moveActivity();
																								}

																								@Override
																								public GameState getGameState()
																								{
																									return GameState.Ended;
																								}
																							};

	private static final int			MILLIS_BEFORE_SWITCHING_TO_GAME_ACTIVITY	=
																							5 * 1000;

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		boolean fResult = super.onTouchEvent(event);
		m_PausingMenuManager.handlePausingMenuTouch(this,
				(int) event.getX(), (int) event.getY(),
				m_ActionsManager, m_PopupDrawer);
		return fResult;
	}

	private PlanAdapter		m_PlanAdapter;

	/**
	 * The view holding the list items.
	 */
	private ListView		m_viewListView;

	private TextView		m_viewTitle;

	private GameSurfaceView	m_viewGameSurfaceView;

	private PlanDonePopup	m_PopupDrawer;

	@Override
	protected View createRootView(final ViewGroup parent)
	{
		return LayoutInflater.from(this).inflate(
				R.layout.exercise_plan_activity, null);
	}

	/**
	 * @param plan
	 */
	private void moveActivity()
	{
		m_TimerStartActivity.cancel();

		if (m_Plan.isDone())
		{
			ExerciseStage currentStage =
					m_Plan.get(0).getParent();
			Intent intent =
					new Intent(	ExercisePlanActivity.this,
								ExercisePickerActivity.class);
			intent.putExtra(
					Definitions.Intents.INTENT_EXERCISE_ID,
					currentStage.getID());

			startActivity(intent);
		}
		else
		{
			ExerciseStageOption currentStageOption =
					m_Plan.getCurrentStageOption();
			Intent intent =
					new Intent(	ExercisePlanActivity.this,
								GameActivity.class);
			intent.putExtra(
					Definitions.Intents.INTENT_EXERCISE_ID,
					currentStageOption.getParent().getID());
			intent.putExtra(
					Definitions.Intents.INTENT_OPTION_ID,
					currentStageOption.getParam());
			intent.putExtra(Definitions.Intents.INTENT_PLAN_ID,
					m_Plan.getId());
			startActivity(intent);
		}
	}

	@Override
	public void onBackPressed()
	{
		// moveActivity();
		m_TimerStartActivity.cancel();
		super.onBackPressed();
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (!s_fIsManagerInitialized)
		{
			return;
		}
		Intent intent = getIntent();

		// Get the exercise plan.
		int nPlanId =
				intent.getIntExtra(
						Definitions.Intents.INTENT_PLAN_ID,
						ExercisePlan.PLAN_ID_INVALID);
		ExercisesManager manager =
				AppLibraryServiceProvider.getInstance()
						.get(R.service.exercises_manager);
		ExercisePlan plan =
				manager.getPracticeScheduler().getPlan(nPlanId);

		int nPlanAction =
				intent.getIntExtra(
						Definitions.Intents.INTENT_PLAN_ACTION,
						Definitions.Intents.PLAN_ACTION_INVALID);

		ExerciseStage stage;

		switch (nPlanAction)
		{
			case Definitions.Intents.PLAN_ACTION_EXERCISE_DONE:
			{
				if ((plan == null) || (plan.size() == 0))
				{
					ErrorHandler
							.HandleError(new IllegalArgumentException());
				}
				// Advance to the next stageOption.
				plan.advanceToNextStage();
				if (plan.isDone())
				{
					stage = plan.get(0).getParent();
				}
				else
				{
					stage =
							plan.getCurrentStageOption()
									.getParent();
				}
				break;
			}
			case Definitions.Intents.PLAN_ACTION_STAGE_PLAN_START:
			{
				String strStageId =
						intent.getStringExtra(Definitions.Intents.INTENT_EXERCISE_ID);
				stage = manager.getStage(strStageId);
				plan =
						manager.getPracticeScheduler()
								.createPlan(stage);
				break;
			}
			case Definitions.Intents.PLAN_ACTION_GENERAL_PLAN_START:
			{
				plan =
						manager.getPracticeScheduler()
								.createPlan(null);
				if (plan == null)
				{
					ErrorHandler
							.HandleError(new NullPointerException());
					return;
				}
				stage = plan.getCurrentStageOption().getParent();
				break;
			}
			case Definitions.Intents.PLAN_ACTION_INVALID:
			default:
			{
				ErrorHandler
						.HandleError(new IllegalStateException());
				return;
			}
		}

		if (plan == null)
		{
			ErrorHandler.HandleError(new NullPointerException());
			return;
		}

		m_viewListView = (ListView) findViewById(R.id.main_list);
		m_viewListView.setOnItemClickListener(null);
		m_viewTitle = (TextView) findViewById(R.id.title);
		m_viewListView.setOnTouchListener(m_OnTouchAnywhere);
		m_viewGameSurfaceView =
				(GameSurfaceView) findViewById(R.id.popup);
		GraphicsPoint pntDimensions =
				GraphicsPoint.getDimensions();
		m_PopupDrawer =
				new PlanDonePopup(	this,
									pntDimensions.x / 2,
									pntDimensions.y / 2,
									m_PausingMenuManager,
									m_ActionsManager);
		m_viewGameSurfaceView.setDrawingManager(m_PopupDrawer);
		m_viewGameSurfaceView.setZOrderOnTop(true); // necessary
		SurfaceHolder sfhTrackHolder =
				m_viewGameSurfaceView.getHolder();
		sfhTrackHolder.setFormat(PixelFormat.TRANSPARENT);
		((ImageView) findViewById(R.id.header_bg))
				.setImageBitmap(DisplayHelper.getStrechedBitmap(
						getResources(),
						R.drawable.plan_header_bg));

		if (stage == null)
		{
			stage = plan.get(0).getParent();
		}

		final ExerciseStage stageAsFinal = stage;
		m_PlanAdapter = new PlanAdapter(this, plan);
		m_Plan = plan;
		if (m_Plan.isDone())
		{
			// Calculate the stage score.

			m_PlanAdapter
					.setIsShownListener(new OnShownListener()
					{

						@Override
						public void onShown(IShowHideDrawable drawable)
						{
							m_viewGameSurfaceView
									.setVisibility(View.VISIBLE);
							m_PopupDrawer.show(stageAsFinal
									.getName(), stageAsFinal
									.getMaxScore(), true);
						}
					});
		}
		else
		{
			getRootView().setOnTouchListener(m_OnTouchAnywhere);

			// If the time passes, move to game activity.
			m_TimerStartActivity.schedule(new TimerTask()
			{

				@Override
				public void run()
				{
					moveActivity();

				}

			}, MILLIS_BEFORE_SWITCHING_TO_GAME_ACTIVITY);
		}
		m_viewTitle.setText(stage.getName());

		m_viewListView.setAdapter(m_PlanAdapter);

	}
}
