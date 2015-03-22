package com.mad.metronomelib.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.mad.lib.activities.LayoutActivityBase;
import com.mad.lib.display.utils.DisplayHelper;
import com.mad.lib.display.utils.LocationAnimator;
import com.mad.metronomelib.R;
import com.mad.metronomelib.utils.MetronomeManager;
import com.mad.metronomelib.utils.MetronomeManager.OnTempoChangeListener;
import com.mad.metronomelib.utils.MetronomeManager.OnTickListener;
import com.mad.metronomelib.utils.MySpinnerAdapter;

/**
 * The metronome activity. Here the user can select a subdivision and hear a
 * metronome ticks according to the bpm (bits per minute)
 * 
 * @author Guy Kaplan
 * @since 12/11/2013
 * 
 * @edited Tom Feigin
 * 
 */
public class MetronomeActivity extends LayoutActivityBase
		implements OnTickListener, OnTempoChangeListener
{

	private static final int	MAX_BPM									=
																				320;
	private final String		BUNDLE_KEY_SETTINGS_OPEN				=
																				"settings.open";
	private final String		BUNDLE_KEY_BPM							=
																				"MetronomeManager.bpm";
	private final String		BUNDLE_KEY_NOMINATOR					=
																				"MetronomeManager.Nominator";
	private final String		BUNDLE_KEY_DENOMINATOR					=
																				"MetronomeManager.Denominator";

	private static final float	CIRCLE_SCALE_DOWN_ANIMATION_DURATION	=
																				0.95f;

	private static final float	CIRCLE_SCALE_UP_ANIMATION_DURATION		=
																				0.05f;

	private final int			DEFAULT_BPM								=
																				100;

	/******************************************************************
	 * Members
	 ******************************************************************/
	private TextView			m_tvCurrentSubdivisionTextView;
	private ImageView			m_cCurrentSubdivisionView;
	private ImageView			m_viewCircleLineView;
	private ImageView			m_startButtonText;
	private ImageView			m_stopButtonText;
	private Spinner				m_Nominator;
	private Spinner				m_Denominator;
	// The rotate animation for the circle view.
	private ObjectAnimator		m_animLineRotateAnimation;

	// a flag indicates if the settings view is showing or not.
	private boolean				m_isSettingsShown						=
																				false;

	// The scaling animations (When scale up is done, scale down is being
	// called)
	private ObjectAnimator		m_animScaleUpX;
	private ObjectAnimator		m_animScaleUpY;
	private ObjectAnimator		m_animScaleDownX;
	private ObjectAnimator		m_animScaleDownY;

	// Settings related members.
	private View				m_settingsView							=
																				null;
	private TextView			m_currentBpmText						=
																				null;
	private SeekBar				m_tempoPicker							=
																				null;

	private class OnHoldMultipleClickListener implements
			OnTouchListener
	{
		private static final int	SETTINGS_BPM_BUTTONS_INITIAL_DELAY_MILLIS	=
																						800;
		private static final float	REDUCTION_FACTOR							=
																						0.70f;
		int							m_nDelay									=
																						SETTINGS_BPM_BUTTONS_INITIAL_DELAY_MILLIS;

		OnHoldMultipleClickListener(Runnable runnable)
		{
			m_Runnable = runnable;
		}

		private final Handler	mHandler	= new Handler();
		private final Runnable	m_Runnable;
		final Runnable			action		= new Runnable()
											{

												@Override
												public void run()
												{
													m_Runnable
															.run();
													m_nDelay *=
															REDUCTION_FACTOR;
													mHandler.removeCallbacks(this);
													mHandler.postDelayed(
															action,
															m_nDelay);
												}
											};

		@Override
		public boolean onTouch(final View v, MotionEvent event)
		{

			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				m_nDelay =
						SETTINGS_BPM_BUTTONS_INITIAL_DELAY_MILLIS;
				action.run();
			}
			else if (event.getAction() == MotionEvent.ACTION_UP)
			{
				mHandler.removeCallbacks(action);
			}

			return true;
		}
	}

	// The metronome manager, this class handles the metronome logic.
	private MetronomeManager		m_metronomeManager;
	private BitmapDrawable			seekbarScaledThumb;
	private BitmapDrawable			tempoPickerDrawable;
	private BitmapDrawable			bmpSpinnerBackground;
	private Bitmap					bmpRaiseTempo;
	private Bitmap					bmpLowerTempo;
	private Bitmap					bmpBorder;
	private int						m_activityBPM;
	private AnimatorSet				m_animSetCircle;
	private Bitmap					bmpThumb;
	private FrameLayout				settingsButton;
	private FrameLayout				startButton;
	private ArrayAdapter<Integer>	spinnerAdapter;
	private final Integer[]			m_arSpinnerValues	=
																{
			0, 0												};
	private final static int		NOMINATOR			= 0;
	private final static int		DENOMINATOR			= 1;

	@Override
	protected View createRootView(final ViewGroup parent)
	{
		View result =
				LayoutInflater.from(this).inflate(
						R.layout.metronome_activity, null);

		return result;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (!s_fIsManagerInitialized)
		{
			return;
		}
		// Get the buttons from the XML.
		initViews();

		// Scaling the views.
		scaleViews();
		// Scale the margins of the line.
		m_viewCircleLineView.setAlpha(0.5f);

		// Load the animations from the XMLs.
		loadAnimations();

		// Set the animation listener.
		initializeRotateAnimation();

		// Define a touch listener that is meant to scale up and down a view
		// when it's being pressed.
		OnTouchListener buttonTouchScalerListener =
				new OnTouchListener()
				{

					@Override
					public boolean onTouch(	final View v,
											final MotionEvent event)
					{
						animateScaleUpDownView(v, event);
						return false;
					}
				};

		setStartButton(startButton, buttonTouchScalerListener);
		setSettingsButton(settingsButton,
				buttonTouchScalerListener);

		m_tvCurrentSubdivisionTextView =
				(TextView) findViewById(R.id.text);

		m_metronomeManager = new MetronomeManager(this);
		m_metronomeManager.setOnTickListener(this);
		m_metronomeManager.setOnBPMChange(this);

		// Set to the default bpm.
		setBPM(m_activityBPM = DEFAULT_BPM);

		// We dont need it now.
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				initSettingsBitmaps();
			}

		}).start();

		if (savedInstanceState != null)
		{
			loadSavedInstanceState(savedInstanceState);
		}
	}

	private void initViews()
	{
		m_viewCircleLineView =
				(ImageView) findViewById(R.id.circle_line);
		startButton =
				(FrameLayout) findViewById(R.id.start_button);
		m_startButtonText =
				(ImageView) findViewById(R.id.start_button_txt);
		m_stopButtonText =
				(ImageView) findViewById(R.id.stop_button_txt);
		settingsButton =
				(FrameLayout) findViewById(R.id.settings_button);
		m_tvCurrentSubdivisionTextView =
				(TextView) findViewById(R.id.text);
		m_cCurrentSubdivisionView =
				(ImageView) findViewById(R.id.background);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		outState.putBoolean(BUNDLE_KEY_SETTINGS_OPEN,
				m_isSettingsShown);

		outState.putInt(BUNDLE_KEY_BPM, m_metronomeManager
				.getBPM());
		outState.putInt(BUNDLE_KEY_NOMINATOR,
				m_arSpinnerValues[NOMINATOR]);
		outState.putInt(BUNDLE_KEY_DENOMINATOR,
				m_arSpinnerValues[DENOMINATOR]);
	}

	private void loadSavedInstanceState(final Bundle savedInstanceState)
	{
		if (savedInstanceState
				.getBoolean(BUNDLE_KEY_SETTINGS_OPEN))
		{
			onSettingsClicked();
		}

		m_metronomeManager.setBPM(savedInstanceState
				.getInt(BUNDLE_KEY_BPM));

		m_metronomeManager
				.setSubdivision(m_arSpinnerValues[NOMINATOR] =
						savedInstanceState
								.getInt(BUNDLE_KEY_NOMINATOR));

		m_arSpinnerValues[DENOMINATOR] =
				savedInstanceState
						.getInt(BUNDLE_KEY_DENOMINATOR);
	}

	/**
	 * 
	 */
	protected void initializeRotateAnimation()
	{
		// Initialize the rotate animation for the circle line.
		m_animLineRotateAnimation =
				ObjectAnimator.ofFloat(m_viewCircleLineView,
						"rotation", 360);
		m_animLineRotateAnimation
				.addUpdateListener(new AnimatorUpdateListener()
				{

					@Override
					public void onAnimationUpdate(ValueAnimator animation)
					{// Explanation: If the metronome stopped playing, we must
						// stop the animation at once!!
						// Alas, if we do so and the animation update listener
						// is still attached, buffer overflow,
						// hence, code below.
						if (!m_metronomeManager.isPlaying())
						{
							animation.removeUpdateListener(this);
							animation.end();
							animation.addUpdateListener(this);
						}
					}
				});
		m_animLineRotateAnimation
				.setInterpolator(new LinearInterpolator());

		// m_viewCircleLineView.getViewTreeObserver()
		// .addOnGlobalLayoutListener(
		// new OnGlobalLayoutListener()
		// {
		// @Override
		// public void onGlobalLayout()
		// {
		// m_viewCircleLineView
		// .setPivotY(m_viewCircleLineView
		// .getHeight());
		// m_viewCircleLineView
		// .setPivotX(m_viewCircleLineView
		// .getWidth() / 2);
		//
		// }
		// });
	}

	/**
	 * 
	 */
	protected void loadAnimations()
	{
		m_animScaleDownX =
				ObjectAnimator.ofFloat(
						m_cCurrentSubdivisionView, "scaleX", 1);
		m_animScaleUpX =
				ObjectAnimator.ofFloat(
						m_cCurrentSubdivisionView, "scaleX",
						1.1F);

		m_animScaleDownY =
				ObjectAnimator.ofFloat(
						m_cCurrentSubdivisionView, "scaleY", 1);
		m_animScaleUpY =
				ObjectAnimator.ofFloat(
						m_cCurrentSubdivisionView, "scaleY",
						1.1F);
		m_animSetCircle = new AnimatorSet();
		m_animSetCircle.play(m_animScaleUpX)
				.with(m_animScaleUpY).before(m_animScaleDownX)
				.before(m_animScaleDownY);
	}

	/**
	 * Scale all the views of the activity.
	 */
	protected void scaleViews()
	{
		DisplayHelper
				.scaleImageView((ImageView) findViewById(R.id.start_button_bg));
		DisplayHelper
				.scaleImageView((ImageView) findViewById(R.id.welcome_activity_logo));
		DisplayHelper.scaleImageView(m_startButtonText);
		DisplayHelper.scaleImageView(m_stopButtonText);
		DisplayHelper
				.scaleImageView((ImageView) findViewById(R.id.settings_button_bg));
		DisplayHelper
				.scaleImageView((ImageView) findViewById(R.id.settings_button_txt));
		DisplayHelper.scaleImageView(m_cCurrentSubdivisionView,
				true);
		DisplayHelper.scaleImageView(m_viewCircleLineView, true);
		DisplayHelper.scaleMargin(m_viewCircleLineView);
	}

	/**
	 * @param startButton
	 * @param buttonTouchScalerListener
	 */
	protected void setStartButton(	FrameLayout startButton,
									OnTouchListener buttonTouchScalerListener)
	{
		startButton
				.setOnTouchListener(buttonTouchScalerListener);

		startButton.setClickable(true);
		startButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(final View v)
			{
				onStartStopClicked();
			}
		});
	}

	/**
	 * @param settingsButton
	 * @param buttonTouchScalerListener
	 */
	protected void setSettingsButton(	FrameLayout settingsButton,
										OnTouchListener buttonTouchScalerListener)
	{
		settingsButton
				.setOnTouchListener(buttonTouchScalerListener);

		settingsButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(final View v)
			{
				onSettingsClicked();
			}
		});
	}

	private void initSettingsBitmaps()
	{
		bmpThumb =
				DisplayHelper.getScaledBitmap(getResources(),
						R.drawable.general_slider_circle, true);
		seekbarScaledThumb =
				new BitmapDrawable(getResources(), bmpThumb);
		tempoPickerDrawable =
				new BitmapDrawable(getResources(), DisplayHelper
						.getScaledBitmap(getResources(),
								R.drawable.seek_bar_bg));
		bmpSpinnerBackground =
				new BitmapDrawable(getResources(), DisplayHelper
						.getScaledBitmap(getResources(),
								R.drawable.spinner_bg, true));
		bmpRaiseTempo =
				DisplayHelper.getScaledBitmap(getResources(),
						R.drawable.metronome_btn_tempo_up, true);
		bmpLowerTempo =
				DisplayHelper.getScaledBitmap(getResources(),
						R.drawable.metronome_btn_tempo_down,
						true);
		bmpBorder =
				DisplayHelper.getScaledBitmap(getResources(),
						R.drawable.metronome_settings_bg, true);
	}

	/**
	 * Logic to act out when the settings button is pressed.
	 */
	private void onSettingsClicked()
	{
		// Add the view to the root view of the activity.
		final ViewGroup vRootView =
				MetronomeActivity.this.getRootView();

		// Check if the settings view has been loaded before.
		if ((m_settingsView =
				vRootView
						.findViewById(R.id.metronome_settings_root)) == null)
		{
			createSettingsView(vRootView);
		}

		animateSettingsTranslation(vRootView);
	}

	/**
	 * Create the settings view.
	 * 
	 * @param vRootView
	 */
	private void createSettingsView(final ViewGroup vRootView)
	{
		// If the view hasn't been loaded before, load the settings view
		// from the XML.
		m_settingsView =
				LayoutInflater.from(MetronomeActivity.this)
						.inflate(R.layout.metronome_settings,
								null);

		m_settingsView.setTranslationY(vRootView.getBottom());

		// Set the bpmText
		m_currentBpmText =
				(TextView) m_settingsView
						.findViewById(R.id.tempo_bpm_label);
		m_currentBpmText.setText(Integer.toString(m_activityBPM)
				+ " BPM");

		initTimeSignature();

		TextView viewTimeSignature =
				(TextView) m_settingsView
						.findViewById(R.id.time_signature);
		DisplayHelper.scaleFontSize(viewTimeSignature);
		TextView viewTempoLabel =
				(TextView) m_settingsView
						.findViewById(R.id.tempo_label);
		DisplayHelper.scaleFontSize(viewTempoLabel);
		// Scale paddings.
		DisplayHelper.scalePadding(m_settingsView
				.findViewById(R.id.content));
		View viewSlash = m_settingsView.findViewById(R.id.slash);
		DisplayHelper.scaleFontSize((TextView) viewSlash);
		DisplayHelper.scaleMargin(viewSlash);
		DisplayHelper.scaleMargin(m_currentBpmText);
		DisplayHelper.scaleFontSize(m_currentBpmText);
		DisplayHelper.scaleViewByLayoutParams(m_currentBpmText,
				true);
		// Scale view sizes.
		ImageView imgRaiseBPM =
				(ImageView) m_settingsView
						.findViewById(R.id.raise_bpm_btn);
		imgRaiseBPM.setImageBitmap(bmpRaiseTempo);
		DisplayHelper.scaleMargin(imgRaiseBPM);
		ImageView imgLowerBPM =
				(ImageView) m_settingsView
						.findViewById(R.id.lower_bpm_btn);
		imgLowerBPM.setImageBitmap(bmpLowerTempo);

		imgLowerBPM
				.setOnTouchListener(new OnHoldMultipleClickListener(new Runnable()
				{

					@Override
					public void run()
					{
						lowerBPM();
					}
				}));
		imgRaiseBPM
				.setOnTouchListener(new OnHoldMultipleClickListener(new Runnable()
				{

					@Override
					public void run()
					{
						raiseBPM();
					}
				}));

		DisplayHelper.scaleMargin(imgLowerBPM);

		((ImageView) m_settingsView.findViewById(R.id.border))
				.setImageBitmap(bmpBorder);

		initTempoPicker();

		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				vRootView.addView(m_settingsView);
			}
		});
	}

	/**
	 * Initialize the time signature related logic.
	 */
	private void initTimeSignature()
	{
		m_Nominator =
				initializeSpinner(R.id.nominator, new Integer[] {
						1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
						13 }, 3, new Runnable()
				{

					@Override
					public void run()
					{
						m_metronomeManager
								.setSubdivision(m_arSpinnerValues[NOMINATOR]);

					}
				}, NOMINATOR);

		m_Denominator =
				initializeSpinner(R.id.denominator,
						new Integer[] { 2, 4, 8, 16 }, 1, null,
						DENOMINATOR);
	}

	/**
	 * 
	 */
	protected Spinner initializeSpinner(int nResId,
										Integer[] values,
										int nSelectedIndex,
										final Runnable onChangeListener,
										final int nArPosition)
	{
		final Spinner viewSpinner =
				(Spinner) m_settingsView.findViewById(nResId);
		DisplayHelper.scaleMargin(viewSpinner);
		DisplayHelper.scalePadding(viewSpinner);

		viewSpinner
				.setAdapter(new MySpinnerAdapter(this, values));

		viewSpinner.setSelection(nSelectedIndex);

		viewSpinner.setBackground(bmpSpinnerBackground);
		// Listener for user changing the weight.
		OnItemSelectedListener weightChangeListener =
				new OnItemSelectedListener()
				{
					@Override
					public void onItemSelected(	AdapterView<?> arg0,
												View arg1,
												int arg2,
												long arg3)
					{
						int selectedValue =
								(Integer) viewSpinner
										.getSelectedItem();

						// Check if the nominator changed.
						if (m_arSpinnerValues[nArPosition] == selectedValue)
						{
							return;
						}

						m_arSpinnerValues[nArPosition] =
								selectedValue;

						boolean isPlaying =
								m_metronomeManager.isPlaying();
						if (isPlaying)
						{
							stopMetronome();
						}

						if (onChangeListener != null)
						{
							onChangeListener.run();
						}
						// m_metronomeManager
						// .setSubdivision(selectedValue);
						setBPM(m_activityBPM);

						if (isPlaying)
						{
							startMetronome();
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0)
					{
					}
				};
		viewSpinner
				.setOnItemSelectedListener(weightChangeListener);

		DisplayHelper.scaleViewByLayoutParams(viewSpinner, true);
		return viewSpinner;
	}

	/**
	 * Sets the BPM of the metronome manager.
	 * 
	 * @param bpm
	 *            - BPM to set.
	 */
	private void setBPM(int bpm)
	{
		int selectedItem = 4;

		if (m_Denominator != null)
		{
			selectedItem = m_arSpinnerValues[DENOMINATOR];
		}

		int actualBPM = (bpm * selectedItem) / 4;

		m_metronomeManager.setBPM(actualBPM);
	}

	/**
	 * Initialize the tempo picker object.
	 */
	private void initTempoPicker()
	{
		m_tempoPicker =
				(SeekBar) m_settingsView
						.findViewById(R.id.seeker);
		m_tempoPicker.setProgress(m_activityBPM - 10);
		int padding = bmpThumb.getWidth();
		m_tempoPicker.setPadding(padding, 0, padding, 0);
		// Set the thumb image.
		m_tempoPicker.setThumb(seekbarScaledThumb);
		DisplayHelper.scalePadding(m_tempoPicker);

		m_tempoPicker.setProgressDrawable(tempoPickerDrawable);

		m_tempoPicker
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
				{

					@Override
					public void onStopTrackingTouch(SeekBar seekBar)
					{
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar)
					{
					}

					@Override
					public void onProgressChanged(	SeekBar seekBar,
													int progress,
													boolean fromUser)
					{

						// Change the BPM to the value from the seek bar.
						setBPM(m_activityBPM = (progress + 10));
					}
				});
	}

	private void animateSettingsTranslation(final ViewGroup vRootView)
	{
		// Get the current view that the activity shows to animate it.
		final ViewGroup currentView =
				(ViewGroup) vRootView
						.findViewById(R.id.metronome_activity_root);
		currentView.post(new Runnable()
		{

			@Override
			public void run()
			{

				float currentViewToYPos;
				float settingsViewToYPos;
				// If the settings aren't shown, show them.
				if (m_isSettingsShown == false)
				{
					currentViewToYPos =
							vRootView.getBottom() * (-0.8f);
					settingsViewToYPos =
							currentViewToYPos
									+ vRootView.getHeight();
					m_isSettingsShown = true;
				}
				else
				{
					// Hide the settings.
					currentViewToYPos = vRootView.getTop();
					settingsViewToYPos = vRootView.getBottom();
					m_isSettingsShown = false;
				}

				// Setup the animations and start.
				LocationAnimator currentViewAnim =
						new LocationAnimator(	currentView,
												currentView
														.getTranslationY(),
												currentViewToYPos);
				currentViewAnim.setDuration(1000);
				currentViewAnim.start();

				LocationAnimator settingsAnim =
						new LocationAnimator(	m_settingsView,
												m_settingsView
														.getTranslationY(),
												settingsViewToYPos);
				settingsAnim.setDuration(1000);
				settingsAnim.start();

			}
		});
	}

	public void raiseBPM()
	{
		if (m_activityBPM < MAX_BPM)
		{
			setBPM(m_activityBPM += 1);
		}
	}

	public void lowerBPM()
	{
		if (m_activityBPM > 9)
		{
			setBPM(m_activityBPM -= 1);
		}
	}

	@Override
	protected void onPause()
	{
		m_metronomeManager.stop();
		super.onPause();
	}

	private void animateScaleUpDownView(final View v,
										final MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			v.setScaleX(0.9f);
			v.setScaleY(0.9f);
		}
		else if (event.getAction() == MotionEvent.ACTION_UP)
		{
			v.setScaleX(1.0f);
			v.setScaleY(1.0f);
		}
	}

	@Override
	public void onTick(final int currentSubdivision)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if (m_metronomeManager.isPlaying())
				{
					// Start the animations.
					m_animSetCircle.start();

					// Check if this is a tock subdivision.
					if (currentSubdivision == 0)
					{
						// If its a tock subdivision, start the line
						// animation again.
						m_animLineRotateAnimation.start();
					}
				}
			}
		});
	}

	private void onStartStopClicked()
	{
		View viewRoot = findViewById(android.R.id.content);
		// Change the state of the metronome.
		if (m_metronomeManager.isPlaying() == true)
		{
			viewRoot.setKeepScreenOn(false);
			stopMetronome();
		}
		else
		{
			viewRoot.setKeepScreenOn(true);
			setBPM(m_activityBPM);
			startMetronome();
		}
	}

	/**
	 * 
	 */
	protected void startMetronome()
	{
		m_metronomeManager.start();

		// Change the text from start to stop.
		m_startButtonText.setVisibility(View.INVISIBLE);
		m_stopButtonText.setVisibility(View.VISIBLE);
	}

	/**
	 * 
	 */
	protected void stopMetronome()
	{
		m_metronomeManager.stop();
		killAllAnimations();

		// Change the text from stop to start.
		m_startButtonText.setVisibility(View.VISIBLE);
		m_stopButtonText.setVisibility(View.INVISIBLE);

		// Cancel the animations.
		m_cCurrentSubdivisionView.clearAnimation();
		m_viewCircleLineView.clearAnimation();

		// Update the text on the subdivision text view.
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				m_tvCurrentSubdivisionTextView.setText(Integer
						.toString(m_activityBPM + 1));
			}
		});
	}

	/**
	 * 
	 */
	protected void killAllAnimations()
	{
		m_animSetCircle.cancel();
		m_animSetCircle.end();
		m_animLineRotateAnimation.end();
		// m_animScaleDownX.cancel();
		// m_animScaleDownX.end();
		// m_animScaleUpX.cancel();
		// m_animScaleUpX.end();
		// m_animScaleDownY.cancel();
		// m_animScaleDownY.end();
		// m_animScaleUpY.cancel();
		// m_animScaleUpY.end();
		// m_animLineRotateAnimation.cancel();
		// m_animLineRotateAnimation.end();
	}

	@Override
	public void onTempoChange(final int nNewBPMMilisecs)
	{
		MetronomeActivity.this.runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				// Set the duration of the animations
				m_animScaleUpX
						.setDuration((long) (nNewBPMMilisecs * CIRCLE_SCALE_UP_ANIMATION_DURATION));
				m_animScaleDownX
						.setDuration((long) (nNewBPMMilisecs * CIRCLE_SCALE_DOWN_ANIMATION_DURATION));
				// Set the duration of the animations
				m_animScaleUpY
						.setDuration((long) (nNewBPMMilisecs * CIRCLE_SCALE_UP_ANIMATION_DURATION));
				m_animScaleDownY
						.setDuration((long) (nNewBPMMilisecs * CIRCLE_SCALE_DOWN_ANIMATION_DURATION));
				m_animLineRotateAnimation
						.setDuration(nNewBPMMilisecs
								* m_metronomeManager.m_subdivisions);

				// Change the text of the picker.
				if ((m_currentBpmText != null)
						&& (m_tempoPicker != null))
				{
					int currentBpm = m_activityBPM;
					m_currentBpmText.setText(Integer
							.toString(currentBpm)
							+ " BPM");
					m_tempoPicker.setProgress(currentBpm - 10);
				}
			}
		});
	}

}
