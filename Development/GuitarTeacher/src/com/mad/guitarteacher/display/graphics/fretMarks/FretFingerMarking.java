package com.mad.guitarteacher.display.graphics.fretMarks;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.mad.guitarteacher.R;
import com.mad.guitarteacher.display.graphics.DisplayState;
import com.mad.guitarteacher.display.graphics.animation.AnimatedBitmapDrawable;
import com.mad.guitarteacher.display.graphics.animation.OnHiddenListener;
import com.mad.tunerlib.musicalBase.FretFingerPair;
import com.mad.lib.display.graphics.GraphicsPoint;
import com.mad.lib.display.utils.DisplayHelper;

/**
 * A single fret finger mark.
 * 
 * @author Tom
 * 
 */
public abstract class FretFingerMarking extends
		AnimatedBitmapDrawable
{
	private float				m_sfTransitionState;
	/**
	 * Holds the statically created bitmaps for all the fingers.
	 */
	protected static Bitmap[]	s_arFingersBitmaps	=
															new Bitmap[FretFingerPair.NUM_FINGERS + 1];

	/**
	 * Holds the ids of all the giners drawables.
	 */
	private static int[]		s_arFingersResIds	= new int[] {
			R.drawable.game_numbers0, R.drawable.game_numbers1,
			R.drawable.game_numbers2, R.drawable.game_numbers3,
			R.drawable.game_numbers4				};

	/**
	 * Get the fingers bitmaps.
	 * 
	 * @return Array holding the bitmaps used to display the fingers.
	 */
	public static void initialize(Context context)
	{
		// Create the bitmaps for the fingers.
		for (int nFinger = 0; nFinger < s_arFingersBitmaps.length; nFinger++)
		{
			s_arFingersBitmaps[nFinger] =
					BitmapFactory.decodeResource(context
							.getResources(),
							s_arFingersResIds[nFinger]);
		}
		GraphicsPoint pntScaledDimensions = new GraphicsPoint();
		GraphicsPoint pntBitmapDimensions =
				new GraphicsPoint(s_arFingersBitmaps[0]
						.getWidth(), s_arFingersBitmaps[0]
						.getHeight());

		DisplayHelper.measureScaledDimensions(
				pntBitmapDimensions, pntScaledDimensions, true);
	}

	/**
	 * Create a new instance of the FretFingerMarking class.
	 * 
	 * @param finger
	 *            - Index of finger.
	 * @param pointDest
	 *            - Destination on the guitar.
	 */
	public FretFingerMarking(	final int finger,
								final PointF pointDest)
	{
		super(	s_arFingersBitmaps[finger],
				new PointF(	(int) (pointDest.x),
							(int) (pointDest.y)),
				true,
				true);
		show();
	}

	@Override
	public void hide(boolean fViolent, OnHiddenListener listener)
	{
		m_anim = null;
		super.hide(fViolent, listener);
	}

	@Override
	protected ValueAnimator createAnimationImpl()
	{
		// If the dispay state is hiding, show.
		if (m_DisplayState == DisplayState.Hiding)
		{
			m_paintBitmap = new Paint();

			// Create the animator for indicating a note
			// correctly played.
			ObjectAnimator anim =
					ObjectAnimator.ofFloat(this,
							"TransitionState", 0.15f, 0.35f);

			// anim.setDuration(300);

			// Add a listner to the animator.
			anim.addUpdateListener(new AnimatorUpdateListener()
			{
				private final Rect	m_RectOriginal	=
															new Rect(m_rectBitmap);

				@Override
				public void onAnimationUpdate(ValueAnimator animation)
				{
					m_paintBitmap
							.setAlpha((int) (m_sfTransitionState * 256));
					DisplayHelper.scaleRectangle(m_RectOriginal,
							m_rectBitmap,
							1 + (m_sfTransitionState * 10));
				}

			});
			return anim;
		}
		return null;
	}

	/**
	 * Function that enables the animator change the internal property of
	 * transition state, determining the marking's opacity and size.
	 * 
	 * @param transitionState
	 */
	public void setTransitionState(float transitionState)
	{
		m_sfTransitionState = transitionState;
	}

	@Override
	public void draw(Canvas canvas)
	{
		super.draw(canvas);
	}
}
