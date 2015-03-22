package com.mad.guitarteacher.utils.Services.JSON;

import org.json.JSONObject;

import com.mad.guitarteacher.dataContract.DataContractBase;
import com.mad.guitarteacher.services.JSONSerializerBase;

public class JSONTester
{
	@SuppressWarnings("unused")
	private class A extends DataContractBase
	{
		public int	toStoreA	= 4;

		public int	toStoreB	= 2;

		public int	toStoreC	= 1;
	}

	@SuppressWarnings("unused")
	private class B extends DataContractBase
	{
		public int	a;

		public int	b;

		public int	toStoreC	= 1;

		public JSONObject storeObject(final JSONSerializerBase serializer,
										final Class<?> objClass)
		{
			return serializer.storeObjectDefaultImpl(toStoreC);
		}
	}

	void testJSON()
	{
		// Example 1: Serialize an whole class
		// A info = new A();

		// JSONSerializer ser = new JSONSerializer();

		// String asJSONString = ser.storeObject(info, A.class).toString();

		// Save string to file: Should look like this.
		// {
		// "toStoreA" : 4,
		// "toStoreB" : 2,
		// "toStoreC" : 1,
		// }

		// Example 2: Deserialize
		// info = (A)ser.readObject(A.class);

		// Example 3: Serialize a part of a class
		// B info2 = new B();

		// JSONSerializer ser2 = new JSONSerializer();

		// String asJSONString2 = ser.storeObject(info2, B.class).toString();

		// Save string to file: Should look like this.
		// {
		// "toStoreC" : 1,
		// }
	}
}
