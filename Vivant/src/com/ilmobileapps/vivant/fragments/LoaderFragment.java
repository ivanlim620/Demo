package com.ilmobileapps.vivant.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.ilmobileapps.vivant.models.Gallery;
import com.ilmobileapps.vivant.utils.IOUtils;

/**
 * This class is non UI fragment. It's is to wrap an AsyncTask to fetch JSON data from a URL and return it as a
 * {@link List} or {@link Gallery}.
 * 
 * @author ivanlim
 * @version 1.0
 * @since 2014-05-21
 */
public class LoaderFragment extends Fragment
{

	/**
	 * Classes wishing to be notified when the data has been fetch.
	 */
	public interface ProgressListener
	{
		/**
		 * Notifies that the task has completed
		 * 
		 * @param data result of the task List<Gallery>
		 */
		public void onCompletion(List<Gallery> data);

	}

	// Instance Variables

	/**
	 * Listener wishing to be notified when task is complete.
	 */
	private ProgressListener mProgressListener;

	/**
	 * The AsyncTask
	 */
	private LoadingTask mTask;

	/**
	 * List of galleries fetched.
	 */
	private List<Gallery> mGalleries;

	/**
	 * Is true when JSON fetch is completed.
	 */
	private boolean mIsCompleted;

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		// Keep this Fragment around even during config changes
		setRetainInstance(true);
	}

	/**
	 * Returns the fetched data
	 * 
	 * @return the fetched data as list of Gallery.
	 */
	public List<Gallery> getGalleries()
	{
		return mGalleries;
	}

	/**
	 * Returns true if data is ready.
	 * 
	 * @return true if data is ready.
	 * @see #getGalleries()
	 */
	public boolean isCompleted()
	{
		return mIsCompleted;
	}

	/**
	 * Register listener to be notified of updates.
	 * 
	 * @param listener ProgressListener to notify
	 */
	public void registerProgressListener(ProgressListener listener)
	{
		mProgressListener = listener;
	}

	/**
	 * Un-register listener and remove from being notified.
	 * 
	 * @param listener ProgressListener to un-register
	 */
	public boolean unRegisterProgressListener(ProgressListener listener)
	{
		if (mProgressListener == listener)
		{
			mProgressListener = null;
		}

		return false;
	}

	/**
	 * Starts loading the data
	 * 
	 * @param url The URL to where the JSON data needs to be fetched from.
	 */
	public void startLoading(String url)
	{
		mIsCompleted = false;

		mTask = new LoadingTask();
		mTask.execute(url);
	}

	private class LoadingTask extends AsyncTask<String, Integer, List<Gallery>>
	{
		// To monitor if 3 seconds have passed. Task must take at least 3 seconds.
		long mStartTime;

		@Override
		protected List<Gallery> doInBackground(String... params)
		{
			mStartTime = System.currentTimeMillis();

			mGalleries = new ArrayList<Gallery>();

			JSONArray aJSONArray;
			try
			{
				// fetch and create the JSONArray from within the "galleries" field.
				aJSONArray = new JSONObject(IOUtils.getContentFromURL(params[0])).optJSONArray("galleries");
			}
			catch (JSONException e)
			{
				Log.e(this.getClass().getSimpleName(), "doInBackground: JSONException - There was an error parsing / retrieving galleries array from the feed.");
				Log.e(this.getClass().getSimpleName(), "doInBackground: JSONException - " + e.getMessage(), e);
				return mGalleries;
			}
			catch (IOException e)
			{
				Log.e(this.getClass().getSimpleName(), "doInBackground: IOException - " + e.getMessage(), e);
				return mGalleries;
			}

			// Extracting information
			for (int i = 0; i < aJSONArray.length(); i++)
			{
				try
				{
					JSONObject oneObject = aJSONArray.getJSONObject(i);
					String address = oneObject.getString("address");
					String suburb = oneObject.getString("suburb");
					String postcode = oneObject.getString("postcode");
					String state = oneObject.getString("state");
					int identifier = oneObject.getInt("id");
					double lat = oneObject.getDouble("lat");
					double lon = oneObject.getDouble("lon");
					int piecesOfArt = oneObject.getInt("piecesofart");

					// Add to list.
					mGalleries.add(new Gallery(identifier, address, suburb, postcode, state, lat, lon, piecesOfArt));
				}
				catch (JSONException e)
				{
					Log.e(this.getClass().getSimpleName(), "doInBackground: JSONException - There was an error parsing a news item.");
					Log.e(this.getClass().getSimpleName(), "doInBackground: JSONException - " + e.getMessage(), e);
				}
			}
			// Do not return if it has taken less than 3 seconds.
			while ((System.currentTimeMillis() - mStartTime) < 3000)
			{
				try
				{
					Thread.sleep(System.currentTimeMillis() - mStartTime);
				}
				catch (InterruptedException e)
				{
				}
			}

			mIsCompleted = true;
			return mGalleries;
		}

		@Override
		protected void onPostExecute(List<Gallery> result)
		{
			mTask = null;
			if (mProgressListener != null)
			{
				// Notify of completion.
				mProgressListener.onCompletion(result);
			}
		}

	}
}