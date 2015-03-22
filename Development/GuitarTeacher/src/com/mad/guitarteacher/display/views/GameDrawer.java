package com.mad.guitarteacher.display.views;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.View;

import com.mad.guitarteacher.R;
import com.mad.guitarteacher.activities.IPlayingDisplayer;
import com.mad.guitarteacher.display.graphics.FretMarksManager;
import com.mad.guitarteacher.display.graphics.GraphicsCollection;
import com.mad.guitarteacher.display.graphics.GuitarStrings;
import com.mad.guitarteacher.display.graphics.MicNotifier;
import com.mad.guitarteacher.display.graphics.PauseButton;
import com.mad.guitarteacher.display.graphics.StaticBitmapDrawable;
import com.mad.guitarteacher.display.graphics.StringsToPlayDrawer;
import com.mad.guitarteacher.display.graphics.TotalPointsDrawer;
import com.mad.guitarteacher.display.graphics.animation.IShowHideDrawable;
import com.mad.guitarteacher.display.graphics.animation.OnHiddenListener;
import com.mad.guitarteacher.display.graphics.popups.PauseMenu;
import com.mad.guitarteacher.display.graphics.popups.PausingMenu;
import com.mad.guitarteacher.display.graphics.popups.ScenarioEndPopup;
import com.mad.guitarteacher.display.graphics.popups.StarsPausingMenu;
import com.mad.guitarteacher.display.graphics.tuner.TunerGauge;
import com.mad.guitarteacher.practice.ExerciseDoneReport;
import com.mad.guitarteacher.services.IGameManager;
import com.mad.guitarteacher.utils.MinMaxInteger;
import com.mad.lib.display.graphics.GraphicsPoint;
import com.mad.lib.display.pager.OnDisplayInformationDone;
import com.mad.lib.display.pager.PagerPageCollection;
import com.mad.lib.display.pager.PopupInformationDisplayer;
import com.mad.lib.display.pager.PopupInformationDisplayer.EDisplayState;
import com.mad.lib.musicalBase.FretFingerPairBase;
import com.mad.lib.musicalBase.IHandPositioning;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.lib.utils.BitmapsLoader;
import com.mad.lib.utils.ErrorHandler;

