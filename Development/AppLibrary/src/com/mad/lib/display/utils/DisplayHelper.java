package com.mad.lib.display.utils;

import java.security.InvalidParameterException;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mad.lib.display.graphics.GraphicsPoint;

public class DisplayHelper
{
	public final static float		ORIGINAL_SCREEN_HEIGHT		=
																		1080;
	public final static float		ORIGINAL_SCREEN_WIDTH		=
																		1920;
	private static GraphicsPoint	m_pntTempOriginalPoint		=
																		new GraphicsPoint();
	private static GraphicsPoint	m_pntTempDestinationPoint	=
																		new GraphicsPoint();

	public static Bitmap getScaledBitmap(	final Resources resources,
											final int nResID)
	{
		return getScaledBitmap(resources, nResID, true);
	}

	public static Bitmap getStrechedBitmap(	final Resources resources,
											final int nResID)
	{
		Bitmap bmpImageOriginal =
				prepareScaledBitmapParams(resources, nResID,
						false);

		bmpImageOriginal =
				Bitmap.createScaledBitmap(bmpImageOriginal,
						(int) GraphicsPoint.getDimensions().x,
						(int) m_pntTempDestinationPoint.y, false);
		return bmpImageOriginal;
	}

	private static Bitmap prepareScaledBitmapParams(final Resources resources,
													final int nResID,
													boolean fKeepProportions)
	{
		Bitmap bmpImageOriginal =
				BitmapFactory.decodeResource(resources, nResID);

		if (bmpImageOriginal == null)
		{
			throw new InvalidParameterException();
		}

		int nWidth = (bmpImageOriginal.getWidth());
		int nHeight = (bmpImageOriginal.getHeight());

		m_pntTempOriginalPoint.x = nWidth;
		m_pntTempOriginalPoint.y = nHeight;

		// Measure the scaling to be done.
		measureScaledDimensions(m_pntTempOriginalPoint,
				m_pntTempDestinationPoint, fKeepProportions);
		return bmpImageOriginal;
	}

