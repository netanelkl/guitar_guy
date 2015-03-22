package com.mad.guitarteacher.dataContract;

import org.json.JSONObject;

import com.mad.guitarteacher.services.JSONSerializerBase;

public abstract class DataContractBase
{
	public JSONObject storeObject(JSONSerializerBase serializer)
	{
		return serializer.storeObjectDefaultImpl(this);
	}

	public boolean readObject(JSONSerializerBase serializer)
	{
		return serializer.readObjectDefaultImpl(this);
	}

}
