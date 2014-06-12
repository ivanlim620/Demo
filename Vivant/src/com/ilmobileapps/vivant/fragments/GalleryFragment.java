package com.ilmobileapps.vivant.fragments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.ilmobileapps.vivant.R;
import com.ilmobileapps.vivant.VivantApplication;
import com.ilmobileapps.vivant.activities.MainActivity;
import com.ilmobileapps.vivant.adapters.GalleryArrayAdapter;
import com.ilmobileapps.vivant.adapters.GalleryArrayAdapter.RatingListener;
import com.ilmobileapps.vivant.models.ArtPiece;
import com.ilmobileapps.vivant.models.Gallery;
import com.ilmobileapps.vivant.utils.IOUtils;

/**
 * This class display all the {@link ArtPiece} in a {@link Gallery} in a {@link ListFragment}.
 * 
 * @author ivanlim
 * @version 1.0
 * @since 2014-05-21
 */
public class GalleryFragment extends ListFragment implements RatingListener, MainActivity.BackPressedListener
{
	// Constants
	public static final String BUNDLE_GALLERY = "gallery";

	// Instance Variables

	/**
	 * Current {@link Gallery} shown
	 */
	private Gallery mGallery;
	/**
	 * List of {@link ArtPiece} in this gallery
	 */
	private List<ArtPiece> mArtPieces;

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null)
		{
			// retrieve gallery
			mGallery = (Gallery) bundle.getSerializable(BUNDLE_GALLERY);

			mArtPieces = new ArrayList<ArtPiece>(mGallery.getPiecesOfArt());

			File aDirectory = getActivity().getDir("GALLERY_" + mGallery.getId(), Context.MODE_PRIVATE);
			File[] arrFiles = aDirectory.listFiles();
			if (arrFiles.length == 0)
			{
				createArtPieces(aDirectory);
			}
			else
			{
				loadArtPieces(arrFiles);
			}
		}

		ArrayAdapter<ArtPiece> adapter = new GalleryArrayAdapter(this, getActivity(), android.R.layout.simple_list_item_1, mArtPieces);
		setListAdapter(adapter);
	}

	/**
	 * Create {@link ArtPiece} and save them to disk
	 */
	private void loadArtPieces(File[] arrFiles)
	{
		for (int i = 0; i < arrFiles.length; i++)
		{
			ArtPiece artPiece = new ArtPiece(Integer.parseInt(arrFiles[i].getName()), mGallery.getId());
			try
			{
				// load the ratings
				String text = IOUtils.readTextFile(arrFiles[i]);
				artPiece.setRating(Float.parseFloat(text));
				mArtPieces.add(artPiece);
			}
			catch (IOException e)
			{
				// Ignore
			}
		}
	}

	/**
	 * Load existing {@link ArtPiece}
	 */
	private void createArtPieces(File aDirectory)
	{
		// if there are no ArtPiece in the gallery saved, then create them.
		for (int i = 0; i < mGallery.getPiecesOfArt(); i++)
		{
			try
			{
				ArtPiece aArtPiece = new ArtPiece(i, mGallery.getId());
				File aFile = new File(aDirectory, Integer.toString(aArtPiece.getId()));
				IOUtils.writeTextFile(aFile, 0.0f);
				mArtPieces.add(aArtPiece);
			}
			catch (IOException e)
			{
				// Ignore
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Listens for BackPress
		((MainActivity) getActivity()).registerBackPressedListener(this);
		// Setup up caret
		((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// Show ActionBar Options Menu
		setHasOptionsMenu(true);

		return inflater.inflate(R.layout.fragment_gallery, null);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.fragment_gallery, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Get item selected and deal with it
		switch (item.getItemId())
		{
			case android.R.id.home: // Up carat in ActionBar is pressed
				doBack();
				return true;
			case R.id.action_help:
				Toast.makeText(getActivity(), R.string.txt_coming_soon, Toast.LENGTH_LONG).show();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void doSubmitRating(ArtPiece anArtPiece)
	{
		// Ratings have been submitted, save the ratings to the file.
		File aDirectory = getActivity().getDir(VivantApplication.FILE_PREFIX_GALLERY + anArtPiece.getGalleryId(), Context.MODE_PRIVATE);
		File aFile = new File(aDirectory, Integer.toString(anArtPiece.getId()));
		try
		{
			IOUtils.writeTextFile(aFile, anArtPiece.getRating());
		}
		catch (IOException e)
		{
			Toast.makeText(getActivity(), R.string.txt_failed_to_save, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void doBack()
	{
		// Back Key has been pressed. Unregister listener, close self and remove the Up carat
		((MainActivity) getActivity()).unRegisterBackPressedListener(this);
		((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getFragmentManager().popBackStack();
	}

	@Override
	public void onDestroyView()
	{
		// Unregister listener
		((MainActivity) getActivity()).unRegisterBackPressedListener(this);
		super.onDestroyView();
	}
}
