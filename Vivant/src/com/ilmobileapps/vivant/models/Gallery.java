package com.ilmobileapps.vivant.models;

import java.io.Serializable;

/**
 * Gallery object holds information about each gallery.
 * 
 * @author ivanlim
 * @version 1.0
 * @since 2014-05-21
 */
public class Gallery implements Serializable
{
	/**
	 * version identifier
	 */
	private static final long serialVersionUID = -458098934673994963L;

	// Gallery's identifier
	private int mId;

	// Gallery's address
	private String mAddress;

	// Gallery's suburb
	private String mSuburb;

	// Gallery's postcode
	private String mPostcode;

	// Gallery's state
	private String mState;

	// Gallery's Latitude
	private double mLat;

	// Gallery's Longtitude
	private double mLng;

	// Gallery's number of pieces of art
	private int mPiecesOfArt;

	public Gallery(int id, String address, String suburb, String postcode, String state, double lat, double lng, int piecesOfArt)
	{
		mId = id;
		mAddress = address;
		mSuburb = suburb;
		mPostcode = postcode;
		mState = state;
		mLat = lat;
		mLng = lng;
		mPiecesOfArt = piecesOfArt;
	}

	/**
	 * @return Gallery's identifier
	 */
	public int getId()
	{
		return mId;
	}

	/**
	 * Set Gallery's identifier
	 * 
	 * @param id
	 */
	public void setId(int id)
	{
		mId = id;
	}

	/**
	 * @return Gallery's address
	 */
	public String getAddress()
	{
		return mAddress;
	}

	/**
	 * Set Gallery's address
	 * 
	 * @param address
	 */
	public void setAddress(String address)
	{
		mAddress = address;
	}

	/**
	 * @return Gallery's suburb
	 */
	public String getSuburb()
	{
		return mSuburb;
	}

	/**
	 * Set Gallery's suburb
	 * 
	 * @param suburb
	 */
	public void setSuburb(String suburb)
	{
		mSuburb = suburb;
	}

	/**
	 * @return Gallery's Postcode
	 */
	public String getPostcode()
	{
		return mPostcode;
	}

	/**
	 * Set Gallery's postcode
	 * 
	 * @param postcode
	 */
	public void setPostcode(String postcode)
	{
		mPostcode = postcode;
	}

	/**
	 * @return Gallery's state
	 */
	public String getState()
	{
		return mState;
	}

	/**
	 * Set Gallery's state
	 * 
	 * @param state
	 */
	public void setState(String state)
	{
		mState = state;
	}

	/**
	 * @return Gallery's latitude
	 */
	public double getLat()
	{
		return mLat;
	}

	/**
	 * Set Gallery's latitude
	 * 
	 * @param lat
	 */
	public void setLat(double lat)
	{
		mLat = lat;
	}

	/**
	 * @return Gallery's longtitude
	 */
	public double getLng()
	{
		return mLng;
	}

	/**
	 * Set Gallery's longtitude
	 * 
	 * @param lng
	 */
	public void setLng(double lng)
	{
		mLng = lng;
	}

	/**
	 * @return Gallery's number of pieces of art
	 */
	public int getPiecesOfArt()
	{
		return mPiecesOfArt;
	}

	/**
	 * Set Gallery's number of pieces of art
	 * 
	 * @param piecesOfArt
	 */
	public void setPiecesOfArt(int piecesOfArt)
	{
		mPiecesOfArt = piecesOfArt;
	}

	/**
	 * Returns a full address by combining the address, suburb, state and postcode.
	 * 
	 * @return A String with the full address.
	 */
	public String getFullAddress()
	{
		StringBuffer strbuf = new StringBuffer();
		if (mAddress != null && mAddress.length() != 0) strbuf.append(mAddress).append(", ");
		if (mSuburb != null && mSuburb.length() != 0) strbuf.append(mSuburb).append(", ");
		if (mState != null && mState.length() != 0) strbuf.append(mState).append(", ");
		if (mPostcode != null && mPostcode.length() != 0) strbuf.append(mPostcode).append(", ");
		return strbuf.substring(0, strbuf.length() - 2);
	}
}
