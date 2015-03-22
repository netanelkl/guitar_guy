package com.mad.guitarteacher.utils.Services.JSON;

import com.mad.guitarteacher.dataContract.DataContractBase;

/**
 * This interface represents all objects stored on the filesystem.
 * 
 * @author Nati
 * 
 */
public abstract class StorableObject extends DataContractBase
{
	/**
	 * Get the filename of the current object.
	 * 
	 * This name is location invariant.
	 * 
	 * @return
	 */
	public abstract String getStorableFilename();

	/**
	 * Is the file stored in the assets or on the SD.
	 * 
	 * @return
	 */
	public abstract boolean isStoredOnAssets();

	/**
	 * Set the default information in case the file doesn't exist.
	 * 
	 * @return True if can set the default information. False if not.
	 */
	public abstract boolean setDefaultInformation();

	public void postReadInitialization()
	{
	}

	public void saveState()
	{
		JSONSerializer serializer = new JSONSerializer();
		serializer.write(this);
	}

}
