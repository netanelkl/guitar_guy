package com.mad.guitarteacher.display.graphics;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;

import com.mad.guitarteacher.R;
import com.mad.guitarteacher.display.graphics.animation.AnimatedDrawable;
import com.mad.guitarteacher.display.graphics.animation.AnimatedDrawableCollection;
import com.mad.guitarteacher.display.graphics.animation.FloatLinearAnimator;
import com.mad.guitarteacher.display.graphics.animation.IShowHideDrawable;
import com.mad.guitarteacher.display.graphics.animation.OnHiddenListener;
import com.mad.guitarteacher.display.graphics.animation.OnShownListener;
import com.mad.guitarteacher.display.graphics.fretMarks.FretFingerMarking;
import com.mad.guitarteacher.display.graphics.fretMarks.NonPlayFretFingerMarking;
import com.mad.guitarteacher.display.graphics.fretMarks.PlayFretFingerMarking;
import com.mad.lib.display.graphics.GraphicsPoint;
import com.mad.lib.display.utils.DisplayHelper;
import com.mad.lib.musicalBase.FretFingerPairBase;
import com.mad.lib.utils.ErrorHandler;

/**
 * A class to manage the fret marks (both those required to play, and the wrong
 * note ones).
 * 
 * Operation is first to initialize, then use clear and addMarking to display
 * what you need.
 * 
 * @author Nati
 * 
 */
public class FretMarksManager extends GraphicsCollection
{
	private final AnimatedDrawableCollection	m_arActualMarks	=
																		new AnimatedDrawableCollection();

	public void pause()
	{
		m_arActualMarks.pause();
	}

	public void resume()
	{
		m_arActualMarks.resume();
	}

	/**
	 * A single wrong note played mark.
	 * 
	 * This is the Drawable to draw a mark that lasts for a second and displays
	 * the estimated wrong note played.
	 * 
	 * @author Nati
	 * 
	 */
	public class WrongNoteMarking extends FloatLinearAnimator
	{
		Paint						m_Paint					=
																	new Paint();
		/**
		 * A const indicating how long will it would take it to appear.
		 */
		private static final int	DISPLAY_SHOW_TIME		=
																	1000;

		/**
		 * A const indicating how long will it would take it to fade out.
		 */
		private static final int	FADE_TIME				=
																	1000;

		/**
		 * The current position of the note.
		 */
		private final PointF		m_pntCurrentPosition	=
																	new PointF();

		/**
		 * Create the new wrong note played drawable.
		 * 
		 * @param pntCenter
		 *            The center of the place to paint.
		 */
		public WrongNoteMarking(final PointF pntCenter)
		{
			super(0, 1f, FADE_TIME);
			m_pntCurrentPosition
					.set(pntCenter.x
							- (s_bmpWrongNoteBmp.getWidth() / 2),
							pntCenter.y
									- (s_bmpWrongNoteBmp
											.getHeight() / 2));
		}

		@Override
		public void draw(final Canvas canvas)
		{
			if (m_DisplayState != DisplayState.Hidden)
			{
				m_Paint.setAlpha((int) (m_sfFloatLinearValue * 255));
				canvas.drawBitmap(s_bmpWrongNoteBmp,
						m_pntCurrentPosition.x,
						m_pntCurrentPosition.y, m_Paint);
			}
		}

		@Override
		public void show(final OnShownListener listener)
		{
			super.show(new OnShownListener()
			{

				@Override
				public void onShown(final IShowHideDrawable drawable)
				{
					Timer t = new Timer();
					t.schedule(new TimerTask()
					{

						@Override
						public void run()
						{
							if (listener != null)
							{
								listener.onShown(drawable);
							}
							m_UIThreadHolder.get()
									.runOnUiThread(
											new Runnable()
											{

												@Override
												public void run()
												{
													hide();
												}
											});
						}
					}, DISPLAY_SHOW_TIME);
				}
			});
		}

	}

	/**
	 * A GUI const based on the drawables.
	 */
	public final static int			VISUALLY_DISPLAYED_FRETS	=
																		6;

	/**
	 * A GUI const based on the drawables.
	 */
	public static final int			FRET_WIDTH					=
																		258;

