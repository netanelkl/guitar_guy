package com.mad.guitarteacher.activities;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.mad.guitarteacher.R;
import com.mad.guitarteacher.activities.views.FieldsBaseAdapter;
import com.mad.guitarteacher.activities.views.ItemAnimatableBaseAdapter;
import com.mad.guitarteacher.activities.views.OptionsBaseAdapter;
import com.mad.guitarteacher.activities.views.StagesBaseAdapter;
import com.mad.guitarteacher.practice.ExerciseStage;
import com.mad.guitarteacher.practice.ExerciseStageOption;
import com.mad.guitarteacher.practice.ExercisesManager;
import com.mad.guitarteacher.practice.IReadOnlyExerciseField;
import com.mad.guitarteacher.practice.IReadOnlyExerciseStage;
import com.mad.guitarteacher.utils.Definitions;
import com.mad.lib.activities.LayoutActivityBase;
import com.mad.lib.display.utils.DisplayHelper;
import com.mad.lib.display.utils.HorizontalListView;
import com.mad.lib.utils.AppLibraryServiceProvider;

/**
 * This screen is responsible for picking the current exercise stage to be
 * played.
 * 
 * It's designed to have a few BaseAdapters for the different hierarchy levels
 * in the game (Fields->Stages->Options). All three: FieldsBaseAdapter
 * StagesBaseAdapter, and OptionsBaseAdapter extend ItemAnimatableBaseAdapter
 * which provides the base for drawing those rectangles, managing their
 * animation, on their appearance/disappearance.
 * 
 */
public class ExercisePickerActivity extends LayoutActivityBase
{
	private static final float	TEXT_HEIGHT_OF_BAR	= 0.7f;

	/**
	 * An event listener that notifies when the list disappeared.
	 * 
	 * @author Nati
	 * 
	 */
	public interface OnListGoneListener
	{
		/**
		 * a const indicating that no item was selected.
		 */
		public final static int	NONE_SELECTED	= -1;

		/**
		 * Notification for a list gone event.
		 * 
		 * @param nItemIndex
		 *            the index of the focused (clicked) index of the item.
		 */
		public void onListGone(int nItemIndex);
	}

	private final Runnable						m_onHidingStartedListener	=
																					new Runnable()
																					{

																						@Override
																						public void run()
																						{
																							hideTopBar();
																						}
																					};

	private final static int					TOP_BAR_ANIMATION_DURATION	=
																					500;

	private TextView							m_txtTopInformationBar;

	private View								m_viewTopBar;

	private ImageView							m_viewTopBarImg;
	/**
	 * The current adapter.
	 */
	private ItemAnimatableBaseAdapter			m_Adapter;

	/**
	 * The current field in our hierarchy. Can be null, in which case we are in
	 * the field choosing stage.
	 */
	private IReadOnlyExerciseField				m_CurrentField;

	/**
	 * The current stage in our heirarchy. Can be null, in which case we are
	 * above the level of option picking.
	 */
	private IReadOnlyExerciseStage				m_CurrentStage;

	/**
	 * The main field list to show.
	 */
	private ArrayList<IReadOnlyExerciseField>	m_Fields;

	/**
	 * The view holding the list items.
	 */
	private HorizontalListView					m_viewListView;

	@Override
	protected View createRootView(final ViewGroup parent)
	{
		return LayoutInflater.from(this).inflate(
				R.layout.exercise_picker_activity, null);

	}

	void hideTopBar()
	{
		TranslateAnimation animation =
				new TranslateAnimation(0, 0, 0, -m_viewTopBar
						.getHeight());
		animation.setFillAfter(true);
		animation.setDuration(TOP_BAR_ANIMATION_DURATION);
		m_viewTopBar.startAnimation(animation);
	}

	@Override
	public void onBackPressed()
	{
		// Hide the current adapter. In turn, it will know which stage to
		// progress to.
		m_Adapter.hide();

	}

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (!s_fIsManagerInitialized)
		{
			return;
		}

		// Get the fields that will populate our view.
		ExercisesManager exercisesManager =
				AppLibraryServiceProvider.getInstance().get(
						R.service.exercises_manager);
		ArrayList<IReadOnlyExerciseField> fields =
				exercisesManager.getLevelMap().getFields();
		m_Fields = fields;

		// See if we came back from an exercise, in that case, show that
		// exercise in the list.
		Intent intent = getIntent();

		// Get the exercise plan.
		String strStageId =
				intent.getStringExtra(Definitions.Intents.INTENT_EXERCISE_ID);

		if (strStageId != null)
		{
			ExercisesManager manager =
					AppLibraryServiceProvider.getInstance().get(
							R.service.exercises_manager);
			ExerciseStage stage = manager.getStage(strStageId);
			if (stage != null)
			{
				m_CurrentField = stage.getParent();

				// TODO: scroll so that the stage is in the center.
			}
		}

		m_viewListView =
				(HorizontalListView) findViewById(R.id.listview);
		m_txtTopInformationBar =
				(TextView) findViewById(R.id.txt_bar_instruction);
		m_viewTopBar = findViewById(R.id.top_info_bar);
		m_viewTopBarImg =
				(ImageView) findViewById(R.id.top_info_bar_img);

