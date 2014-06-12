package com.ilmobileapps.vivant.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ilmobileapps.vivant.R;
import com.ilmobileapps.vivant.activities.MainActivity;

/**
 * Splash screen for the app.
 * 
 * @author ivanlim
 * @version 1.0
 * @since 2014-05-21
 */
public class SplashFragment extends Fragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Setup the ActionBar
		((MainActivity) getActivity()).getSupportActionBar().hide();

		final View view = inflater.inflate(R.layout.fragment_splash, container, false);
		return view;
	}
}