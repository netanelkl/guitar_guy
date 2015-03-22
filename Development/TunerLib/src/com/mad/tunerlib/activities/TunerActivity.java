package com.mad.tunerlib.activities;

import java.security.InvalidParameterException;
import java.util.Random;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.mad.lib.activities.LayoutActivityBase;
import com.mad.lib.activities.views.AutoResizeTextView;
import com.mad.lib.chordGeneration.EString;
import com.mad.lib.display.graphics.GraphicsPoint;
import com.mad.lib.display.pager.OnDisplayInformationDone;
import com.mad.lib.display.pager.PagerPageCollection;
import com.mad.lib.display.pager.PopupInformationDisplayer;
import com.mad.lib.display.utils.DisplayHelper;
import com.mad.lib.display.utils.HorizontalListView;
import com.mad.lib.musicalBase.OctavedNote;
import com.mad.lib.tuning.FrequencyNormalizer;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.lib.utils.ErrorHandler;
import com.mad.lib.utils.HelperMethods;
import com.mad.tunerlib.R;
import com.mad.tunerlib.recognition.DetectionInfo;
import com.mad.tunerlib.recognition.NoteRecognizer;
import com.mad.tunerlib.recognition.NoteRecognizer.NoteDetectionInfo;
import com.mad.tunerlib.recognition.OnRecognitionDetectedListener;
import com.mad.tunerlib.utils.GuitarNotePlayer;

/**
 * Activity responsible for displaying and managing tuning logic.
 */