	/**
	 * A 2-dimentional array for the locations of the frets on the strings.
	 */
	private final Point[][]			m_arPoints					=
																		new Point[VISUALLY_DISPLAYED_FRETS + 1][GuitarStrings.NUM_STRINGS];

	/**
	 * The drawable for the text indicating our offset from the bridge.
	 */
	private StaticBitmapDrawable	m_drawFretOffset;

	/**
	 * The offset from the bridge.
	 */
	private int						m_nFretOffset				=
																		0;

	/**
	 * Hold a reference to the activity for the UI thread.
	 * 
	 * Held weakly to not leak the activity.
	 */
	private WeakReference<Activity>	m_UIThreadHolder;

	/**
	 * The bitmap for the wrong note played.
	 */
	Bitmap							s_bmpWrongNoteBmp;

	public FretMarksManager()
	{
	}

	/**
	 * Add a mark with the given position on the guitar.
	 * 
	 * @param fingerPos
	 *            The position on the guitar and finger to display.
	 * @param fWrongNotes
	 *            A boolean indicating whether this is a wrong note played mark.
	 * @param play
	 *            If true then the user should play this note, meaning it's
	 *            marking will revolve, if false the marking will be idle.
	 */
	public synchronized void addMarking(Context context,
										final FretFingerPairBase fingerPos,
										final boolean fWrongNotes,
										final boolean play)
	{
		int fret = fingerPos.getFret();
		int nPosToDraw = fret == 0 ? 0 : fret - m_nFretOffset;

		if (nPosToDraw > VISUALLY_DISPLAYED_FRETS)
		{
			// If someone played an unrelated note, we're cool
			// but if we intended to put one ourselves, not cool.
			if (!fWrongNotes)
			{
				ErrorHandler
						.HandleError(new IllegalStateException());
			}
			return;
		}
		Point currentPos =
				m_arPoints[fret - m_nFretOffset][fingerPos
						.getString()];
		GraphicsDrawableBase mark = null;

		mark =
				getMarkToShow(context, fingerPos, fWrongNotes,
						play, currentPos);

		m_arActualMarks.add(mark);
	}

	/**
	 * Get the mark that should be shown.
	 * 
	 * @param fingerPos
	 *            - Finger position played.
	 * @param wrongNotes
	 *            - If true then the input finger position should be marked
	 *            wrong.
	 * @param play
	 *            - If true then the input finger position should be played.
	 * @param currentPos
	 *            - Position to show.
	 * @return Mark to display.
	 */
	protected GraphicsDrawableBase getMarkToShow(	Context context,
													final FretFingerPairBase fingerPos,
													final boolean wrongNotes,
													final boolean play,
													Point currentPos)
	{
		GraphicsDrawableBase mark;
		// If a the note played is wrong mark it as such.
		if (wrongNotes)
		{
			mark = markWrongNote(currentPos);
		}
		else
		{
			if (play)
			{
				mark =
						markNoteToPlay(context, fingerPos,
								currentPos);
			}
			else
			{
				mark = markNoteToHold(fingerPos, currentPos);
			}
		}
		return mark;
	}

	/**
	 * @param fingerPos
	 * @param currentPos
	 * @return
	 */
	protected GraphicsDrawableBase markNoteToHold(	final FretFingerPairBase fingerPos,
													Point currentPos)
	{
		GraphicsDrawableBase mark =
				new NonPlayFretFingerMarking(fingerPos
						.getFinger(), new PointF(currentPos));
		return mark;
	}

	/**
	 * @param fingerPos
	 * @param currentPos
	 * @return
	 */
	protected GraphicsDrawableBase markNoteToPlay(	Context context,
													final FretFingerPairBase fingerPos,
													Point currentPos)
	{
		GraphicsDrawableBase mark =
				new PlayFretFingerMarking(context, fingerPos
						.getFinger(), new PointF(currentPos));
		return mark;
	}

	/**
	 * @param currentPos
	 * @return
	 */
	protected GraphicsDrawableBase markWrongNote(Point currentPos)
	{
		WrongNoteMarking wrongNoteMark =
				new WrongNoteMarking(new PointF(currentPos));
		wrongNoteMark.show();
		return wrongNoteMark;
	}