	public static Bitmap getCircleMaskedBitmap(	Bitmap bmp,
												int radius)
	{
		Bitmap sbmp;

		if ((bmp.getWidth() != radius)
				|| (bmp.getHeight() != radius))
		{
			float smallest =
					Math.min(bmp.getWidth(), bmp.getHeight());
			float factor = smallest / radius;
			sbmp =
					Bitmap.createScaledBitmap(bmp, (int) (bmp
							.getWidth() / factor), (int) (bmp
							.getHeight() / factor), false);
		}
		else
		{
			sbmp = bmp;
		}

		Bitmap output =
				Bitmap.createBitmap(radius, radius,
						Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		// final int color = 0xffa19774;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, radius, radius);

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		canvas.drawCircle((radius / 2) + 0.7f,
				(radius / 2) + 0.7f, (radius / 2) + 0.1f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);

		return output;
	}

	public static void scaleMargin(View view)
	{
		GraphicsPoint pntScale = GraphicsPoint.getScalePoint();
		MarginLayoutParams layoutParams =
				(MarginLayoutParams) view.getLayoutParams();
		layoutParams.setMargins(
				(int) (layoutParams.leftMargin / pntScale.x),
				(int) (layoutParams.topMargin / pntScale.y),
				(int) (layoutParams.rightMargin / pntScale.x),
				(int) (layoutParams.bottomMargin / pntScale.y));
	}

	public static void scalePadding(View view)
	{
		GraphicsPoint pntScale = GraphicsPoint.getScalePoint();
		view.setPadding(
				(int) (view.getPaddingLeft() / pntScale.x),
				(int) (view.getPaddingTop() / pntScale.y),
				(int) (view.getPaddingRight() / pntScale.x),
				(int) (view.getPaddingBottom() / pntScale.y));
	}

	public static Bitmap getScaledBitmap(	final Resources resources,
											final int nResID,
											boolean fKeepProportion)
	{
		Bitmap bmpImageOriginal =
				prepareScaledBitmapParams(resources, nResID,
						fKeepProportion);

		bmpImageOriginal =
				Bitmap.createScaledBitmap(bmpImageOriginal,
						(int) m_pntTempDestinationPoint.x,
						(int) m_pntTempDestinationPoint.y, false);

		return bmpImageOriginal;
	}

	public static void measureScaledDimensions(	GraphicsPoint pntOriginalDimensions,
												GraphicsPoint pntDestDimensions,
												boolean fKeepProportion)
	{
		GraphicsPoint pntScale = GraphicsPoint.getScalePoint();

		pntDestDimensions.y =
				(pntOriginalDimensions.y / pntScale.y);
		pntDestDimensions.x =
				(pntOriginalDimensions.x / pntScale.x);

		if (fKeepProportion)
		{
			if (pntOriginalDimensions.x < pntOriginalDimensions.y)
			{
				pntDestDimensions.y =
						((pntDestDimensions.y * pntScale.y) / pntScale.x);
			}
			else
			{
				pntDestDimensions.x =
						((pntDestDimensions.x * pntScale.x) / pntScale.y);
			}
		}
	}

	/**
	 * Scales a view based on its original size.
	 * 
	 * @param viewToScale
	 *            - View to scale.
	 */
	public static void scaleImageView(final ImageView viewToScale)
	{
		scaleImageView(viewToScale, false);
	}

	/**
	 * Scales a view based on its original size.
	 * 
	 * @param viewToScale
	 *            - View to scale.
	 */
	public static void scaleImageView(	final ImageView viewToScale,
										final boolean fKeepProportion)
	{
		viewToScale.getViewTreeObserver()
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
										(int) (viewToScale
												.getDrawable()
												.getIntrinsicHeight() / pntScale.y);
								int nWidth =
										(int) (viewToScale
												.getDrawable()
												.getIntrinsicWidth() / pntScale.x);
								scaleMeasuredView(viewToScale,
										fKeepProportion,
										nHeight, nWidth);
								viewToScale
										.getViewTreeObserver()
										.removeOnGlobalLayoutListener(
												this);
							}
						});
	}

	/**
	 * Scales a view based on its original size.
	 * 
	 * @param viewToScale
	 *            - View to scale.
	 */
	public static void scaleView(	final View viewToScale,
									final boolean fKeepProportion)
	{
		viewToScale.getViewTreeObserver()
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
										(int) (viewToScale
												.getHeight() / pntScale.y);
								int nWidth =
										(int) (viewToScale
												.getWidth() / pntScale.x);

								scaleMeasuredView(viewToScale,
										fKeepProportion,
										nHeight, nWidth);
								viewToScale
										.getViewTreeObserver()
										.removeOnGlobalLayoutListener(
												this);
							}

						});
	}

	/**
	 * Scales a view based on its original size.
	 * 
	 * @param viewToScale
	 *            - View to scale.
	 */
	public static void scaleViewByLayoutParams(	final View viewToScale,
												final boolean fKeepProportion)
	{
		final LayoutParams layoutParams =
				viewToScale.getLayoutParams();
		GraphicsPoint pntScale = GraphicsPoint.getScalePoint();
		int nHeight = (int) (layoutParams.height / pntScale.y);
		int nWidth = (int) (layoutParams.width / pntScale.x);

		scaleMeasuredView(viewToScale, fKeepProportion, nHeight,
				nWidth);
	}

	public static void scaleMeasuredView(	final View viewToScale,
											final boolean fKeepProportion,
											int nHeight,
											int nWidth)
	{
		GraphicsPoint pntScale = GraphicsPoint.getScalePoint();

		if (fKeepProportion)
		{
			// Motivation: scale to the one that gets smaller.
			// I prefer to have smaller views rather than overlapping ones.
			if (pntScale.x > pntScale.y)
			{
				nHeight =
						(int) ((nHeight * pntScale.y) / pntScale.x);
			}
			else
			{
				nWidth =
						(int) ((nWidth * pntScale.x) / pntScale.y);
			}
		}

		final LayoutParams layoutParams =
				viewToScale.getLayoutParams();
		layoutParams.height = nHeight;
		layoutParams.width = nWidth;
		viewToScale.setLayoutParams(layoutParams);
	}

	public static void scaleRectangle(	Rect rectSource,
										Rect rectDest,
										float sfScaleRatio)
	{
		float sfHeight =
				(rectSource.height() / 2) * sfScaleRatio, sfWidth =
				(rectSource.width() / 2) * sfScaleRatio;
		rectDest.top = (int) (rectSource.centerY() - sfHeight);
		rectDest.bottom =
				(int) (rectSource.centerY() + sfHeight);
		rectDest.left = (int) (rectSource.centerX() - sfWidth);
		rectDest.right = (int) (rectSource.centerX() + sfWidth);
	}

	public static void scaleFontSize(TextView tv)
	{
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tv
				.getTextSize()
				/ GraphicsPoint.getScalePoint().y);
	}

	// TODO: Comments, for fuck's sake what does this do?
	public static Bitmap toGrayscale(Bitmap bmpOriginal)
	{
		Bitmap workingBitmap = Bitmap.createBitmap(bmpOriginal);

		Bitmap bmpGrayscale =
				workingBitmap
						.copy(Bitmap.Config.ARGB_8888, true);

		// Bitmap.createBitmap(width, height,
		// Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f =
				new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}
}
