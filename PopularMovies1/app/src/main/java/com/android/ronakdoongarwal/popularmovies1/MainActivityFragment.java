package com.android.ronakdoongarwal.popularmovies1;


import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

public class MainActivityFragment extends Fragment {

    // gridView to display the grid of movie posters
    private GridView mGridView = null;

    // list adapter to support the gridView
    private MovieListAdapter mMovieListAdapter = null;

        // arrayList of MovieParcel data to support the gridView and the detail activity
        private List<MovieParcel> mMovieList = new ArrayList<MovieParcel>();

    // asyncTask to get the movies from the API
    private GetMoviesTask mGetMoviesTask = null;

    // displayMetrics to calculate image dimensions
    private DisplayMetrics mDisplayMetrics = new DisplayMetrics();

    // calculated width of an image in the gridView (assuming all are same dimensions)
    // default to the queried width
    private int mImageWidth = 185;

    // calculated height of an image in the gridView (assuming all are same dimensions)
    // default to a height
    private int mImageHeight = 100;

    // number of gridView columns
    private int mNumGridViewCols = 2;

    // parameter to indicate the sorting method for the movies (popular/vote)
    private String mSortByParam = "";

    public MainActivityFragment() {
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // enable the menu option to select the settings menu
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume () {
        super.onResume();

        // get the movies
        mGetMoviesTask = null;
        mGetMoviesTask = new GetMoviesTask(this);
        mGetMoviesTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView)returnView.findViewById(R.id.gridView);

        return returnView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // get the displayMetrics
        mDisplayMetrics = getResources().getDisplayMetrics();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(getActivity(), SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void clearMovieList() {
        mMovieList.clear();
    }

    public void addToMovieList(MovieParcel tempParcel) {
        mMovieList.add(tempParcel);
    }

    public void updateData() {
        mMovieListAdapter.notifyDataSetChanged();
    }

    public void setImageDimensions(int width, int height) {
        mImageWidth = width;
        mImageHeight = height;
    }

    public int getImageWidth() {
        return mImageWidth;
    }

    public int getImageHeight() {
        return mImageHeight;
    }

    public int getDisplayMetricsWidth() {
        return mDisplayMetrics.widthPixels;
    }

    public int getNumGridViewCols() {
        return mGridView.getNumColumns();
    }

    public String getPosterURL(int position) {
        return mMovieList.get(position).getImagePosterURL();
    }

    public int getMovieListSize() {
        return mMovieList.size();
    }

    public boolean isMovieListAdapterNull() {
        return (mMovieListAdapter == null);
    }

    public void openDetailActivity(MovieParcel movieParcel) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(getString(R.string.parcel_data), movieParcel);
        startActivity(intent);
    }

    public void initUI() {

        // get the number of gridView columns
        mNumGridViewCols = mGridView.getNumColumns();

        // this needs to be set after the onPostExecute due to calculating the width/height
        // within the background thread;  sometimes getView is called before that AsyncTask is finished
        // causing a crash to occur
        mMovieListAdapter = new MovieListAdapter(getActivity(), this, mMovieList);

        mGridView.setAdapter(mMovieListAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MovieParcel tempParcel = mMovieListAdapter.getItem(position);
                openDetailActivity(tempParcel);
            }
        });
    }
}