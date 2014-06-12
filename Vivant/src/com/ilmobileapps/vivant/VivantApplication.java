package com.ilmobileapps.vivant;

import android.app.Application;

/**
 * Global State - a place to store static variables and initialised singletons.
 * 
 * @author ivanlim
 * @version 1.0
 * @since 2014-05-21
 */
public class VivantApplication extends Application
{
	public static final String GALLERY_URL = "http://www.itechnet.com.au/galleries.json";
	public static final String FILE_PREFIX_GALLERY = "GALLERY_";

	private static VivantApplication mApplication;

	public static VivantApplication getInstance()
	{
		return mApplication;
	}

	public void onCreate()
	{
		super.onCreate();
		mApplication = this;
	}
}
