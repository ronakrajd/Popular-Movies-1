package com.android.ronakdoongarwal.popularmovies1;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieParcel implements Parcelable {
    private String mOriginalTitle = "";
    private String mImagePosterUrl = "";
    private String mOverview = "";
    private double mUserRating = 0.0;
    private String mReleaseDate = "";

    public MovieParcel(String title,
                       String posterUrl,
                       String overview,
                       double rating,
                       String releaseDate) {
        this.mOriginalTitle = title;
        this.mImagePosterUrl = posterUrl;
        this.mOverview = overview;
        this.mUserRating = rating;
        this.mReleaseDate = releaseDate;
    }

    private MovieParcel(Parcel in) {
        this.mOriginalTitle = in.readString();
        this.mImagePosterUrl = in.readString();
        this.mOverview = in.readString();
        this.mUserRating = in.readDouble();
        this.mReleaseDate = in.readString();
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getImagePosterURL() {
        return mImagePosterUrl;
    }

    public String getOverview() {
        return mOverview;
    }

    public double getUserRating() {
        return mUserRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return mOriginalTitle + "--" +
                mImagePosterUrl + "--" +
                mOverview + "--" +
                mUserRating + "--" +
                mReleaseDate; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mOriginalTitle);
        parcel.writeString(mImagePosterUrl);
        parcel.writeString(mOverview);
        parcel.writeDouble(mUserRating);
        parcel.writeString(mReleaseDate);
    }

    public static final Creator<MovieParcel> CREATOR = new Creator<MovieParcel>() {
        @Override
        public MovieParcel createFromParcel(Parcel parcel) {
            return new MovieParcel(parcel);
        }

        @Override
        public MovieParcel[] newArray(int i) {
            return new MovieParcel[i];
        }

    };
}