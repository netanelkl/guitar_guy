package com.mad.lib.musicalBase.keyConverters;

import java.util.HashMap;

/**
 * Factory for generating key converters.
 * 
 * @author Tom
 * 
 */
public class KeyConverterFactory
{
	/**
	 * Array of key converters.
	 */
	HashMap<EKeyConverterType, IKeyConverter>	m_mapConverters	=
																		new HashMap<EKeyConverterType, IKeyConverter>();

	/**
	 * Create a new instance of the KeyConverterFactory.
	 */
	public KeyConverterFactory()
	{
		// Create all key converters.
		MajorKeyConverter majorConverter =
				new MajorKeyConverter();
		MinorKeyConverter minorConverter =
				new MinorKeyConverter();
		m_mapConverters.put(majorConverter.getType(),
				majorConverter);
		m_mapConverters.put(minorConverter.getType(),
				minorConverter);
	}

	/**
	 * Get a key converter.
	 * 
	 * @param eType
	 *            - Type of the key converter to get.
	 * @return IKeyConverter -
	 */
	public IKeyConverter getConverter(final EKeyConverterType eType)
	{
		return m_mapConverters.get(eType);
	}
}