	@Override
	public synchronized void clear()
	{
		clear(false);
	}

	public synchronized void clear(boolean fViolent)
	{
		if (fViolent)
		{
			m_arActualMarks.m_arCollection.clear();
		}
		else
		{
			// Go through the markings.
			for (GraphicsDrawableBase drawable : m_arActualMarks.m_arCollection)
			{
				AnimatedDrawable mark =
						(AnimatedDrawable) drawable;

				mark.hide(fViolent, new OnHiddenListener()
				{

					@Override
					public synchronized void onHidden(IShowHideDrawable drawable)
					{
						m_arActualMarks.m_arCollection
								.remove(drawable);
					}
				});
			}
		}
	}

	/**
	 * Initialize the object. create the drawables.
	 * 
	 * @param context
	 *            The context.
	 * @param pntFretStartOffset
	 *            The offset from the screen top left of the place where we
	 *            start drawing frets.
	 * @param pntScale
	 *            The screen scale.
	 */
	public void initialize(	final Activity context,
							final Point pntFretStartOffset)
	{
		m_UIThreadHolder = new WeakReference<Activity>(context);

		GraphicsPoint pntScale = GraphicsPoint.getScalePoint();

		FretFingerMarking.initialize(context);

		s_bmpWrongNoteBmp =
				DisplayHelper.getScaledBitmap(context
						.getResources(),
						R.drawable.game_wrong_note, true);

		// Create the required location for each fret-string pair.
		for (int nCurrentFret = 0; nCurrentFret < (VISUALLY_DISPLAYED_FRETS + 1); ++nCurrentFret)
		{
			for (int nCurrentString = 0; nCurrentString < GuitarStrings.NUM_STRINGS; ++nCurrentString)
			{
				m_arPoints[nCurrentFret][nCurrentString] =
						new Point(	(int) ((pntFretStartOffset.x + (FRET_WIDTH * (VISUALLY_DISPLAYED_FRETS - nCurrentFret))) / pntScale.x),
									(int) ((pntFretStartOffset.y + (GuitarStrings.STRINGS_DISTANCE * nCurrentString)) / pntScale.y));
			}
		}

		add(m_drawFretOffset =
				new StaticBitmapDrawable(	context,
											R.drawable.game_fret_offset,
											m_arPoints[1][0].x,
											m_arPoints[1][0].y,
											false,
											false)
				{
					Paint	m_Paint	= new Paint();

					Rect	bounds	= new Rect();

					@Override
					public void draw(final Canvas canvas)
					{
						if (m_DisplayState != DisplayState.Hidden)
						{
							super.draw(canvas);
							m_Paint.setTextAlign(Align.CENTER);
							// We want to add the draw of the fret offset.
							int nFontSize = (int) (40 * 0.7f);
							String strTextToDraw =
									Integer.toString(m_nFretOffset);
							m_Paint.setTextSize(nFontSize);

							// This centers the texts. God thank stack overflow.
							m_Paint.getTextBounds(strTextToDraw,
									0, strTextToDraw.length(),
									bounds);
							canvas.drawText(strTextToDraw,
									m_pntCurrentPosition.x,
									m_pntCurrentPosition.y
											- bounds.height(),
									m_Paint);
						}
					}
				});

		add(m_arActualMarks);
	}

	/**
	 * sets the offset from the bridge basing on the min and max frets.
	 * 
	 * @param nMinFret
	 *            The min fret to play.
	 * @param nMaxFret
	 *            The max fret to play.
	 */
	public void setOffset(final int nMinFret, final int nMaxFret)
	{
		if (nMaxFret < VISUALLY_DISPLAYED_FRETS)
		{
			m_nFretOffset = 0;
			m_drawFretOffset.hide();
		}
		else
		{
			if ((nMaxFret - nMinFret) >= VISUALLY_DISPLAYED_FRETS)
			{
				ErrorHandler
						.HandleError(new IllegalStateException());
				m_nFretOffset = 0;
			}
			else
			{
				m_nFretOffset = nMinFret - 1;
				m_drawFretOffset.show();
			}
		}
	}
}
