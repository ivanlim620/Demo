package com.ilmobileapps.vivant.fragments;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ilmobileapps.vivant.R;
import com.ilmobileapps.vivant.activities.MainActivity;
import com.ilmobileapps.vivant.models.Gallery;

/**
 * This class display a marker for all the available Galleries. It display a custom {@link InfoWindow} via a
 * {@link PopupAdapter}.
 * 
 * @author ivanlim
 * @version 1.0
 * @since 2014-05-21
 */
public class MapFragment extends SupportMapFragment implements OnInfoWindowClickListener
{
	/**
	 * Custom InfoWindow to hide whats in the Marker snippet's.
	 */
	class PopupAdapter implements InfoWindowAdapter
	{
		LayoutInflater inflater = null;

		PopupAdapter(LayoutInflater inflater)
		{
			this.inflater = inflater;
		}

		@Override
		public View getInfoContents(Marker marker)
		{
			View popup = inflater.inflate(R.layout.map_popup, null);
			TextView tv = (TextView) popup.findViewById(R.id.title);
			tv.setText(marker.getTitle());
			return (popup);
		}

		@Override
		public View getInfoWindow(Marker marker)
		{
			return (null);
		}
	}

	/**
	 * Creates a map fragment, using default options.
	 */
	public static MapFragment newInstance()
	{
		return new MapFragment();
	}

	// Constants

	// LatLntBounds for Australia
	final LatLngBounds AUSTRALIA = new LatLngBounds(new LatLng(-44, 113), new LatLng(-10, 154));

	// List of Gallery to display on Map
	private List<Gallery> mGalleries;

	// Build a LatLngBounds
	private LatLngBounds.Builder mLatLngBuilder;

	// This is in-efficient if there are a lot of markers on the Map, but hey, this is just a test
	private SparseArray<Gallery> mSparseGalleryMap;

	/**
	 * Add a marker for each Gallery. Hide the Gallery Id in the marker's snippet.
	 */
	private void addMarkers()
	{
		for (Iterator<Gallery> iterator = mGalleries.iterator(); iterator.hasNext();)
		{
			Gallery aGallery = (Gallery) iterator.next();
			mSparseGalleryMap.put(aGallery.getId(), aGallery);

			LatLng aLatLng = new LatLng(aGallery.getLat(), aGallery.getLng());
			mLatLngBuilder.include(aLatLng);
			StringBuffer strBuf = new StringBuffer();
			strBuf.append(aGallery.getPiecesOfArt());
			strBuf.append(" ");
			if (aGallery.getPiecesOfArt() > 1)
			{
				strBuf.append(getResources().getString(R.string.frag_map_pieces_of_art));
			}
			else
			{
				strBuf.append(getResources().getString(R.string.frag_map_piece_of_art));
			}
			getMap().addMarker(new MarkerOptions().position(aLatLng).title(strBuf.toString()).snippet(Integer.toString(aGallery.getId())));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Setup the ActionBar
		((MainActivity) getActivity()).getSupportActionBar().show();
		((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

		// Inflate the view
		View view = super.onCreateView(inflater, container, savedInstanceState);

		if (!servicesConnected()) { return view; }

		// Prepare the map
		final GoogleMap map = getMap();
		mLatLngBuilder = LatLngBounds.builder();

		// Prepare for Customise InfoWindow
		map.setInfoWindowAdapter(new PopupAdapter(inflater));
		map.setOnInfoWindowClickListener(this);

		if (savedInstanceState == null && mGalleries == null)
		{
			// Initialise the map to overlook Australia
			map.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(AUSTRALIA.getCenter()).zoom(3).build()));
		}

		// Create Markers on the map - one for each gallery.
		// Note: For efficiency, we should not display markers off screen. This is not considered here.
		mSparseGalleryMap = new SparseArray<Gallery>();
		mGalleries = ((MainActivity) getActivity()).getGalleries();

		if (mGalleries == null) return view;

		// Lets add all the markers, one for each Gallery
		addMarkers();

		if (savedInstanceState == null)
		{
			// We want to zoom into the areas where all the markers are if the Map has just been created.
			map.setOnMapLoadedCallback(new OnMapLoadedCallback()
				{
					@Override
					public void onMapLoaded()
					{
						map.animateCamera(CameraUpdateFactory.newLatLngBounds(mLatLngBuilder.build(), 15));
					}
				});
		}

		return view;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		// release the markers
		mSparseGalleryMap.clear();
	}

	@Override
	public void onInfoWindowClick(Marker marker)
	{
		// Launch the GalleryFragment
		Gallery gallery = mSparseGalleryMap.get(Integer.parseInt(marker.getSnippet()));

		// Create new fragment and transaction
		Fragment newFragment = new GalleryFragment();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();

		// Set Arguments
		Bundle bundle = new Bundle();
		bundle.putSerializable(GalleryFragment.BUNDLE_GALLERY, gallery);
		newFragment.setArguments(bundle);

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack
		// Commit the transaction
		transaction.add(android.R.id.content, newFragment);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	// Code borrowed from Google to detect if Google Play services is installed.

	/*
	 * Define a request code to send to Google Play services This code is returned in Activity.onActivityResult
	 */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	// Define a DialogFragment that displays the error dialog

	public static class ErrorDialogFragment extends DialogFragment
	{
		// Global field to contain the error dialog
		private Dialog mDialog;

		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment()
		{
			super();
			mDialog = null;
		}

		// Set the dialog to display
		public void setDialog(Dialog dialog)
		{
			mDialog = dialog;
		}

		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			return mDialog;
		}
	}

	/*
	 * Handle results returned to the FragmentActivity by Google Play services
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// Decide what to do based on the original request code
		switch (requestCode)
		{
			case CONNECTION_FAILURE_RESOLUTION_REQUEST:
				/*
				 * If the result code is Activity.RESULT_OK, try to connect again
				 */
				switch (resultCode)
				{
					case Activity.RESULT_OK:
						break;
				}
		}
	}

	private boolean servicesConnected()
	{
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode)
		{
			// In debug mode, log the status
			Log.d("Activity Recognition", "Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason
		}
		else
		{
			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);

			// If Google Play services can provide an error dialog
			if (errorDialog != null)
			{
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);
				// Show the error dialog in the DialogFragment
				errorFragment.show(getFragmentManager(), "Activity Recognition");
			}
			return false;
		}
	}
}
