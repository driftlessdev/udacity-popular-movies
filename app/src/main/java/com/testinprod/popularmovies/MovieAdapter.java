package com.testinprod.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.testinprod.popularmovies.models.Movie;

import java.util.ArrayList;

/**
 * Created by Tim on 7/8/2015.
 */
public class MovieAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Movie> mMovies;

    public MovieAdapter(Context context, ArrayList<Movie> movies)
    {
        mContext = context;
        mMovies = movies;
    }
    @Override
    public int getCount() {
        return mMovies.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<Movie> getMovies()
    {
        return mMovies;
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

        //Picasso.with(parent.getContext()).load(artist.images.get(0).url).into(artistImage);
        Picasso.with(mContext).load(mMovies.get(position).getPosterPath()).into(movieThumb);
        return movieThumb;
    }
}