public class TunerActivity extends LayoutActivityBase implements
		OnRecognitionDetectedListener
{

	private static final int	FAKE_NEEDLE_INTERVAL			=
																		200;
	private static final int	NEEDLE_MOVEMENT_TIME			=
																		500;
	private static final double	RATIO_TUNING_CLOSE				=
																		0.1;
	private static final double	RATIO_TUNING_ACCURATE			=
																		0.032;
	private static final int	DURATION_RECOGNITION_FADE		=
																		2000;
	private static final int	THRESHOLD_ACCEPTING_NEW_INPUTS	=
																		2;

	private boolean getAutoMode()
	{
		return m_fIsAutoMode;
	}

	private boolean						m_fIsAutoMode				=
																			false;
	private static final float			MAX_ANGLE					=
																			50;
	private static final int			FAKE_FAST_INPUT_LEFT		=
																			20;
	private OctavedNote					m_nCurrentNote				=
																			null;

	private TextView					m_tvCurrentNote;

	private ImageView					m_viewNeedle;

	private View						m_viewRootContent;

	private TunerAdapter				m_adapter;

	FrequencyNormalizer					m_frequencyNormalizer;

	Random								m_Random					=
																			new Random();

	private float						m_lastAngle					=
																			0;

	private final float[]				m_arFrequencies				=
																			new float[EString.NumberOfStrings];
	private TextView					m_viewPrevFreq;
	private TextView					m_viewNextFreq;
	private Switch						m_viewSwitcher;
	private HorizontalListView			m_tunerNotes;
	private int							m_nCurrentSetString			=
																			EString.Sixth;
	private ObjectAnimator				m_anim;
	private ImageView					m_btnHelp;
	private PopupInformationDisplayer	m_popupHelp;
	private boolean						m_fIsAboutShown;
	private TextView					m_tvPlayedFreq;
	private Handler						m_FakeFastNeedleHandler;
	Runnable							m_FakeFastNeedleRunnable	=
																			new Runnable()
																			{

																				@Override
																				public void run()
																				{
																					Random r =
																							new Random();
																					float sfRandomCloseNumber =
																							m_sfPlayedFreq
																									+ ((r.nextFloat() - 0.5f) / 5);
																					updateUIOnRecognition(sfRandomCloseNumber);
																					m_nFakeFastInputLeft--;
																					if (m_nFakeFastInputLeft > 0)
																					{
																						m_FakeFastNeedleHandler
																								.postDelayed(
																										this,
																										FAKE_NEEDLE_INTERVAL);
																					}
																				}
																			};

	/**
	 * This class is meant to implement retrieving the views for the Stages.
	 * 
	 * @author Nati
	 * 
	 */
	private class TunerAdapter extends ArrayAdapter<OctavedNote>
	{
		Bitmap						m_bmpHighlightedButton;
		Bitmap						m_bmpRegularButton;
		private static final int	TEXT_VIEW_FONT_SIZE_PROPORTION	=
																			3;

		public TunerAdapter(final Context context)
		{
			super(	context,
					R.layout.tuner_activity_frequency_picker_item,
					R.id.name);
		}

		@Override
		public View getView(final int position,
							final View convertView,
							final ViewGroup parent)
		{
			View view =
					super.getView(position, convertView, parent);

			if (convertView == null)
			{
				DisplayHelper.scaleMargin(view);

				view.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						btnPressed(position);
					}
				});
			}

			Bitmap curBitmap = null;
			// Highlight the current tunable string.
			if (position == m_nCurrentSetString)
			{
				if (m_bmpHighlightedButton == null)
				{
					m_bmpHighlightedButton =
							DisplayHelper
									.getScaledBitmap(
											parent.getResources(),
											R.drawable.tuner_button_string_note_lighted);
				}
				curBitmap = m_bmpHighlightedButton;
			}
			else
			{
				if (m_bmpRegularButton == null)
				{
					m_bmpRegularButton =
							DisplayHelper
									.getScaledBitmap(
											parent.getResources(),
											R.drawable.tuner_button_string_note);
				}
				curBitmap = m_bmpRegularButton;
			}
			AutoResizeTextView tvName =
					(AutoResizeTextView) view
							.findViewById(R.id.name);
			// The text view should be TEXT_VIEW_FONT_SIZE_PROPORTION
			tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					curBitmap.getWidth()
							/ TEXT_VIEW_FONT_SIZE_PROPORTION);
			ImageView imgBackground =
					(ImageView) view
							.findViewById(R.id.background);
			imgBackground.setImageBitmap(curBitmap);
			return view;
		}

		public void setHighlightedButton(int nString)
		{
			m_nCurrentSetString = nString;
			if (!m_fIsAutoMode)
			{
				m_nCurrentNote =
						OctavedNote.getOpenStringNotes()[nString];
			}
			runOnUiThread(new Runnable()
			{

				@Override
				public void run()
				{
					notifyDataSetChanged();
				}
			});
		}

	}

	public void btnPressed(int nString)
	{
		GuitarNotePlayer player =
				AppLibraryServiceProvider.getInstance().get(
						R.service.guitar_note_player);
		player.playOpenString(nString);

		if (!getAutoMode())
		{
			m_adapter.setHighlightedButton(nString);
		}
	}

	/**
	 * @param sfPlayedFrequency
	 */
	private void animateFrequency(float sfAngle)
	{
		if (sfAngle != sfAngle)
		{
			ErrorHandler
					.HandleError(new InvalidParameterException());
			sfAngle = 0;
		}
		if (sfAngle > MAX_ANGLE)
		{
			sfAngle = MAX_ANGLE;
		}

		m_lastAngle = m_viewNeedle.getRotation();

		// The only way to test for NAN. Java suxxxx!!!
		if (m_lastAngle != m_lastAngle)
		{
			m_lastAngle = 0;
		}

		final ObjectAnimator anim =
				ObjectAnimator.ofFloat(m_viewNeedle, "Rotation",
						m_lastAngle, sfAngle);
		anim.setDuration(NEEDLE_MOVEMENT_TIME);
		anim.setInterpolator(new LinearInterpolator());
		anim.addUpdateListener(new AnimatorUpdateListener()
		{

			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				m_viewNeedle.invalidate();
			}
		});
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				if (m_anim != null)
				{
					m_anim.cancel();
				}
				m_anim = anim;
				anim.start();
				// m_viewNeedle.startAnimation(anim);
				// m_viewRootContent.invalidate();
			}
		});
	}

	@Override
	protected View createRootView(final ViewGroup parent)
	{
		return LayoutInflater.from(this).inflate(
				R.layout.tuner_activity, null);

	}

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (!s_fIsManagerInitialized)
		{
			return;
		}

		initFrequencies();

		initViews();

		m_adapter = new TunerAdapter(this);
		m_tunerNotes =
				(HorizontalListView) m_viewRootContent
						.findViewById(R.id.frequency_picker);
		DisplayHelper.scaleMargin(m_tunerNotes);
		DisplayHelper
				.scaleImageView((ImageView) m_viewRootContent
						.findViewById(R.id.welcome_activity_logo));
		com.mad.tunerlib.tuning.BaseGuitarTuning b =
				new com.mad.tunerlib.tuning.BaseGuitarTuning();

		m_adapter.addAll(b.getTuning());

		m_tunerNotes.setAdapter(m_adapter);

		setAutoMode(m_fIsAutoMode);
	}

	private void initFrequencies()
	{
		m_frequencyNormalizer =
				FrequencyNormalizer.createDefaultNormalizer();

		OctavedNote[] openNotes =
				OctavedNote.getOpenStringNotes();
		for (int i = 0; i < openNotes.length; i++)
		{
			m_arFrequencies[i] =
					m_frequencyNormalizer
							.getFrequencyByNoteIndex(openNotes[i]
									.getAbsoluteIndex());
		}
	}

	/**
	 * Initialize and scale all views.
	 */
	private void initViews()
	{
		m_viewRootContent = findViewById(R.id.root);

		m_tvCurrentNote =
				(TextView) m_viewRootContent
						.findViewById(R.id.current_freq);
		m_tvPlayedFreq =
				(TextView) m_viewRootContent
						.findViewById(R.id.played_freq);

		m_viewNextFreq =
				(TextView) m_viewRootContent
						.findViewById(R.id.next_freq);
		m_viewPrevFreq =
				(TextView) m_viewRootContent
						.findViewById(R.id.previous_freq);
		// m_viewNextFreq.setVisibility(View.INVISIBLE);
		// m_tvCurrentNote.setVisibility(View.INVISIBLE);
		m_viewPrevFreq.setRotation(-MAX_ANGLE);
		m_viewNextFreq.setRotation(MAX_ANGLE);
		rotateTextOnGauge(m_viewPrevFreq);
		rotateTextOnGauge(m_viewNextFreq);

		m_viewNeedle =
				(ImageView) m_viewRootContent
						.findViewById(R.id.needle);

		final ImageView viewGauge =
				(ImageView) m_viewRootContent
						.findViewById(R.id.gauge);

		m_btnHelp =
				(ImageView) m_viewRootContent
						.findViewById(R.id.help);
		m_btnHelp.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				showHelp();
			}
		});
		DisplayHelper.scaleImageView(viewGauge, false);
		m_btnHelp.setImageBitmap(DisplayHelper.getScaledBitmap(
				getResources(), R.drawable.help));
		DisplayHelper.scaleMargin(m_btnHelp);
		DisplayHelper.scaleImageView(m_viewNeedle);

		m_viewNeedle.getViewTreeObserver()
				.addOnGlobalLayoutListener(
						new OnGlobalLayoutListener()
						{
							@Override
							public void onGlobalLayout()
							{
								GraphicsPoint pntScale =
										GraphicsPoint
												.getScalePoint();
								int nHeight =
										(int) (m_viewNeedle
												.getDrawable()
												.getIntrinsicHeight() / pntScale.y);
								int nWidth =
										(int) (m_viewNeedle
												.getDrawable()
												.getIntrinsicWidth() / pntScale.x);
								m_viewNeedle
										.setPivotX(nWidth / 2);
								m_viewNeedle.setPivotY(nHeight);
								m_viewNeedle
										.getViewTreeObserver()
										.removeOnGlobalLayoutListener(
												this);
							}
						});
		m_popupHelp =
				new PopupInformationDisplayer(	this,
												m_viewRootContent);
		m_viewSwitcher = (Switch) findViewById(R.id.switcher);
		m_viewSwitcher.setTextOff(getString(R.string.manual));
		m_viewSwitcher.setTextOn(getString(R.string.auto));

		// Set the width.
		Point pntScreenSize = new Point();
		getWindowManager().getDefaultDisplay().getSize(
				pntScreenSize);
		m_viewSwitcher.setSwitchPadding(0);
		m_viewSwitcher.setTextSize(10);
		DisplayHelper.scalePadding(m_viewSwitcher);
		m_viewSwitcher.setSwitchTextAppearance(this,
				R.style.Switch);
		m_viewSwitcher
				.setOnCheckedChangeListener(new OnCheckedChangeListener()
				{

					@Override
					public void onCheckedChanged(	CompoundButton buttonView,
													boolean isChecked)
					{
						setAutoMode(isChecked);
					}
				});

	}

	private void rotateTextOnGauge(final View view)
	{
		DisplayHelper.scaleMargin(view);
		view.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener()
				{

					@Override
					public void onGlobalLayout()
					{
						int pivotX = view.getWidth() / 2;
						view.setPivotX(pivotX);
						int height = view.getHeight();
						view.setPivotY(height);
						// m_viewPrevFreq
						// .getViewTreeObserver()
						// .removeOnGlobalLayoutListener(
						// this);
					}
				});
	}

	@Override
	public void onBackPressed()
	{
		if (m_popupHelp.isShown())
		{
			m_popupHelp
					.dismiss(OnDisplayInformationDone.CLOSE_BUTTON);
		}
		else
		{
			super.onBackPressed();
		}
	}

	protected void showHelp()
	{

		PagerPageCollection pagerCollection =
				new PagerPageCollection();
		pagerCollection.addPage(getString(R.string.help),
				getString(R.string.help_page1_text));
		pagerCollection.addPage(getString(R.string.auto),
				getString(R.string.tuner_page_auto));
		pagerCollection.addPage(getString(R.string.manual),
				getString(R.string.Tuner_page_manual));
		pagerCollection.setIsPrePlay(false);
		m_popupHelp.setPagerInfo(pagerCollection);
		m_popupHelp.show();
	}

	private void setAutoMode(boolean isChecked)
	{
		m_fIsAutoMode = isChecked;

		// If true, meaning that this is an auto mode.
		int nString = EString.FirstString;
		m_adapter.setHighlightedButton(nString);
	}

	private final int			m_nFilterReceive	= 4;
	protected AlphaAnimation	m_alphaAnim;
	private float				m_sfPlayedFreq;
	private float				m_sfCurrentFreq;
	private float				m_sfNextFreq;
	private float				m_sfPrevFreq;
	private int					m_nFakeFastInputLeft;

	@Override
	public synchronized void onRecognitionDetected(final DetectionInfo detectionInfo)
	{
		// if (m_nFilterReceive == 0)
		// {
		// m_nFilterReceive = THRESHOLD_ACCEPTING_NEW_INPUTS;
		// return;
		// }
		// m_nFilterReceive--;
		boolean fIsAuto = getAutoMode();
		if (detectionInfo instanceof NoteDetectionInfo)
		{
			NoteDetectionInfo info =
					(NoteDetectionInfo) detectionInfo;
			int nNextNote, nPrevNote;
			if (fIsAuto)
			{
				m_nCurrentNote = new OctavedNote(info.NoteIndex);
			}

			int nNoteIndex = m_nCurrentNote.getAbsoluteIndex();
			nNextNote = nNoteIndex + 1;
			nPrevNote = nNoteIndex - 1;

			m_sfPlayedFreq = info.Frequency;
			m_sfCurrentFreq =
					m_frequencyNormalizer
							.getFrequencyByNoteIndex(nNoteIndex);
			m_sfNextFreq =
					m_frequencyNormalizer
							.getFrequencyByNoteIndex(nNextNote);
			m_sfPrevFreq =
					m_frequencyNormalizer
							.getFrequencyByNoteIndex(nPrevNote);

			// If we are in auto mode, you should update the adapter to the
			// currently played string.
			if (fIsAuto)
			{
				int nString =
						getClosestOpenString(m_sfPlayedFreq);
				m_adapter.setHighlightedButton(nString);
			}
			else
			{

			}

			m_nFakeFastInputLeft = FAKE_FAST_INPUT_LEFT;

			runOnUiThread(new Runnable()
			{

				@Override
				public void run()
				{
					if (m_FakeFastNeedleHandler == null)
					{
						m_FakeFastNeedleHandler = new Handler();
					}
					else
					{
						m_FakeFastNeedleHandler
								.removeCallbacks(m_FakeFastNeedleRunnable);
					}

					m_FakeFastNeedleHandler
							.post(m_FakeFastNeedleRunnable);
				}
			});

		}
		else
		{
			ErrorHandler.HandleError(new Exception());
		}
	}

	private void updateUIOnRecognition(float sfPlayedFreq)
	{
		float sfRatio =
				(sfPlayedFreq - m_sfCurrentFreq)
						/ (m_sfNextFreq - m_sfPrevFreq);
		float sfAngle = sfRatio * MAX_ANGLE * 2;

		// Test for NaN.
		if (sfAngle != sfAngle)
		{
			sfAngle = 0;
		}

		if (sfAngle > MAX_ANGLE)
		{
			sfAngle = MAX_ANGLE;
		}
		else if (sfAngle < -MAX_ANGLE)
		{
			sfAngle = -MAX_ANGLE;
		}
		updateFrequencyTexts(m_sfNextFreq, m_sfPrevFreq,
				m_sfCurrentFreq, sfPlayedFreq, Math.abs(sfRatio));

		animateFrequency(sfAngle);
	}

	private int getClosestOpenString(final float sfPlayedFreq)
	{
		int nString;
		for (nString = 0; nString < m_arFrequencies.length; nString++)
		{
			if (m_arFrequencies[nString] > sfPlayedFreq)
			{
				// Frequency is too low, so we chose the lowest open
				// string.
				if (nString == 0)
				{
					break;
				}

				// If the previous string is closer than our string,
				// we'll choose the previous string.
				if ((m_arFrequencies[nString] - sfPlayedFreq) >= (sfPlayedFreq - m_arFrequencies[nString - 1]))
				{
					nString--;
				}

				break;
			}
		}
		if (nString == EString.NumberOfStrings)
		{
			nString = EString.NumberOfStrings - 1;
		}
		return nString;
	}

	private void updateFrequencyTexts(	final float sfNextFreq,
										final float sfPrevFreq,
										final float sfCurrentFreq,
										final float sfPlayedFreq,
										final float sfAbsRatio)
	{
		final OctavedNote currentNote = m_nCurrentNote;
		this.runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				final float myFreq = sfCurrentFreq;
				m_tvCurrentNote
						.setText(Float.toString(HelperMethods
								.round(myFreq, 2))
								+ "("
								+ currentNote.getName()
								+ ")");

				m_viewNextFreq.setText(Float
						.toString(HelperMethods.round(
								sfNextFreq, 2)));
				m_viewPrevFreq.setText(Float
						.toString(HelperMethods.round(
								sfPrevFreq, 2)));
				m_tvPlayedFreq.setText(Float
						.toString(HelperMethods.round(
								sfPlayedFreq, 2)));

				if (m_alphaAnim == null)
				{
					m_alphaAnim = new AlphaAnimation(1, 0);
					m_alphaAnim
							.setDuration(DURATION_RECOGNITION_FADE);
				}

				float sfRat = sfAbsRatio;
				if (sfRat < RATIO_TUNING_ACCURATE)
				{
					m_tvPlayedFreq.setTextColor(Color.GREEN);
				}
				else if (sfRat < RATIO_TUNING_CLOSE)
				{
					m_tvPlayedFreq.setTextColor(Color.YELLOW);
				}
				else
				{
					m_tvPlayedFreq.setTextColor(Color.WHITE);
				}

				m_tvPlayedFreq.clearAnimation();
				m_tvPlayedFreq.startAnimation(m_alphaAnim);
			}
		});
	}

	@Override
	protected void onPause()
	{
		NoteRecognizer noteRecognizer =
				AppLibraryServiceProvider.getInstance().get(
						R.service.note_recognizer);
		noteRecognizer.stopListening();
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		NoteRecognizer noteRecognizer =
				AppLibraryServiceProvider.getInstance().get(
						R.service.note_recognizer);
		noteRecognizer.beginListening(this);
	}
}
