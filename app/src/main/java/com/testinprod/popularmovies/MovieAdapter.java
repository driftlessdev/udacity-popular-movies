package com.testinprod.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.testinprod.popularmovies.api.TheMovieDBConsts;
import com.testinprod.popularmovies.models.MovieModel;

import java.util.ArrayList;

/**
 * Created by Tim on 7/8/2015.
 */
public class MovieAdapter extends BaseAdapter {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<MovieModel> mMovieModels;

    public MovieAdapter(Context context, ArrayList<MovieModel> movies)
    {
        mContext = context;
        mMovieModels = movies;
    }
    @Override
    public int getCount() {
        return mMovieModels.size();
    }

    @Override
    public Object getItem(int position) {
        return mMovieModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<MovieModel> getMovies()
    {
        return mMovieModels;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView movieThumb;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            movieThumb = (ImageView) inflater.inflate(R.layout.movie_item, parent, false);
        }
        else
        {
            movieThumb = (ImageView) convertView;
        }

        String posterURL = mMovieModels.get(position).getPosterPath();
        if(!posterURL.isEmpty())
        {
            Picasso.with(mContext)
                    .load(TheMovieDBConsts.POSTER_BASE_URL + posterURL)
                    .into(movieThumb);
        }

        return movieThumb;
    }
}