public class GameDrawer extends GraphicsCollection implements
		IPlayingDisplayer
{

	private static final Point				s_pntFingerBeginningOffset		=
																					new Point(	118,
																								314);
	private static final Point				s_pntBGFretsOffset				=
																					new Point(	215,
																								270);
	private static final Point				s_pntStringNotesBeginningOffset	=
																					new Point(	1783,
																								267);
	private static final PointF				s_pntTunerBeginningOffset		=
																					new PointF(	55f,
																								895f);
	private static final PointF				s_pntPauseButtonBeginningOffset	=
																					new PointF(	16f,
																								16f);
	private static final PointF				s_pntMicNotifierBeginningOffset	=
																					new PointF(	1590f,
																								796f);
	private WeakReference<IGameManager>		m_ActionsManager;
	private final WeakReference<Activity>	m_Activity;

	private ScenarioEndPopup				m_ExerciseEndPopup;
	private FretMarksManager				m_FingerMarkManager;

	private StringsToPlayDrawer				m_StringsToPlayManager;
	// This suggests a const scaling, which would probably suck :P
	// Consider revising.
	private boolean							m_fScreenCreated				=
																					false;
	int										m_nGamePoints;
	private PauseButton						m_PauseButton;
	private PausingMenu						m_PauseMenu;
	private PopupInformationDisplayer		m_PopupInformationDisplayer		=
																					null;
	private final Point						m_sfScreenDisplay				=
																					new Point();
	private TotalPointsDrawer				m_TotalPointsDrawer;
	private TunerGauge						m_TunerGauge;
	private final PausingMenuManager		m_PausingMenuManager			=
																					new PausingMenuManager();
	// I hate JAVA, why can't i just put it in the function?
	boolean									m_fIsGaugeShown					=
																					false;

	private StaticBitmapDrawable			m_drawableBGFrets;
	private MicNotifier						m_MicNotifier;

	/**
	 * Create a new instance of the BasicGameDrawer
	 * 
	 * @param context
	 *            - Current context.
	 */
	public GameDrawer(final Activity context)
	{
		super();
		m_Activity = new WeakReference<Activity>(context);

	}

	@Override
	public void updateTotalPoints(int totalPoints)
	{
		m_TotalPointsDrawer.updateTotalPoints(totalPoints);
	}

	/**
	 * Display the tuner gauge.
	 * 
	 * @param fShow
	 *            - If true show the tuner gauge, if false hide.
	 * @param sfBarValue
	 *            - The value to display on the bar.
	 */
	@Override
	public void displayTunerGauge(	final boolean fShow,
									final float sfBarValue)
	{
		m_Activity.get().runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				if (m_fIsGaugeShown)
				{
					if (!fShow)
					{
						m_TunerGauge.hide(false, null);
						m_fIsGaugeShown = false;
					}
				}
				else
				{
					if (fShow)
					{
						m_TunerGauge.show(null);
						m_fIsGaugeShown = true;
					}
				}

				m_TunerGauge.shiftPosition(sfBarValue);
			}
		});

	}

	/**
	 * @param context
	 */
	private void createScreen(final Activity context)
	{
		if ((context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
				&& !m_fScreenCreated)
		{
			clear();
			GraphicsPoint pntScale =
					GraphicsPoint.getScalePoint();

			BitmapsLoader bitmapsLoader =
					AppLibraryServiceProvider.getInstance().get(
							R.service.bitmaps_loader);
			// Loads the bg img.
			Bitmap m_bmpBG =
					bitmapsLoader.getBitmap(context
							.getResources(),
							R.drawable.game_activity_bg, false);
			// DisplayHelper.getScaledBitmap(
			// context.getResources(),
			// , false);

			add(new StaticBitmapDrawable(	m_bmpBG,
											0,
											0,
											true,
											false));

			// Draw the frets.
			add(m_drawableBGFrets =
					new StaticBitmapDrawable(bitmapsLoader
							.getBitmap(context.getResources(),
									R.drawable.game_bg_frets,
									false), s_pntBGFretsOffset.x
							/ pntScale.x, s_pntBGFretsOffset.y
							/ pntScale.y, true, false));

			add(new StaticBitmapDrawable(	bitmapsLoader
													.getBitmap(
															context.getResources(),
															R.drawable.game_string_notes,
															false),
											s_pntStringNotesBeginningOffset.x
													/ pntScale.x,
											s_pntStringNotesBeginningOffset.y
													/ pntScale.y,
											true,
											false));

			add(m_StringsToPlayManager =
					new StringsToPlayDrawer());
			m_StringsToPlayManager.initialize(context);

			add(m_FingerMarkManager = new FretMarksManager());

			context.getWindowManager().getDefaultDisplay()
					.getSize(m_sfScreenDisplay);

			m_FingerMarkManager.initialize(context,
					s_pntFingerBeginningOffset);
			StarsPausingMenu.staticInit(context);
			// Load the information bars.
			add(m_MicNotifier =
					new MicNotifier(context,
									new PointF(	s_pntMicNotifierBeginningOffset.x
														/ pntScale.x,
												s_pntMicNotifierBeginningOffset.y
														/ pntScale.y)));
			add(m_PauseButton =
					new PauseButton(context,
									new PointF(	s_pntPauseButtonBeginningOffset.x
														/ pntScale.x,
												s_pntPauseButtonBeginningOffset.y
														/ pntScale.y)));

			add(m_PauseMenu =
					new PauseMenu(	context,
									m_sfScreenDisplay.x / 2,
									m_sfScreenDisplay.y / 2));
			add(m_ExerciseEndPopup =
					new ScenarioEndPopup(	context,
											m_sfScreenDisplay.x / 2,
											m_sfScreenDisplay.y / 2));

			add(m_TotalPointsDrawer =
					new TotalPointsDrawer(	context,
											m_sfScreenDisplay.x,
											0));

			m_TunerGauge =
					new TunerGauge(	context,
									new PointF(	s_pntTunerBeginningOffset.x
														/ pntScale.x,
												s_pntTunerBeginningOffset.y
														/ pntScale.y));
			add(m_TunerGauge);

			m_fScreenCreated = true;
			m_fInvalidate = true;
		}
	}

	@Override
	public void displayInformation(	final PagerPageCollection stageInfo,
									final OnDisplayInformationDone onDoneListener)
	{
		m_PopupInformationDisplayer
				.setOnDisplayDoneListener(onDoneListener);
		m_PopupInformationDisplayer
				.setPagerInfo(stageInfo);
		m_PopupInformationDisplayer.show();
	}

	@Override
	public void displayNotes(	final IHandPositioning handPositioningToHold,
								final IHandPositioning handPositioningToPlay)
	{
		// m_CurrentFingering = handPositioning;
		// m_FingerMarkManager.clear();
		setHandPositioning(handPositioningToHold,
				handPositioningToPlay, false);

		m_Activity.get().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				// Iterate over the strings we need to play right now and mark
				// them.
				for (FretFingerPairBase pair : handPositioningToPlay
						.getFingerPositions())
				{
					// Mark to string to play.
					m_StringsToPlayManager.showPlayString(pair
							.getString());
				}
			}
		});
	}

	public void dispose()
	{
		m_ExerciseEndPopup = null;
		m_FingerMarkManager = null;
		m_MicNotifier = null;
		m_PauseMenu = null;
	}

	@Override
	public void draw(final Canvas canvas)
	{
		super.draw(canvas);

	}

	@Override
	public void endExercise(final ExerciseDoneReport report,
							final boolean fShowStars)
	{
		// report.getStars();
		m_Activity.get().runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				m_ExerciseEndPopup.show(report, fShowStars);
			}
		});
	}

	@Override
	public void resumeGame()
	{
		m_PauseMenu.hide(false, new OnHiddenListener()
		{

			@Override
			public void onHidden(IShowHideDrawable drawable)
			{
				// When the pause menu is hidden, check if the popup information
				// should be displayed.
				if (m_PopupInformationDisplayer
						.getDisplayState() == EDisplayState.Hidden)
				{
					// If the popup info should be displayed, display it.
					m_PopupInformationDisplayer.show();
				}
			}
		});

		m_MicNotifier.show();
		m_FingerMarkManager.resume();
		m_StringsToPlayManager.resume();
		m_TunerGauge.resume();
	}

	@Override
	public boolean handleTouched(	final Context context,
									final int nEventX,
									final int nEventY)
	{
		IGameManager actionsManager = m_ActionsManager.get();
		if (actionsManager == null)
		{
			ErrorHandler.HandleError(new NullPointerException());
			return false;
		}

		IGameManager.GameState gameState =
				actionsManager.getGameState();
		if ((gameState == IGameManager.GameState.Paused)
				|| (gameState == IGameManager.GameState.Ended))
		{
			PausingMenu activeMenu;
			if (gameState == IGameManager.GameState.Paused)
			{
				activeMenu = m_PauseMenu;
			}
			else
			{
				activeMenu = m_ExerciseEndPopup;
			}

			m_PausingMenuManager
					.handlePausingMenuTouch(context, nEventX,
							nEventY, actionsManager, activeMenu);

		}
		else
		{
			if (m_PauseButton.handleTouched(nEventX, nEventY))
			{
				m_ActionsManager.get().pauseExercise(context);
			}
		}
		return true;
	}

	@Override
	public void pauseGame()
	{
		if (m_PopupInformationDisplayer.getDisplayState() == EDisplayState.Shown)
		{
			m_PopupInformationDisplayer.hide();
		}

		m_PauseMenu.show(null);
		m_MicNotifier.hide();
		m_FingerMarkManager.pause();
		m_StringsToPlayManager.pause();
		m_TunerGauge.pause();
	}

	public void initialize(	final Activity context,
							final View view,
							final IGameManager actionsManager)
	{
		m_fScreenCreated = false;
		// Load the popup screen.
		m_PopupInformationDisplayer =
				new PopupInformationDisplayer(context, view);

		m_ActionsManager =
				new WeakReference<IGameManager>(actionsManager);
		createScreen(context);
	}

	public void onConfigurationChanged(	final Activity context,
										final Configuration newConfig)
	{
		createScreen(context);

		m_PopupInformationDisplayer.refreshState();
	}

	@Override
	public void setCurrentActState(final String strText)
	{
		m_Activity.get().runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				// m_InformationBarLeft.show(strText);

			}
		});
	}

	private void setHandPositioning(final IHandPositioning handPositioningToHold,
									final IHandPositioning handPositioningToPlay,
									final boolean fWrongNotes)
	{

		m_Activity.get().runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				MinMaxInteger maxMinPair = new MinMaxInteger();

				handleAddHandPositionProccess(maxMinPair,
						handPositioningToHold, false);

				handleAddHandPositionProccess(maxMinPair,
						handPositioningToPlay, true);

				// TODO: why not separate to just adding a marking?
				// Why revolving circles, idle circles and wrong markings,
				// are all conditioned with booleans instead of factories?
				if (!fWrongNotes)
				{
					m_FingerMarkManager.setOffset(
							maxMinPair.Min, maxMinPair.Max);
				}

				// To make sure no finger stays on the fret board.
				// The user will most likely want to draw new fingers anyway.
				if (!fWrongNotes)
				{
					// clear();
				}
			}

			/**
			 * @param fWrongNotes
			 * @param handPosition
			 * @param play
			 */
			private void handleAddHandPositionProccess(	MinMaxInteger maxMinPair,
														IHandPositioning handPosition,
														final boolean play)
			{
				if (handPosition == null)
				{
					return;
				}

				ArrayList<FretFingerPairBase> fretFingerPairs =
						handPosition.getFingerPositions();

				// We need the max min values to see if we need to shift the
				// fretboard displayed.
				for (FretFingerPairBase fingerPos : fretFingerPairs)
				{
					int fret = fingerPos.getFret();
					if (fret > maxMinPair.Max)
					{
						maxMinPair.Max = fret;
					}

					if (fret < maxMinPair.Min)
					{
						maxMinPair.Min = fret;
					}
				}

				// For each finger add the correct marking.
				for (FretFingerPairBase fingerPos : fretFingerPairs)
				{
					m_FingerMarkManager
							.addMarking(m_Activity.get(),
									fingerPos, fWrongNotes, play);
				}
			}
		});
	}

	private boolean	m_fIsWaitingPlay	= false;

	@Override
	public void setWaitingPlay(final boolean fEnable)
	{
		if (m_fIsWaitingPlay == fEnable)
		{
			return;
		}
		m_fIsWaitingPlay = fEnable;
		m_Activity.get().runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				if (fEnable)

				{
					m_MicNotifier.show();
					m_PauseButton.show(null);
				}
				else
				{
					m_MicNotifier.hide();
					m_PauseButton.hide();
				}
			}

		});
	}

	@Override
	public synchronized boolean update()
	{
		// TODO Auto-generated method stub
		return super.update();
	}

	@Override
	public void wrongNotePlayed(final IHandPositioning playedFingering)
	{
		if (playedFingering.getFingerPositions().size() == 0)
		{
			boolean x = true;
			Boolean.toString(x);
			return;
		}
		setHandPositioning(playedFingering, null, true);
	}

	@Override
	public void setFretVisibility(boolean fShowFrets)
	{
		if (fShowFrets)
		{
			m_drawableBGFrets.show();
		}
		else
		{
			m_drawableBGFrets.hide();
		}
	}

	@Override
	public void setNotesPlayedCorrectly()
	{
		m_Activity.get().runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				hideStringsAndFrets(false);
			}

		});

	}

	private void hideStringsAndFrets(boolean fViolentRemove)
	{
		m_FingerMarkManager.clear(fViolentRemove);

		if (fViolentRemove)
		{
			m_StringsToPlayManager.clear();
		}
		else
		{
			// Iterate over all the strings and hide them all.
			for (int i = 0; i < GuitarStrings.NUM_STRINGS; i++)
			{
				m_StringsToPlayManager.hidePlayString(
						fViolentRemove, i);
			}
		}
	}

	@Override
	public void destroy()
	{
		m_Activity.get().finish();
	}

	@Override
	public void setScenarioName(String strText)
	{
		m_PauseMenu.setTitle(strText);
		m_ExerciseEndPopup.setTitle(strText);
	}

	@Override
	public void resetScreen()
	{
		m_Activity.get().runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				hideStringsAndFrets(true);

			}
		});
	}
}
