package com.testinprod.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.testinprod.popularmovies.api.TheMovieDBConsts;
import com.testinprod.popularmovies.models.MovieModel;

import java.util.ArrayList;

import timber.log.Timber;

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
        Timber.tag(LOG_TAG);
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
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.movie_item, parent, false);
        }

        ImageView movieThumb = (ImageView) convertView.findViewById(R.id.ivMoviePoster);
        View rlNoPoster = convertView.findViewById(R.id.rlNoPoster);

        MovieModel movie = mMovieModels.get(position);
        String posterURL = movie.getPosterPath();
        TextView tvPlaceholder = (TextView) convertView.findViewById(R.id.tvTitlePlaceholder);
        Timber.v("Poster Path: " + TheMovieDBConsts.POSTER_BASE_URL + posterURL);
        if(posterURL != null && !posterURL.isEmpty())
        {
            rlNoPoster.setVisibility(View.GONE);
            movieThumb.setVisibility(View.VISIBLE);
            Picasso.with(mContext)
                    .load(TheMovieDBConsts.POSTER_BASE_URL + posterURL)
                    .placeholder(R.drawable.movie_board)
                    .into(movieThumb);
        }
        else
        {
            Timber.v("No poster, showing title of " + movie.getTitle());
            movieThumb.setVisibility(View.GONE);
            rlNoPoster.setVisibility(View.VISIBLE);
            tvPlaceholder.setText(movie.getTitle());
        }

        return convertView;
    }
}
