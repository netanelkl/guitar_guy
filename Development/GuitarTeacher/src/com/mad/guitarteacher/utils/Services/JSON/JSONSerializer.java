package com.mad.guitarteacher.utils.Services.JSON;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;

import com.mad.guitarteacher.dataContract.DataContractBase;
import com.mad.guitarteacher.services.JSONSerializerBase;
import com.mad.lib.utils.ErrorHandler;

/**
 * See JSONSerializerBase.
 * 
 * @author Nati
 * 
 */
public class JSONSerializer extends JSONSerializerBase
{
	private static final String	SDCARD_FOLDER	=
														"guitarTeacher";

	private static List<String>	s_Primitives;								// s_Primitives=
																			// new
																			// ArrayList<String>
																			// {{
																			// add("String")}};
																			// ,
																			// "java.lang.String",
																			// "int",
																			// "double",
																			// "boolean",
																			// "Date",
																			// "java.util.Date"
																			// }};

	private static boolean		s_fInit			= false;

	private static InputStream getStoredObjectInputStream(	final Context context,
															final StorableObject storableObject) throws IOException
	{
		InputStream in = null;
		// Read text from file
		StringBuilder text = new StringBuilder();
		BufferedReader br;
		String line;

		if (storableObject.isStoredOnAssets())
		{
			try
			{
				in =
						context.getAssets().open(
								storableObject
										.getStorableFilename());
			}
			catch (IOException e)
			{
				return null;
			}
			br = new BufferedReader(new InputStreamReader(in));
		}
		else
		{
			File file = getStorableObjectFile(storableObject);
			if ((file == null)
					|| (!file.exists() && !file.createNewFile()))
			{
				return null;
			}

			try
			{
				br = new BufferedReader(new FileReader(file));
			}
			catch (IOException e)
			{
				return null;
			}
		}

		while ((line = br.readLine()) != null)
		{
			text.append(line);
			text.append('\n');
		}

		return new JSONStreamReader(text.toString());

	}

	private static OutputStream getStoredObjectOutputStream(final StorableObject toStore)
	{
		OutputStream out;
		File file = getStorableObjectFile(toStore);
		try
		{
			if ((file == null)
					|| (!file.exists() && !file.createNewFile()))
			{
				return null;
			}

			out =
					new BufferedOutputStream(new FileOutputStream(file));
			return out;
		}
		catch (IOException e)
		{
			return null;
		}
	}

	private static File getStorableObjectFile(final StorableObject toStore)
	{
		File sdcard = Environment.getExternalStorageDirectory();
		// Get the text file
		File file =
				new File(sdcard, SDCARD_FOLDER + "/"
						+ toStore.getStorableFilename());
		File fParentFile = file.getParentFile();
		if (!fParentFile.exists() && !fParentFile.mkdirs())
		{
			return null;
		}
		return file;
	}

	private static void init()
	{
		s_Primitives =
				Arrays.asList("String", "java.lang.String",
						"int", "double", "boolean", "Date",
						"java.util.Date");
		s_fInit = true;

	}

	public JSONSerializer()
	{
		if (!s_fInit)
		{
			init();
		}
	}

	private Date deserializeDate(final String dateStringFromJSON)
	{
		// Remove prefix and suffix extra string information
		String dateString =
				dateStringFromJSON.replace("/Date(", "")
						.replace(")/", "");

		// Split date and timezone parts
		String[] dateParts = dateString.split("[+-]");

		// The date must be in milliseconds since January 1, 1970 00:00:00 UTC
		// We want to be sure that it is a valid date and time, aka the use of
		// Calendar
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(Long.parseLong(dateParts[0]));

		// If you want to play with time zone:
		// calendar.setTimeZone(TimeZone.);
		// Read back and look at it, it must be the same
		// long timeinmilliseconds = calendar.getTimeInMillis();

		// Convert it to a Date() object now:
		// long now = System.currentTimeMillis();

		// No need to add the local time offset, it will be automatically added.
		// int hoursOffset = TimeZone.getDefault().getOffset(now) / 3600000;
		int receivedDateOffset = 0;
		if (dateParts.length > 1)
		{
			receivedDateOffset =
					Integer.parseInt(dateParts[1]) / 100;
		}

		calendar.add(Calendar.HOUR, -receivedDateOffset);

		Date date = calendar.getTime();
		return date;

	}

	private JSONStreamReader getJSONStream()
	{
		return (JSONStreamReader) m_InputStream;
	}

