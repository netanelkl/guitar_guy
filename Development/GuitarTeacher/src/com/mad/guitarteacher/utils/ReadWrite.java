package com.mad.guitarteacher.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.zip.Checksum;

public class ReadWrite
{

    private static ReadWrite m_instance;

    // Singleton
    public static ReadWrite getInstance()
    {
	if (m_instance == null)
	{
	    m_instance = new ReadWrite();
	}

	return m_instance;
    }

    public Serializable ReadObject(String strFileName, Checksum checksum)
    {
	// Validate parameters.
	if (strFileName == null || strFileName.isEmpty())
	{
	    return null;
	}

	Serializable result = null;

	result = ReadObject(new File(strFileName), checksum);

	return result;
    }

    /**
     * This function loads a serializable object from a file.
     * 
     * @param fileInput
     *            - The file to read from.
     * @param checksum
     * @return
     */
    public Serializable ReadObject(File fileInput, Checksum checksum)
    {
	if (fileInput == null || !fileInput.canRead())
	{
	    return null;
	}

	// Open a stream to write the object into a byte array.
	java.io.FileInputStream pFileStream = null;

	try
	{
	    // Try to open the input file.
	    pFileStream = new FileInputStream(fileInput);
	}
	catch (FileNotFoundException e)
	{
	    System.out.println(e.getMessage());
	    e.printStackTrace();

	    return null;
	}

	return ReadObject(pFileStream, checksum);
    }

    public Serializable ReadObject(java.io.InputStream pInputStream,
				   Checksum checksum)
    {
	// Validate
	if (pInputStream == null || checksum == null)
	{
	    return null;
	}

	java.io.ObjectInputStream pObjectReader = null;
	Serializable oResult = null;

	try
	{
	    // Try to open the stream.
	    pObjectReader = new ObjectInputStream(pInputStream);

	    // Write the object to the stream.
	    oResult = (Serializable) pObjectReader.readObject();

	    long lChecksum = pObjectReader.readLong();

	    // Open a stream to write the object into a file .
	    java.io.ByteArrayOutputStream pStream = new ByteArrayOutputStream();
	    ObjectOutputStream pObjectWriter = new ObjectOutputStream(pStream);
	    pObjectWriter.writeObject(oResult);

	    // Calculate the checksum.
	    checksum.update(pStream.toByteArray(), 0, pStream.size());

	    // Validate the checksum!
	    if (checksum.getValue() != lChecksum)
	    {
	    	pObjectReader.close();
	    	pObjectReader = null;
	    }

	    // Write it all down.
	    pStream.flush();
	}
	catch (StreamCorruptedException e)
	{
	    System.out.println(e.getMessage());

	    e.printStackTrace();
	    oResult = null;
	}
	catch (OptionalDataException e)
	{
	    e.printStackTrace();
	    oResult = null;
	}
	catch (EOFException e)
	{
	    e.printStackTrace();
	    oResult = null;
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	    oResult = null;
	}
	catch (ClassNotFoundException e)
	{
	    e.printStackTrace();
	    oResult = null;
	}
	finally
	{
	    if (pInputStream != null)
	    {
		// Close the file stream.
		try
		{
		    pInputStream.close();
		}
		catch (IOException e)
		{
		    e.printStackTrace();
		    return null;
		}
	    }
	}

	return oResult;
    }

    public boolean StoreObject(String strFileName, Serializable objectToWrite,
			       Checksum checksum)
    {
	// Validate parameters.
	if (strFileName == null || strFileName.isEmpty())
	{
	    return false;
	}

	return StoreObject(new File(strFileName), objectToWrite, checksum);
    }

    public boolean StoreObject(String strDirName, String strFileName,
			       Serializable objectToWrite, Checksum checksum)
    {
	// Validate parameters.
	if (strDirName == null || strDirName.isEmpty()
	    || strFileName == null
	    || strFileName.isEmpty())
	{
	    return false;
	}

	return StoreObject(new File(strDirName, strFileName), objectToWrite,
		checksum);
    }

    /**
     * Stores an serializable object to a file with added checksum.
     * 
     * @param fileOutput
     *            - File to write to.
     * @param objectToWrite
     *            - The object to write.
     * @param checksum
     *            - The checksum to calculate.
     * @return true if successful.
     */
    public boolean StoreObject(File fileOutput, Serializable objectToWrite,
			       Checksum checksum)
    {
	// Validate parameters.
	if (fileOutput == null || objectToWrite == null || checksum == null)
	{
	    System.out.println("StoreObject: Invalid parameters");
	    return false;
	}

	// Open a stream to write the object into a byte array.
	java.io.FileOutputStream pFileStream = null;

	// Open a stream to write the object into a file .
	try
	{
	    pFileStream = new FileOutputStream(fileOutput);
	}
	catch (FileNotFoundException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return false;
	}

	return StoreObject(pFileStream, objectToWrite, checksum);
    }

    public boolean StoreObject(OutputStream pOutputStream,
			       Serializable objectToWrite, Checksum checksum)
    {
	// Validate parameters.
	if (pOutputStream == null || objectToWrite == null || checksum == null)
	{
	    System.out.println("StoreObject: Invalid parameters");
	    return false;
	}

	// Open a stream to write the object into a byte array.
	java.io.ByteArrayOutputStream pStream = new ByteArrayOutputStream();
	java.io.ObjectOutputStream pObjectWriter;

	boolean result = true;
	try
	{
	    pObjectWriter = new ObjectOutputStream(pStream);

	    // Write the object to the stream.
	    pObjectWriter.writeObject(objectToWrite);

	    // Calculate the checksum.
	    checksum.update(pStream.toByteArray(), 0, pStream.size());

	    // Write the checksum
	    pObjectWriter.writeLong(checksum.getValue());

	    // Close the object stream.
	    pObjectWriter.close();

	    // Write the object to the file.
	    pOutputStream.write(pStream.toByteArray());

	    // Open a data stream.
	    DataOutputStream pDataOutput = new DataOutputStream(pOutputStream);

	    // Write the checksum.
	    pDataOutput.writeLong(checksum.getValue());
	}
	catch (FileNotFoundException e)
	{
	    e.printStackTrace();
	    result = false;
	}
	catch (IOException e1)
	{
	    e1.printStackTrace();

	    // Well there was an IO exception, so like.. IDK.
	    result = false;
	}
	finally
	{
	    if (pOutputStream != null)
	    {
		try
		{
		    pOutputStream.close();
		}
		catch (IOException e)
		{
		    e.printStackTrace();

		    return false;
		}
	    }
	}

	return result;
    }
}
