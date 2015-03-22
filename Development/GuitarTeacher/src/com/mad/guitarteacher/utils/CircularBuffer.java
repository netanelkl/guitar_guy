package com.mad.guitarteacher.utils;

import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

import android.util.Log;

public class CircularBuffer
{
	// private byte[][] data;
	private int data[];
	private int head;
	private int tail;
	private Semaphore readPermission;

	public CircularBuffer(Integer number)
	{
		// data = new byte[number][];
		readPermission = new Semaphore(1);
		data = new int[number];
		head = 0;
		tail = 0;
	}

	public boolean store(byte[] value)
	{
		ByteBuffer bb = ByteBuffer.wrap(value);
		// may need to be reversed
		int intVal = bb.getShort();
		data[tail++] = intVal;
		if (tail == data.length)
		{
			tail = 0;
		}
		return true;
	}

	public int getSize()
	{
		return tail - head;
	}

	public int read()
	{
		Log.i("Buffer", "Taking");
		if (head != tail)
		{
			// byte[] value = data[head++];
			int value = data[head++];
			if (head == data.length)
			{
				head = 0;
			}
			return value;
		} else
		{
			// return null;
			return 0;
		}
	}

	// Getting permission using a semaphore
	public void getPermission()
	{
		try
		{
			readPermission.acquire();
		} catch (InterruptedException e)
		{
			Log.i("Buffer", "Interrupted Exception");
			e.printStackTrace();
		}
	}

	// Giving up permission using a semaphore
	public void givePersmission()
	{
		readPermission.release();
	}

}