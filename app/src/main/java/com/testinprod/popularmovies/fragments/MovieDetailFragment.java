package com.testinprod.popularmovies.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.testinprod.popularmovies.R;
import com.testinprod.popularmovies.models.Movie;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {

    public static MovieDetailFragment newInstance(Movie movie)
    {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(Movie.EXTRA_MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Bundle args = getArguments();
        Movie movie = args.getParcelable(Movie.EXTRA_MOVIE);
        TextView temp = (TextView) rootView.findViewById(R.id.tvTemp);
        temp.setText(movie.getTitle() + " [" + movie.getID() + "]");

        return rootView;
    }
}