		// m_txtTopInformationBar.setMaxWidth()

		Bitmap scaledBitmap =
				DisplayHelper.getScaledBitmap(getResources(),
						R.drawable.exercises_picker_top_bar,
						true);
		m_txtTopInformationBar.setWidth(scaledBitmap.getWidth());
		m_txtTopInformationBar.setHeight((int) (scaledBitmap
				.getHeight() * TEXT_HEIGHT_OF_BAR));
		m_viewTopBarImg.setImageBitmap(scaledBitmap);
		DisplayHelper.scaleMargin(m_viewTopBarImg);
	}

	@Override
	protected void onResume()
	{
		// Load the items in the current state.
		if (m_CurrentStage == null)
		{
			if (m_CurrentField == null)
			{
				switchToFields();
			}
			else
			{
				switchToStages(m_CurrentField);
			}
		}
		else
		{
			switchToOptions(m_CurrentStage);
		}
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		m_viewListView.setAdapter(null);
	}

	void showTopBar()
	{
		TranslateAnimation animation =
				new TranslateAnimation(0, 0, -m_viewTopBar
						.getHeight(), 0);
		animation.setFillAfter(true);
		animation.setDuration(TOP_BAR_ANIMATION_DURATION);
		m_viewTopBar.startAnimation(animation);
	}

	/**
	 * Show the list of fields on the display.
	 */
	private void switchToFields()
	{
		ItemAnimatableBaseAdapter adapter =
				m_Adapter = new FieldsBaseAdapter(m_Fields);
		adapter.setOnHidingStartedListener(m_onHidingStartedListener);
		adapter.setOnGoneListener(new OnListGoneListener()
		{

			@Override
			public void onListGone(final int nCurrentField)
			{
				if (nCurrentField == OnListGoneListener.NONE_SELECTED)
				{
					ExercisePickerActivity.super.onBackPressed();
				}
				else
				{
					switchToStages(m_Fields.get(nCurrentField));
				}
			}

		});
		m_viewListView.setAdapter(adapter);

		showTopBar();
		m_txtTopInformationBar
				.setText(R.string.activity_exercises_top_information_bar_fields);
	}

	/**
	 * Show the list of options in the selected field.
	 * 
	 * @param stage
	 */
	private void switchToOptions(final IReadOnlyExerciseStage stage)
	{
		m_CurrentStage = stage;
		if (Definitions.DebugFlags.DEBUG_ALLOW_CHOOSE_OPTION)
		{
			final OptionsBaseAdapter adapter =
					new OptionsBaseAdapter(stage);
			adapter.setOnHidingStartedListener(m_onHidingStartedListener);
			m_Adapter = adapter;
			adapter.setOnGoneListener(new OnListGoneListener()
			{
				@Override
				public void onListGone(final int nCurrentOption)
				{
					if (nCurrentOption == OnListGoneListener.NONE_SELECTED)
					{
						switchToStages(m_CurrentField);
					}
					else
					{
						// Call the game activity with the correct
						// ExerciseOption.
						Intent intent =
								new Intent(	ExercisePickerActivity.this,
											GameActivity.class);
						intent.putExtra(
								Definitions.Intents.INTENT_EXERCISE_ID,
								m_CurrentStage.getID());
						intent.putExtra(
								Definitions.Intents.INTENT_OPTION_ID,
								((ExerciseStageOption) adapter
										.getItem(nCurrentOption))
										.getParam());

						// We must mark that the stage has not been selected so
						// we can go back to this activity.

						m_CurrentStage = null;
						startActivity(intent);
					}
				}

			});
			m_viewListView.setAdapter(adapter);
			showTopBar();
			m_txtTopInformationBar
					.setText(R.string.activity_exercises_top_information_bar_options);
		}
		else
		{
			Intent intent =
					new Intent(	ExercisePickerActivity.this,
								ExercisePlanActivity.class);
			intent.putExtra(
					Definitions.Intents.INTENT_EXERCISE_ID,
					m_CurrentStage.getID());
			intent.putExtra(
					Definitions.Intents.INTENT_PLAN_ACTION,
					Definitions.Intents.PLAN_ACTION_STAGE_PLAN_START);

			m_CurrentStage = null;
			startActivity(intent);
		}
	}

	/**
	 * Show the list of stages in the selected field.
	 * 
	 * @param nCurrentField
	 */
	private void switchToStages(final IReadOnlyExerciseField field)
	{
		m_CurrentField = field;
		final StagesBaseAdapter adapter =
				new StagesBaseAdapter(field);
		adapter.setOnHidingStartedListener(m_onHidingStartedListener);
		m_Adapter = adapter;
		adapter.setOnGoneListener(new OnListGoneListener()
		{

			@Override
			public void onListGone(final int nCurrentStage)
			{
				if (nCurrentStage == OnListGoneListener.NONE_SELECTED)
				{
					switchToFields();
				}
				else
				{
					switchToOptions(m_CurrentField.getInfo()
							.getStages().get(nCurrentStage));
				}
			}

		});
		m_viewListView.setAdapter(adapter);
		showTopBar();
		m_txtTopInformationBar
				.setText(R.string.activity_exercises_top_information_bar_exercises);

	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
	}
}
