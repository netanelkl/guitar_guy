package com.mad.guitarteacher.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.json.JSONObject;

public abstract class JSONSerializerBase
{
	/**
	 * Stream used for reading from data source into object.
	 */
	protected InputStream			m_InputStream;

	/**
	 * Stream used for writing object to.
	 */
	protected OutputStreamWriter	m_OutputStream;

	public JSONSerializerBase()
	{
	}

	/**
	 * Initialize the object for reading.
	 * 
	 * Should throw exception if not initialized
	 * 
	 * @param stream
	 * @return
	 */
	public boolean initRead(InputStream stream)
	{
		if (stream == null)
		{
			return false;
		}

		m_InputStream = stream;

		return true;
	}

	public void endRead() throws IOException
	{
		m_InputStream.close();
	}

	public void endWrite() throws IOException
	{
		m_OutputStream.close();
	}

	/**
	 * Initialize the object for writing.
	 * 
	 * Should throw exception if not initialized
	 * 
	 * @param stream
	 * @return
	 */
	public boolean initWrite(OutputStream stream)
	{
		if (stream == null)
		{
			return false;
		}

		m_OutputStream = new OutputStreamWriter(stream);

		return true;
	}

	/**
	 * Store the object using a default implementation
	 * 
	 * This method is similar to the basic serialization methods available.
	 * 
	 * @param obj
	 *            the object to store.
	 * @param objClass
	 *            The class of the object to store.
	 * @return
	 */
	public abstract JSONObject storeObjectDefaultImpl(Object obj);

	/**
	 * Store the object using a default implementation
	 * 
	 * This method is similar to the basic serialization methods available.
	 * 
	 * @param obj
	 *            the object to store.
	 * @param objClass
	 *            The class of the object to store.
	 * @return
	 */
	public abstract boolean readObjectDefaultImpl(Object toFill);

	// /**
	// * Store the object using the class' serialization definition.
	// *
	// * Should be used <b>by</b> objects having special serialization.
	// *
	// * @param obj the object to store.
	// * @param objClass The class of the object to store.
	// * @return
	// */
	// public abstract JSONObject storeObject(Object obj);
	//

}
