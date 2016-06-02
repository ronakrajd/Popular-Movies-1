package com.android.ronakdoongarwal.popularmovies1;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieListAdapter extends ArrayAdapter<MovieParcel> {
    private Context mContext;
    private LayoutInflater mInflater;
    private MainActivityFragment mActivityFragment;
    private String mBaseImgStr = "";

    public MovieListAdapter(Context context, MainActivityFragment activityFragment, List<MovieParcel> imageUrls) {
        super(context, R.layout.movie_grid_image, imageUrls);

        this.mContext = context;
        this.mActivityFragment = activityFragment;
        this.mBaseImgStr = mActivityFragment.getString(R.string.api_base_image_url);

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            MovieParcel movieParcel = getItem(position);

            if (null == convertView) {
                convertView = mInflater.inflate(R.layout.movie_grid_image, parent, false);
            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                convertView.setOnTouchListener(new View.OnTouchListener() {
//                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        v
//                                .findViewById(R.id.card_view)
//                                .getBackground()
//                                .setHotspot(event.getX(), event.getY());
//
//                        return(false);
//                    }
//                });
//            }
            // get the number of columns
            //mActivityFragment.mNumGridViewCols = mActivityFragment.mGridView.getNumColumns();

            // get the screen width
            int screenWidth = mActivityFragment.getDisplayMetricsWidth();

            // eventual width of each image
            int newImageWidth = (screenWidth / mActivityFragment.getNumGridViewCols());

            // original dimensions : mImageWidth and mImageHeight
            int newImageHeight = ((newImageWidth * mActivityFragment.getImageHeight()) / mActivityFragment.getImageWidth());

            // get the poster URL
            String posterURL = movieParcel.getImagePosterURL();
            TextView textView = (TextView) convertView.findViewById(R.id.movie_name_tv);
            textView.setText(movieParcel.getOriginalTitle());
            // sometimes, the poster URL from the API returns null;
            // if it does, load a temporary image if there is no image available
            if ((posterURL == null) || (posterURL.isEmpty()) || (posterURL.equals("null"))) {
                ImageView tempImageView = (ImageView) (convertView.findViewById(R.id.gridImage));
                tempImageView.setImageResource(R.drawable.image);
            } else {
                Glide
                    .with(mContext)
                    .load(mBaseImgStr + posterURL)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into((ImageView) (convertView.findViewById(R.id.gridImage)));
            }
        }
        catch(IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