	/**
	 * opens an input stream for reading the object.
	 * 
	 * Because the object may come from the assets, requiring the context, we
	 * pass it anyway.
	 * 
	 * @param context
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public boolean initRead(final Context context,
							final StorableObject obj) throws IOException
	{
		return initRead(getStoredObjectInputStream(context, obj));
	}

	public boolean initWrite(final StorableObject toStore)
	{
		return initWrite(getStoredObjectOutputStream(toStore));
	}

	// public JSONObject storeObject(Object obj)
	// {
	// return storeObjectGeneric(obj, false);
	// }

	public boolean read(final Context context,
						final StorableObject obj)
	{
		// Read the stages map.
		try
		{
			if (!initRead(context, obj))
			{
				return false;
			}

			if (!obj.readObject(this))
			{
				obj.setDefaultInformation();
				return true;
			}
			else
			{
				obj.postReadInitialization();
			}

			endRead();
		}
		catch (IOException e)
		{
			ErrorHandler.HandleError(e);
			return false;
		}
		return true;
	}

	//
	// @SuppressWarnings("rawtypes")
	// private JSONObject storeObjectGeneric(Object obj,
	// boolean fDefaultSerialization)
	// {
	//
	// }

	private boolean readObjectByMethod(	final Class<?> fieldClass,
										final Object toFill) throws InstantiationException,
															IllegalAccessException
	{
		if (DataContractBase.class.isAssignableFrom(fieldClass))
		{

			return ((DataContractBase) toFill).readObject(this);
		}
		else
		{
			return readObjectDefaultImpl(toFill);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean readObjectDefaultImpl(Object toFill)
	{
		JSONStreamReader reader = getJSONStream();
		try
		{
			JSONIndexOfResult jsonFieldName;
			boolean fAnyFields = false;
			while ((jsonFieldName = reader.readName()) != null)
			{
				try
				{
					fAnyFields = true;

					Object value = null;

					// If this is a 'child', create the child.
					if (jsonFieldName.Result
							.equalsIgnoreCase("__type"))
					{
						String childType = reader.readString();
						int indexOfNamespaceSplitter =
								childType.indexOf(":");

						childType =
								"FindMe.Service."
										+ childType
												.substring(0,
														indexOfNamespaceSplitter);
						Class<?> objClazz =
								Class.forName(childType);
						/* } */
						toFill = objClazz.newInstance();
						continue;
					}

					Class<?> objClass = toFill.getClass();

					// Figure out which kind of element are we dealing with and
					// handle the bastard.
					Field field =
							objClass.getField(jsonFieldName.Result);
					String fieldTypeName =
							field.getType().getName();

					value =
							parseValue(reader, field,
									fieldTypeName);
					if (value == null)
					{
						return false;
					}

					field.set(toFill, value);
				}
				catch (Exception ex)
				{
					throw ex;
				}
			}

			if (!fAnyFields)
			{
				return false;
			}

			return true;
		}
		catch (Exception ex)
		{
			ErrorHandler.HandleError(ex);
		}

		return false;
	}

	/**
	 * @param reader
	 * @param value
	 * @param field
	 * @param fieldTypeName
	 * @return
	 * @throws JSONException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private Object parseValue(	JSONStreamReader reader,
								Field field,
								String fieldTypeName)	throws JSONException,
														ClassNotFoundException,
														InstantiationException,
														IllegalAccessException
	{
		Object value = null;
		if (fieldTypeName.equalsIgnoreCase("String")
				|| fieldTypeName.equals("java.lang.String"))
		{
			value = reader.readString();
		}
		else if (fieldTypeName.equalsIgnoreCase("int")
				|| fieldTypeName.equals("java.lang.Integer"))
		{
			value = reader.readInt();
		}
		else if (fieldTypeName.equalsIgnoreCase("double"))
		{

			value = reader.readDouble();
		}
		else if (fieldTypeName.equalsIgnoreCase("boolean"))
		{
			value = reader.readBoolean();
		}
		else if (fieldTypeName.equalsIgnoreCase("Date")
				|| fieldTypeName.equals("java.util.Date"))
		{
			value = deserializeDate(reader.readString());
		}
		else
		{
			@SuppressWarnings("rawtypes")
			Class fieldClass = Class.forName(fieldTypeName);

			if (fieldClass.isEnum())
			{
				value =
						Enum.valueOf(fieldClass, reader
								.readString());
			}
			else if (fieldTypeName.equals("java.util.ArrayList"))
			{
				value =
						parseArrayList(reader, field
								.getGenericType());

			}
			else if (fieldTypeName.equals("java.util.HashMap"))
			{
				value = parseMap(reader, field.getGenericType());
			}
			else
			{
				value = fieldClass.newInstance();
				if (!readObjectByMethod(fieldClass, value))
				{
					return null;
				}
			}
		}
		return value;
	}

	private ArrayList<Object> parseArrayList(	JSONStreamReader reader,
												Type type)	throws JSONException,
															ClassNotFoundException,
															InstantiationException,
															IllegalAccessException
	{
		JSONIndexOfResult jsonFieldName;
		if (type instanceof ParameterizedType)
		{
			Type t =
					((ParameterizedType) type)
							.getActualTypeArguments()[0];

			String arrTypeName = t.toString().substring(6);
			ArrayList<Object> listElements =
					new ArrayList<Object>();

			if (arrTypeName.equals("java.lang.String"))
			{
				while ((jsonFieldName = reader.readName()) != null)
				{
					listElements.add(jsonFieldName.Result);
				}
			}
			else if (arrTypeName.equals("java.lang.Integer"))
			{
				listElements = reader.readIntegerArray();
			}
			else
			{
				Class<?> cArObjectClass =
						Class.forName(arrTypeName);
				Object deserialized;

				while ((readObjectByMethod(cArObjectClass,
						deserialized =
								cArObjectClass.newInstance())))
				{
					listElements.add(deserialized);

				}
			}
			return listElements;

		}
		return null;
	}

	private Map<Object, Object> parseMap(	JSONStreamReader reader,
											Type typeMap)	throws JSONException,
															ClassNotFoundException,
															InstantiationException,
															IllegalAccessException
	{
		if (typeMap instanceof ParameterizedType)
		{
			Type[] arTypes =
					((ParameterizedType) typeMap)
							.getActualTypeArguments();
			Type typeKey = arTypes[0];
			Type typeValue = arTypes[1];

			String strKeyTypeName =
					typeKey.toString().substring(6);
			String strKeyTypeValue =
					typeValue.toString().substring(6);
			Map<Object, Object> listElements =
					new HashMap<Object, Object>();

			Object key, value;
			while (((key =
					parseValue(reader, null, strKeyTypeName)) != null)
					&& ((value =
							parseValue(reader, null,
									strKeyTypeValue)) != null))
			{
				listElements.put(key, value);
			}
			return listElements;

		}
		return null;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public JSONObject storeObjectDefaultImpl(final Object obj)
	{
		JSONObject jsonObject = new JSONObject();
		boolean fHasContent = false;
		try

		{
			Field[] fields = obj.getClass().getFields();
			for (int i = 0; i < fields.length; i++)
			{
				Field field = fields[i];

				// If member field is empty, continue.
				if (fields[i].get(obj) == null)
				{
					continue;
				}

				String name = field.getName();
				String fieldTypeName = field.getType().getName();
				if (!s_Primitives.contains(fieldTypeName))
				{
					// String fieldTypeName = field.getType().getName();
					// Object value = null;
					Class<?> fieldClass =
							Class.forName(fieldTypeName);
					if (fieldClass.isEnum())
					{
						jsonObject.put(name, ((Enum) (field
								.get(obj))).ordinal());
					}
					else
					{
						Object subField = field.get(obj);
						JSONObject resultObj;
						if (DataContractBase.class
								.isAssignableFrom(fieldClass))
						{
							resultObj =
									((DataContractBase) subField)
											.storeObject(this);
						}
						else
						{
							resultObj =
									storeObjectDefaultImpl(subField);
						}
						if (resultObj != null)
						{
							fHasContent = true;
							jsonObject.put(name, resultObj);
						}
					}
				}
				else
				{
					fHasContent = true;
					jsonObject.put(name, field.get(obj));
				}

			}
		}
		catch (Exception ex)
		{
			ErrorHandler.HandleError(ex);
		}
		if (!fHasContent)
		{
			jsonObject = null;
		}
		return jsonObject;
	}

	public boolean write(final StorableObject obj)
	{
		// Read the stages map.
		try
		{
			if (!initWrite(obj))
			{
				return false;
			}

			JSONObject serializedObj = obj.storeObject(this);
			m_OutputStream.write(serializedObj.toString());
			endWrite();
		}
		catch (IOException e)
		{
			ErrorHandler.HandleError(e);
			return false;
		}
		return true;
	}
}
