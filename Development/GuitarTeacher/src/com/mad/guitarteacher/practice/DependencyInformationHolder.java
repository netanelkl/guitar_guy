package com.mad.guitarteacher.practice;

import java.util.ArrayList;

/**
 * A class to hold and manage dependency information.
 * 
 * @author Nati
 * 
 */
public class DependencyInformationHolder
{
	/**
	 * A boolean indicating if the object is available. True would indicate no
	 * dependency or dependency rule met.
	 * 
	 */
	private boolean									m_fIsAvailable		=
																				false;

	/**
	 * A boolean indicating whether we've already calced dependency for the
	 * current object.
	 */
	private boolean									m_fIsInitialized	=
																				false;

	/**
	 * The object on which the current object depends upon.
	 */
	private final DependencyInformationHolder		m_Dependency;

	/**
	 * The value of the current object. For example the stage's best score.
	 */
	private Integer									m_nValue;

	/**
	 * The value required by the dependency.
	 */
	private final int								m_nDependencyThresholdValue;

	private final IReadOnlyExerciseStage			m_ComposingStage;
	/**
	 * The objects dependent on me.
	 * 
	 * This will be used to inform them on updating their 'availability'.
	 * 
	 */
	private ArrayList<DependencyInformationHolder>	m_arDependsOnMe;

	/**
	 * Create a dependency information holder.
	 * 
	 * @param dependency
	 *            The object that this depends on.
	 */
	public DependencyInformationHolder(IReadOnlyExerciseStage composingStage)
	{
		m_nValue = null;
		m_nDependencyThresholdValue = 0;
		m_Dependency = null;
		m_fIsAvailable = true;
		m_fIsInitialized = true;
		m_ComposingStage = composingStage;
	}

	/**
	 * Create a dependency information holder.
	 * 
	 * This ctor is for when you do have a dependency to rely on.
	 * 
	 * @param dependency
	 *            The object that this depends on.
	 */
	public DependencyInformationHolder(	IReadOnlyExerciseStage composingStage,
										final DependencyInformationHolder dependency,
										final int nDependencyThresholdValue)
	{
		m_nDependencyThresholdValue = nDependencyThresholdValue;
		m_Dependency = dependency;
		m_fIsInitialized = true;
		m_ComposingStage = composingStage;
	}

	/**
	 * Adds an object that depends on me.
	 * 
	 * @param dependent
	 */
	public void addDependsOnMe(final DependencyInformationHolder dependent)
	{
		if (m_arDependsOnMe == null)
		{
			m_arDependsOnMe =
					new ArrayList<DependencyInformationHolder>();
		}

		m_arDependsOnMe.add(dependent);
	}

	/**
	 * Get the object 'this' depends upon.
	 * 
	 * @return
	 */
	public DependencyInformationHolder getDependency()
	{
		return m_Dependency;
	}

	/**
	 * Gets an array of objects that depend on me.
	 * 
	 * @return
	 */
	public ArrayList<DependencyInformationHolder> getDependentOnMe()
	{
		return m_arDependsOnMe;
	}

	/**
	 * Gets the value of the current object.
	 * 
	 * @return
	 */
	public Integer getValue()
	{
		return m_nValue;
	}

	void invalidate()
	{
		// If not available so far, force invalidation. Otherwise, it's not
		// needed.
		if (!m_fIsAvailable)
		{
			m_fIsInitialized = false;
		}
	}

	/**
	 * Returns whether the current object is available, meaning if the object
	 * doesn't depend or anything or that the dependency rule is met.
	 * 
	 * @return
	 */
	public boolean isAvailable()
	{
		if (m_fIsInitialized == true)
		{
			return m_fIsAvailable;
		}

		if (m_Dependency == null)
		{
			return true;
		}
		Integer nDepValue = m_Dependency.getValue();
		return m_Dependency.isAvailable() && (nDepValue != null)
				&& (nDepValue > m_nDependencyThresholdValue);
	}

	public void setAvailable()
	{
		m_fIsAvailable = true;
		m_fIsInitialized = true;
		// for (iterable_type iterable_element : iterable)
		// {
		//
		// }
	}

	/**
	 * Updates the current object's value.
	 * 
	 * Notifies the other objects of needing to update the availability.
	 */
	public void setValue(final int nValue)
	{
		m_nValue = nValue;

		if (m_arDependsOnMe != null)
		{
			for (DependencyInformationHolder dependsOnMe : m_arDependsOnMe)
			{
				dependsOnMe.invalidate();
			}
		}
	}

	public IReadOnlyExerciseStage getComposingStage()
	{
		return m_ComposingStage;
	}

	/**
	 * @return the m_nDependencyThresholdValue
	 */
	public int getDependencyThresholdValue()
	{
		return m_nDependencyThresholdValue;
	}

}
