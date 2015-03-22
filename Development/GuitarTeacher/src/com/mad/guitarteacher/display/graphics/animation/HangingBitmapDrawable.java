package com.mad.guitarteacher.display.graphics.animation;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.mad.guitarteacher.display.graphics.DisplayState;
import com.mad.lib.display.graphics.GraphicsPoint;

public class HangingBitmapDrawable extends
		AnimatedBitmapDrawable
{
	protected static final int		ANIMATION_LENGTH	= 500;
	private final Paint				m_paintRect			=
																new Paint();
	private final RectF				m_rectGrayRect		=
																new RectF();
	private final HangingDirection	m_Direction;
	private final float				m_sfMarginX;
	private final float				m_sfMarginY;

	// private Rect m_rect
	public enum HangingDirection
	{
		Top,
		Right,
		Bottom,
		Left
	}

	public HangingBitmapDrawable(	Bitmap bmpImage,
									PointF pntInitialPosition,
									HangingDirection direction,
									float sfMarginX,
									float sfMarginY,
									boolean fLoadOriginalSize)
	{
		super(	bmpImage,
				pntInitialPosition,
				false,
				fLoadOriginalSize);
		m_paintRect.setColor(Color.GRAY);
		m_paintRect.setAlpha((int) (0.48f * 256));

		m_Direction = direction;
		m_sfMarginX = sfMarginX;
		m_sfMarginY = sfMarginY;
	}

	@Override
	protected ValueAnimator createAnimationImpl()
	{
		GraphicsPoint pntDimensions =
				GraphicsPoint.getDimensions();
		float sfStartLeft = 0, sfStartTop = 0, sfStartRight = 0, sfStartBottom =
				0;
		float sfEndLeft = 0, sfEndTop = 0, sfEndRight = 0, sfEndBottom =
				0;
		float sfBitmapStartLeft = 0, sfBitmapStartTop = 0, sfBitmapEndLeft =
				0, sfBitmapEndTop = 0;
		switch (m_Direction)
		{
			case Top:
			{
				sfStartLeft =
						sfEndLeft =
								m_pntInitialPosition.x
										+ m_sfMarginX;
				sfStartRight =
						sfEndRight =
								(m_pntInitialPosition.x + m_rectBitmap
										.width())
										- m_sfMarginX;
				sfStartTop = sfStartBottom = sfEndTop = 0;
				sfEndBottom =
						m_pntInitialPosition.y
								+ (m_rectBitmap.height() / 2);
				sfBitmapStartLeft =
						sfBitmapEndLeft = m_pntInitialPosition.x;
				sfBitmapStartTop = 0 - m_rectBitmap.height();
				sfBitmapEndTop = m_pntInitialPosition.y;
				break;
			}
			case Bottom:
			{
				sfStartLeft =
						sfEndLeft =
								m_pntInitialPosition.x
										+ m_sfMarginX;
				sfStartRight =
						sfEndRight =
								(m_pntInitialPosition.x + m_rectBitmap
										.width())
										- m_sfMarginX;
				sfStartTop =
						sfStartBottom =
								sfEndBottom = pntDimensions.y;
				sfEndTop =
						m_pntInitialPosition.y
								+ (m_rectBitmap.height() / 2);
				// TODO: Not tested.
				break;
			}
			case Left:
			{
				sfStartLeft = sfEndLeft = sfStartRight = 0;
				sfEndRight =
						m_pntInitialPosition.x
								+ (m_rectBitmap.width() / 2);
				sfStartTop = sfEndTop = pntDimensions.y;
				sfStartBottom =
						sfEndBottom =
								m_pntInitialPosition.y
										+ (m_rectBitmap.height());
				// TODO: Not tested.
				break;
			}
			case Right:
			{
				sfEndLeft =
						m_pntInitialPosition.x
								+ (m_rectBitmap.width() / 2);
				sfStartLeft =
						sfStartRight =
								sfEndRight = pntDimensions.x;
				sfStartTop =
						sfEndTop =
								m_pntInitialPosition.y
										+ m_sfMarginY;
				sfStartBottom =
						sfEndBottom =
								(m_pntInitialPosition.y + m_rectBitmap
										.height())
										- m_sfMarginY;
				sfBitmapStartLeft = pntDimensions.x;
				sfBitmapEndLeft = m_pntInitialPosition.x;
				sfBitmapEndTop =
						sfBitmapStartTop =
								m_pntInitialPosition.y;
				break;
			}
		}

		RectF rectStart =
				new RectF(	sfStartLeft,
							sfStartTop,
							sfStartRight,
							sfStartBottom);
		RectF rectEnd =
				new RectF(	sfEndLeft,
							sfEndTop,
							sfEndRight,
							sfEndBottom);
		Rect rectStartBitmap =
				new Rect(	(int) sfBitmapStartLeft,
							(int) sfBitmapStartTop,
							(int) sfBitmapStartLeft
									+ m_rectBitmap.width(),
							(int) sfBitmapStartTop
									+ m_rectBitmap.height());
		Rect rectEndBitmap =
				new Rect(	(int) sfBitmapEndLeft,
							(int) sfBitmapEndTop,
							(int) sfBitmapEndLeft
									+ m_rectBitmap.width(),
							(int) sfBitmapEndTop
									+ m_rectBitmap.height());

		final ObjectAnimator animator =
				ObjectAnimator
						.ofObject(m_rectGrayRect, "",
								new RectFEvaluator(), rectStart,
								rectEnd).setDuration(
								ANIMATION_LENGTH);
		ObjectAnimator animBitmap =
				ObjectAnimator.ofObject(m_rectBitmap, "",
						new RectEvaluator(), rectStartBitmap,
						rectEndBitmap).setDuration(
						ANIMATION_LENGTH);
		animator.start();
		return animBitmap;
	}

	@Override
	public void draw(Canvas canvas)
	{
		if (m_DisplayState != DisplayState.Hidden)
		{
			canvas.drawRect(m_rectGrayRect, m_paintRect);
			super.draw(canvas);
		}
	}

	public boolean handleTouched(	final int nTouchX,
									final int nTouchY)
	{
		// Chech if the touch event happened inside the rect.
		if ((nTouchX >= m_rectBitmap.left)
				&& (nTouchX <= m_rectBitmap.right)
				&& (nTouchY >= m_rectBitmap.top)
				&& (nTouchY <= m_rectBitmap.bottom))
		{
			return true;
		}

		return false;
	}

	void setFloatLinear(float sf)
	{
	}
}
