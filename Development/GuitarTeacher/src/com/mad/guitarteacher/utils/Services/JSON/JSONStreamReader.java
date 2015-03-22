package com.mad.guitarteacher.utils.Services.JSON;

import java.io.IOException;
import java.io.InputStream;
import java.io.NotActiveException;
import java.util.ArrayList;

import org.json.JSONException;

public class JSONStreamReader extends InputStream
{
	String	m_strJsonInput;

	public JSONStreamReader(String backingString)
	{
		m_strJsonInput = backingString;
	}

	public static JSONStreamReader Create(String backString)
	{
		return new JSONStreamReader(backString);
	}

	JSONIndexOfResult	m_tempReaderNextType	= new JSONIndexOfResult();

	public JSONIndexOfResult readName()
	{
		lookupNextSymbol(m_strJsonInput, m_tempReaderNextType);

		int firstIndex = m_tempReaderNextType.Index;
		// If end of json doc, return null.
		if (m_tempReaderNextType.Index == -1 || ((m_tempReaderNextType.cFoundChar != '"') && m_tempReaderNextType.cFoundChar != ','))
		{
			m_strJsonInput = m_strJsonInput.substring(firstIndex + 1);
			return null;
		}
		else
		{

			if (m_tempReaderNextType.cFoundChar == '"')
			{
				firstIndex = m_tempReaderNextType.Index;
			}

			int secondIndex = m_strJsonInput.indexOf("\"", firstIndex + 1);

			String result = m_strJsonInput.substring(firstIndex + 1,
			        secondIndex);
			m_strJsonInput = m_strJsonInput.substring(secondIndex + 2);
			m_tempReaderNextType.Result = result;

			return m_tempReaderNextType;
		}
	}

	public String readString() throws JSONException
	{
		int nStartIndex = -1;
		int nEndIndex = -1;

		// Make sure the quotation are not part of the string.
		do
		{
			nStartIndex = nEndIndex;
			nEndIndex = m_strJsonInput.indexOf('"', nStartIndex + 1);
		}
		while (nStartIndex == -1);

		// If end of json doc, return null.
		if (nEndIndex == -1)
		{
			throw new JSONException("");
		}

		String strValue = m_strJsonInput.substring(nStartIndex + 1, nEndIndex)
		        .replaceAll("u[0-9]{3}a", "\n").replace("\\", "");
		m_strJsonInput = m_strJsonInput.substring(nEndIndex + 1);
		return strValue;
	}

	public int readInt() throws JSONException
	{
		int endIndex = lookupValueEnd(m_strJsonInput, m_tempReaderNextType);

		// If end of json doc, return null.
		if (endIndex == -1)
		{
			throw new JSONException("");
		}

		String strValue = m_strJsonInput.substring(0, endIndex);
		m_strJsonInput = m_strJsonInput.substring(endIndex);
		int result = Integer.parseInt(strValue);
		return result;
	}

	public ArrayList<Object> readIntegerArray() throws JSONException
	{
		m_strJsonInput = m_strJsonInput.substring(1);
		ArrayList<Object> list = new ArrayList<Object>();
		do
		{

			int endIndex = lookupValueEnd(m_strJsonInput, m_tempReaderNextType);

			if (m_tempReaderNextType.cFoundChar == ']' && endIndex == 0)
			{
				break;
			}

			// If end of json doc, return null.
			if (endIndex == -1)
			{
				throw new JSONException("");
			}

			String strValue = m_strJsonInput.substring(0, endIndex);
			m_strJsonInput = m_strJsonInput.substring(endIndex + 1);
			int result = Integer.parseInt(strValue);
			list.add(result);

		}
		while (m_tempReaderNextType.cFoundChar != ']');

		return list;
	}

	public double readDouble() throws JSONException
	{

		int endIndex = lookupValueEnd(m_strJsonInput, m_tempReaderNextType);

		// If end of json doc, return null.
		if (endIndex == -1)
		{
			throw new JSONException("");
		}

		String strValue = m_strJsonInput.substring(0, endIndex);
		m_strJsonInput = m_strJsonInput.substring(endIndex);
		double result = Double.parseDouble(strValue);
		return result;
	}

	public boolean readBoolean() throws JSONException
	{
		int endIndex = lookupValueEnd(m_strJsonInput, m_tempReaderNextType);

		// If end of json doc, return null.
		if (endIndex == -1)
		{
			throw new JSONException("");
		}

		String strValue = m_strJsonInput.substring(0, endIndex);
		m_strJsonInput = m_strJsonInput.substring(endIndex);

		if (!strValue.equals("true") && !strValue.equals("false"))
		{
			throw new JSONException("");
		}

		return strValue.equals("true");
	}

	// private int indexOfMany(String source, char firstChar, char secondChar,
	// IndexOfManyResult resultChar)
	// {
	//
	// int length = source.length();
	// for (int i = 0; i < length; i++)
	// {
	// if (source.charAt(i) == firstChar || source.charAt(i) == secondChar)
	// {
	// resultChar.FoundChar = source.charAt(i);
	// return i;
	// }
	// }
	// return -1;
	// }

	private void lookupNextSymbol(String source, JSONIndexOfResult resultChar)
	{
		char cIterator;
		int length = source.length();
		for (int i = 0; i < length; i++)
		{
			cIterator = source.charAt(i);
			if (cIterator == '}' || cIterator == ']' || cIterator == '"')
			{
				resultChar.cFoundChar = cIterator;
				resultChar.Index = i;
				return;
			}
		}
		resultChar.Index = -1;
	}

	private int lookupValueEnd(String source, JSONIndexOfResult resultChar)
	{
		char cIterator;
		int length = source.length();
		for (int i = 0; i < length; i++)
		{
			cIterator = source.charAt(i);
			if (cIterator == '}' || cIterator == ']' || cIterator == ',')
			{
				resultChar.cFoundChar = cIterator;
				return i;
			}
		}
		return -1;
	}

	@Override
	public int read() throws IOException
	{
		// This method shouldn't be called.
		throw new NotActiveException();
	}

}
