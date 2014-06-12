package com.ilmobileapps.vivant.adapters;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;

import com.ilmobileapps.vivant.R;
import com.ilmobileapps.vivant.models.ArtPiece;

/**
 * This class is an {@link ArrayAdapter} for displaying an image and a rating bar. User cannot change the ratings once
 * it has been submitted. When ratings are submitted, the {@link RatingListener} will be informed.
 * 
 * @author ivanlim
 * @version 1.0
 * @since 2014-05-21
 */
public class GalleryArrayAdapter extends ArrayAdapter<ArtPiece> implements Serializable
{
	/**
	 * Classes wishing to be notified of when ratings have been submitted for an {@link ArtPiece}
	 */
	public interface RatingListener
	{
		public void doSubmitRating(ArtPiece anArtPiece);
	}

	/**
	 * UID, for serializing an instance.
	 */
	private static final long serialVersionUID = 8808885445934284188L;

	// Instance Variables

	/**
	 * Application Context
	 */
	private Context mContext;
	/**
	 * Current listener to be inform for ratings submission
	 */
	private RatingListener mListener;

	// Constructors
	public GalleryArrayAdapter(RatingListener listener, Context context, int resource, List<ArtPiece> objects)
	{
		super(context, resource, objects);
		mListener = listener;
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// Recycle views
		View aRowView = convertView;
		if (aRowView == null)
		{
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			aRowView = inflater.inflate(R.layout.list_item_art_piece, parent, false);
		}

		RatingBar aRatingBar = (RatingBar) aRowView.findViewById(R.id.rating);

		final ArtPiece artPiece = getItem(position);
		aRatingBar.setRating(artPiece.getRating());
		// If the artpiece already has a rating, don't allow the ratings to be changed.
		if (artPiece.getRating() == 0)
		{
			// allow ratings to be changed
			aRatingBar.setIsIndicator(false);
			// listen for changes
			aRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener()
				{
					@Override
					public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
					{
						if (fromUser)
						{
							artPiece.setRating(rating);
							mListener.doSubmitRating(artPiece);
						}
					}
				});
		}
		else
		{
			// disallow ratings to be changed
			aRatingBar.setIsIndicator(true);
			// consume any touch event. This is necessary because otherwise user can click on the view and it flashes but
			// nothing happens
			aRowView.setOnTouchListener(new OnTouchListener()
				{
					@Override
					public boolean onTouch(View v, MotionEvent event)
					{
						return true;
					}
				});
		}
		return aRowView;
	}
}
