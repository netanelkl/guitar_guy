package com.mad.lib.display.graphics;

/**
 * A basic graphics point.
 * 
 * We should consider moving the entire points in the system to be
 * GraphicsPoint. The setX is created to support a setX method for animations.
 * 
 * @author Nati
 * 
 */
public class GraphicsPoint extends android.graphics.PointF
{
	private static GraphicsPoint	s_pntScale		=
															new GraphicsPoint();
	private static GraphicsPoint	s_pntDimensions	=
															new GraphicsPoint();

	public final static GraphicsPoint getScalePoint()
	{
		return s_pntScale;
	}

	public final static void setScalePoint(	final float sfWidthScale,
											final float sfHeightScale)
	{
		s_pntScale.x = sfWidthScale;
		s_pntScale.y = sfHeightScale;
	}

	public static final GraphicsPoint getDimensions()
	{
		return s_pntDimensions;
	}

	public final static void setScreenDimensions(	final float sfWidthScale,
													final float sfHeightScale)
	{
		s_pntDimensions.x = sfWidthScale;
		s_pntDimensions.y = sfHeightScale;
	}

	public GraphicsPoint()
	{
		super();
	}

	public GraphicsPoint(final float x, final float y)
	{
		super(x, y);
	}

	public void setX(final float newValue)
	{
		x = newValue;
	}
}
