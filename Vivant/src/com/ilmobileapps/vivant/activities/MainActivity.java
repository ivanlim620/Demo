package com.ilmobileapps.vivant.activities;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import com.ilmobileapps.vivant.VivantApplication;
import com.ilmobileapps.vivant.fragments.LoaderFragment;
import com.ilmobileapps.vivant.fragments.LoaderFragment.ProgressListener;
import com.ilmobileapps.vivant.fragments.MapFragment;
import com.ilmobileapps.vivant.fragments.SplashFragment;
import com.ilmobileapps.vivant.models.Gallery;

/**
 * This class is the main activity for the app. It is responsible for containing various fragments. It provides
 * ActionBar functionality.
 * 
 * @author ivanlim
 * @version 1.0
 * @since 2014-05-21
 */
public class MainActivity extends ActionBarActivity implements ProgressListener
{
	/**
	 * Classes wishing to be notified of when Back Key is pressed. Could also be implemented using LocalBroadcastManager
	 * instead of an Interface.
	 */
	public interface BackPressedListener
	{
		/**
		 * Inform the listener Back has been pressed.
		 */
		public void doBack();
	}

	// Constants

	/**
	 * Tag used in Fragment Transaction for {@link LoaderFragment}
	 */
	private static final String TAG_FRAG_LOADER = "loader";
	/**
	 * Tag used in Fragment Transaction for {@link MapFragment}
	 */
	private static final String TAG_FRAG_MAP = "map";
	/**
	 * Tag used in Fragment Transaction for {@link SplashFragment}
	 */
	private static final String TAG_FRAG_SPLASH = "splash";

	// Instance Variables

	/**
	 * Fragment {@link LoaderFragment}
	 */
	private LoaderFragment mLoaderFragment;
	/**
	 * Fragment {@link MapFragment}
	 */
	private MapFragment mMapFragment;
	/**
	 * Current listener to be inform for Back Key press
	 */
	private BackPressedListener mBackPressedListener;

	/**
	 * Returns the {@link Gallery} as a {@link List} or <code>null</code> if not defined.
	 * 
	 * @return A list of Gallery or <code>null</code> if not defined.
	 */
	public List<Gallery> getGalleries()
	{
		if (mLoaderFragment != null)
		{
			if (mLoaderFragment.isCompleted()) return mLoaderFragment.getGalleries();
		}
		return null;
	}

	@Override
	public void onBackPressed()
	{
		// Detected Back Key is pressed. Notify listener if one is registered.
		if (mBackPressedListener != null)
			mBackPressedListener.doBack();
		else super.onBackPressed();
	}

	@Override
	public void onCompletion(List<Gallery> result)
	{
		// The list of gallery is ready for use in the {@link MapFragment}
		// Launch existing fragment or create a new one.
		final FragmentManager aFragmentManager = getSupportFragmentManager();
		mMapFragment = (MapFragment) aFragmentManager.findFragmentByTag(TAG_FRAG_MAP);
		if (mMapFragment == null)
		{
			mMapFragment = MapFragment.newInstance();
			aFragmentManager.beginTransaction().replace(android.R.id.content, mMapFragment, TAG_FRAG_MAP).commit();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Create a LoaderFragment if it doesn't already exist. If it does, check if the data is ready.
		final FragmentManager aFragmentManager = getSupportFragmentManager();
		mLoaderFragment = (LoaderFragment) aFragmentManager.findFragmentByTag(TAG_FRAG_LOADER);
		if (mLoaderFragment == null)
		{
			mLoaderFragment = new LoaderFragment();
			// Register to be inform when the data is ready.
			mLoaderFragment.registerProgressListener(this);
			mLoaderFragment.startLoading(VivantApplication.GALLERY_URL);
			aFragmentManager.beginTransaction().add(mLoaderFragment, TAG_FRAG_LOADER).commit();
		}
		else
		{
			// Register to be inform when the data is ready.
			mLoaderFragment.registerProgressListener(this);
			// Check if data is ready
			if (mLoaderFragment.isCompleted())
			{
				onCompletion(mLoaderFragment.getGalleries());
				// If data is ready, no need to show the SplashFragment, so return.
				return;
			}
		}

		// Create a SplashFragment if it doesn't already exist.
		SplashFragment mSplashFragment = (SplashFragment) aFragmentManager.findFragmentByTag(TAG_FRAG_SPLASH);
		if (mSplashFragment == null)
		{
			mSplashFragment = new SplashFragment();
			aFragmentManager.beginTransaction().add(android.R.id.content, mSplashFragment, TAG_FRAG_SPLASH).commit();
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		// free up instance variables.
		mLoaderFragment = null;
		mMapFragment = null;
		mBackPressedListener = null;
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		if (mLoaderFragment != null)
		{
			// Register to be inform when the data is ready.
			mLoaderFragment.registerProgressListener(this);
			// Check if data is ready
			if (mLoaderFragment.isCompleted()) onCompletion(mLoaderFragment.getGalleries());
		}
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		if (mLoaderFragment != null)
		{
			// Stop listening to progress
			mLoaderFragment.unRegisterProgressListener(this);
		}
	}

	/**
	 * Register the listener to be notified of Back Key pressed
	 * 
	 * @param listener BackPressedListener to notify
	 */
	public void registerBackPressedListener(BackPressedListener listener)
	{
		this.mBackPressedListener = listener;
	}

	/**
	 * Un-register the listener if it is currently listening for Back Key pressed
	 * 
	 * @param listener ProgressListener to notify
	 */
	public void unRegisterBackPressedListener(BackPressedListener listener)
	{
		if (this.mBackPressedListener == listener)
		{
			this.mBackPressedListener = null;
		}
	}
}