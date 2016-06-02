package com.android.ronakdoongarwal.popularmovies1;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DetailActivityFragment extends Fragment {

    private TextView mTitle = null;
    private TextView mUserRating = null;
    private TextView mReleaseDate = null;
    private TextView mOverview = null;
    private ImageView mPoster = null;

    // formatters for the date
    private SimpleDateFormat mFormatFromAPI = null;
    private SimpleDateFormat mFormatOutput = null;
    private DecimalFormat mFormatDecimal = null;

    private String mBaseImgStr = "";

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mFormatFromAPI = new SimpleDateFormat(getString(R.string.format_date_api));
        mFormatOutput = new SimpleDateFormat(getString(R.string.format_date_output));
        mFormatDecimal = new DecimalFormat(getString(R.string.format_rating_output));
        mBaseImgStr = getString(R.string.api_base_image_url);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.fragment_detail, container, false);

        mTitle = (TextView) returnView.findViewById(R.id.detail_original_title);
        mUserRating = (TextView) returnView.findViewById(R.id.detail_rating);
        mReleaseDate = (TextView) returnView.findViewById(R.id.detail_release_date);
        mOverview = (TextView) returnView.findViewById(R.id.detail_overview);
        mPoster = (ImageView) returnView.findViewById(R.id.detail_thumbnail);

        return returnView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // get the activity intent
        Intent intent = getActivity().getIntent();

        // get the movie data to display in this activity
        MovieParcel movieParcel = (MovieParcel)(intent.getParcelableExtra(getString(R.string.parcel_data)));

        // get the release date to reformat
        String releaseDateStr = "";
        String outputDateStr = getString(R.string.date_unknown);

        // format the date
        try {
            releaseDateStr = movieParcel.getReleaseDate();
            outputDateStr = mFormatOutput.format(mFormatFromAPI.parse(releaseDateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        // format the user rating
        String outputRatingStr = mFormatDecimal.format(movieParcel.getUserRating());
        outputRatingStr += getString(R.string.rating_divisor);

        // get the poster URL
        String posterURL = movieParcel.getImagePosterURL();

        // update the fields
        mTitle.setText(movieParcel.getOriginalTitle());
        mUserRating.setText(outputRatingStr);
        mReleaseDate.setText(outputDateStr);
        mOverview.setText(movieParcel.getOverview());

        // display the poster image;
        // sometimes the URL is returned as null or empty from the API
        if ((posterURL == null) || (posterURL.isEmpty()) || (posterURL.equals("null"))) {
            mPoster.setImageResource(R.drawable.image);
        } else {
            Picasso.with(getActivity())
                    .load(mBaseImgStr + movieParcel.getImagePosterURL())
                    .into(mPoster);
        }
    }
}
