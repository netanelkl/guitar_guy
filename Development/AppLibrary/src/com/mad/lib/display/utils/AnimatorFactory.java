package com.mad.lib.display.utils;

import java.security.InvalidParameterException;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.mad.lib.display.graphics.GraphicsPoint;

public class AnimatorFactory
{
	public static final int	TOP		= 1;
	public static final int	BOTTOM	= 2;
	public static final int	LEFT	= 3;
	public static final int	RIGHT	= 4;
	public static final int	ALPHA	= 5;

	public static final int	FROM	= 1;
	public static final int	TO		= 2;

	/**
	 * Creates an animation for the object given the direction.
	 * 
	 * @param fAwayFromScreen
	 * @param nDirection
	 *            Should be any of TOP, BOTTOM, LEFT, RIGHT.
	 */
	public static Animation createAnimation(View view,
											boolean fAwayFromScreen,
											int nDirection)
	{
		if (fAwayFromScreen)
		{

			switch (nDirection)
			{
				case RIGHT:
				{
					return createToRightAnimation(view);
				}
				case BOTTOM:
				{
					return createToBottomAnimation(view);
				}
				case LEFT:
				{
					return createToLeftAnimation(view);
				}
				case ALPHA:
				{
					return new AlphaAnimation(1, 0);
				}
				default:
					throw new InvalidParameterException();
			}
		}
		else
		{
			switch (nDirection)
			{
				case LEFT:
				{
					return createFromLeftAnimation(view);
				}
				case RIGHT:
				{
					return createFromRightAnimation(view);
				}
				case BOTTOM:
				{
					return createFromBottomAnimation(view);
				}
				case ALPHA:
				{
					return new AlphaAnimation(0, 1);
				}
				default:
					throw new InvalidParameterException();
			}
		}
	}

	protected void createAppearAnimation()
	{
		// Animation for the logo.
		// AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
	}

	/**
	 * Get a shoot from down animation for a view.
	 * 
	 * @param viewToShoot
	 *            - View to shoot up.
	 * 
	 * @return Shoot from down animation.
	 */
	protected static TranslateAnimation createFromBottomAnimation(View viewToShoot)
	{
		int[] arPoints = new int[2];
		viewToShoot.getLocationInWindow(arPoints);
		int nTarget =
				(int) (GraphicsPoint.getDimensions().y - arPoints[1]);

		TranslateAnimation translateAnimation =
				new TranslateAnimation(0, 0, nTarget, 0);
		return translateAnimation;
	}

	/**
	 * Get a shoot right animation for a view.
	 * 
	 * @param viewToShoot
	 *            - View to shoot right.
	 * 
	 * @return Shoot right animation.
	 */
	protected static TranslateAnimation createToRightAnimation(View viewToShoot)
	{
		int[] arPoints = new int[2];
		viewToShoot.getLocationInWindow(arPoints);
		int nTarget =
				(int) (GraphicsPoint.getDimensions().x - arPoints[0]);

		TranslateAnimation translateAnimation =
				new TranslateAnimation(0, nTarget, 0, 0);
		return translateAnimation;
	}

	/**
	 * Get a shoot from left animation for a view.
	 * 
	 * @param viewToShoot
	 *            - View to shoot right.
	 * 
	 * @return Shoot from left animation.
	 */
	protected static TranslateAnimation createFromLeftAnimation(View viewToShoot)
	{
		int[] arPoints = new int[2];
		viewToShoot.getLocationOnScreen(arPoints);
		int nTarget = -(arPoints[0]);

		TranslateAnimation translateAnimation =
				new TranslateAnimation(nTarget, 0, 0, 0);
		return translateAnimation;
	}

	/**
	 * Get a shoot from right animation for a view.
	 * 
	 * @param viewToShoot
	 *            - View to shoot left.
	 * 
	 * @return Shoot from right animation.
	 */
	protected static TranslateAnimation createFromRightAnimation(View viewToShoot)
	{
		int[] arPoints = new int[2];
		viewToShoot.getLocationInWindow(arPoints);
		int nTarget =
				(int) (GraphicsPoint.getDimensions().x - arPoints[0]);

		TranslateAnimation translateAnimation =
				new TranslateAnimation(nTarget, 0, 0, 0);
		return translateAnimation;
	}

	/**
	 * Get a shoot down animation for a view.
	 * 
	 * @param viewToShoot
	 *            - View to shoot down.
	 * 
	 * @return Shoot down animation.
	 */
	protected static TranslateAnimation createToBottomAnimation(View viewToShoot)
	{
		int[] arPoints = new int[2];
		viewToShoot.getLocationInWindow(arPoints);
		int nTarget =
				(int) (GraphicsPoint.getDimensions().y - arPoints[1]);

		TranslateAnimation translateAnimation =
				new TranslateAnimation(0, 0, 0, nTarget);
		return translateAnimation;
	}

	// TODO: All the get shoot animations should be in a different location
	// and much more organized.
	/**
	 * Get a shoot left animation for a view.
	 * 
	 * @param viewToShoot
	 *            - View to shoot left.
	 * 
	 * @return Shoot left animation.
	 */
	protected static TranslateAnimation createToLeftAnimation(View viewToShoot)
	{
		int[] arPoints = new int[2];
		viewToShoot.getLocationInWindow(arPoints);
		int nTarget = -(arPoints[0] + viewToShoot.getWidth());
		TranslateAnimation translateAnimation =
				new TranslateAnimation(0, nTarget, 0, 0);
		return translateAnimation;
	}
}
