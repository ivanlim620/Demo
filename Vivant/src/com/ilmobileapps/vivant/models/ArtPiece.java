package com.ilmobileapps.vivant.models;

/**
 * ArtPiece object holds information about each art piece within a gallery.
 * 
 * @author ivanlim
 * @version 1.0
 * @since 2014-05-21
 */
public class ArtPiece
{
	// ArtPiece identifier
	int mId;
	// Gallery's identifier
	int mGalleryId;
	// ArtPiece rating
	float mRating;

	/**
	 * Construct a new ArtPiece with defaults
	 */
	public ArtPiece()
	{
		mRating = 0.0f;
	}

	/**
	 * Construct a new ArtPiece with defaults
	 * 
	 * @param id ArtPiece identifier
	 * @param galleryId Gallery's identifier
	 */
	public ArtPiece(int id, int galleryId)
	{
		mId = id;
		mGalleryId = galleryId;
	}

	/**
	 * @return ArtPiece identifier
	 */
	public int getId()
	{
		return mId;
	}

	/**
	 * Set ArtPiece identifier
	 * 
	 * @param id
	 */
	public void setId(int id)
	{
		mId = id;
	}

	/**
	 * @return Gallery's identifier
	 */
	public int getGalleryId()
	{
		return mGalleryId;
	}

	/**
	 * Set Gallery's identifier
	 * 
	 * @param galleryId
	 */
	public void setGalleryId(int galleryId)
	{
		mGalleryId = galleryId;
	}

	/**
	 * @return ArtPiece rating
	 */
	public float getRating()
	{
		return mRating;
	}

	/**
	 * Set ArtPiece rating
	 * 
	 * @param rating
	 */
	public void setRating(float rating)
	{
		mRating = rating;
	}
}
