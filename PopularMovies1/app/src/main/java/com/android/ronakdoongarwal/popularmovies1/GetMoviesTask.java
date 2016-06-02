package com.android.ronakdoongarwal.popularmovies1;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetMoviesTask extends AsyncTask<Void, Void, Void> {

    private MainActivityFragment mActivityFragment = null;
    private String mSortByParam = "";
    private String mSortByParamDefault = "";
    private String mAPIKey = "";
    private String mBaseImgStr = "";

    public GetMoviesTask(MainActivityFragment activityFragment) {
        this.mActivityFragment = activityFragment;
        mSortByParam = mActivityFragment.getString(R.string.sort_param);
        mSortByParamDefault = mActivityFragment.getString(R.string.sort_param_default);
        mAPIKey = mActivityFragment.getString(R.string.api_key);
        mBaseImgStr = mActivityFragment.getString(R.string.api_base_image_url);
    }

    protected Void doInBackground(Void... voids) {

            try {

                // get the shared preferences
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mActivityFragment.getActivity());

                // get the sort method
                String sortByParam = sharedPref.getString(mSortByParam, mSortByParamDefault);

                // clear the list
                mActivityFragment.clearMovieList();

                // set up for reading the json response
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // json response string.
                String jsonStr = null;

                try {
                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("https")
                            .authority("api.themoviedb.org")
                            .appendPath("3")
                            .appendPath("movie")
                            .appendPath(sortByParam)
                            .appendQueryParameter("api_key", mAPIKey);

                    // build the url string
                    String urlStr = builder.build().toString();

                    // create the url
                    URL url = new URL(urlStr);
                    Log.d("URL", urlStr);
                    // create the request
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // read the input stream
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        jsonStr = null;
                    }

                    // read the response
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        jsonStr = null;
                    }
                    jsonStr = buffer.toString();
                } catch (MalformedURLException e) {
                    Log.e("GetMoviesTask", "Error ", e);
                    jsonStr = null;
                } catch (IOException e) {
                    Log.e("GetMoviesTask", "Error ", e);
                    jsonStr = null;
                } finally{
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e("GetMoviesTask", "Error closing stream", e);
                        }
                    }
                }

                // set up the json objects
                JSONObject movieDetailObj = null;
                JSONArray resultArray = null;

                // vars to pull the needed data from the json response
                String originalTitle;
                String imagePosterUrl;
                String overview;
                double userRating;
                String releaseDate;

                try {
                    JSONObject json = new JSONObject(jsonStr);
                    resultArray = json.getJSONArray("results");

                    for(int i = 0; i < resultArray.length(); i++) {
                        movieDetailObj = resultArray.getJSONObject(i);

                        // get the data for each movie
                        originalTitle = movieDetailObj.getString("original_title");
                        imagePosterUrl = movieDetailObj.getString("poster_path");
                        overview = movieDetailObj.getString("overview");
                        userRating = movieDetailObj.getDouble("vote_average");
                        releaseDate = movieDetailObj.getString("release_date");

                        // create a new MovieParcel object
                        MovieParcel tempParcel = new MovieParcel(originalTitle,
                                imagePosterUrl,
                                overview,
                                userRating,
                                releaseDate);

                        // add to the movie list
                        mActivityFragment.addToMovieList(tempParcel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // now that we have the data, determine the dimensions of an image to be able to
                // resize the images to display within the gridView without any gaps
                final Bitmap image;
                try {
                    // find the first posterURL that is not null
                    for(int i = 0; i < mActivityFragment.getMovieListSize(); i++) {

                        String posterURL = mActivityFragment.getPosterURL(i);

                        if((posterURL != null) && (!posterURL.isEmpty()) && (!posterURL.equals("null"))) {
                            // load an image to retrieve the dimensions
                            image = Picasso
                                .with(mActivityFragment.getActivity())
                                .load(mBaseImgStr + mActivityFragment.getPosterURL(i)).get();

                            mActivityFragment.setImageDimensions(image.getWidth(), image.getHeight());
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        // if there are problems obtaining api data, such as an invalid key, display a toast message
        if(mActivityFragment.getMovieListSize() == 0) {
            Toast.makeText(mActivityFragment.getContext(), R.string.no_movies_error, Toast.LENGTH_LONG).show();
        }
        else {
            // sometimes, the getView is called via the android framework;
            // this needs to be set up here after we have obtained the image dimension as it is
            //    used in the getView method;  otherwise, a divide-by-zero exception is called
            if (mActivityFragment.isMovieListAdapterNull()) {
                mActivityFragment.initUI();
            }

            // update the gridView
            mActivityFragment.updateData();
        }
    }
}