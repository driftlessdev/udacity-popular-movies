package com.testinprod.popularmovies;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.testinprod.popularmovies.models.Movie;
import com.testinprod.popularmovies.tasks.MovieDiscoverTask;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieGridFragment extends Fragment implements MovieDiscoverTask.MovieDiscoverTaskResults{
    private TextView mTextView;

    @Override
    public void handleMovieDiscoverResults(ArrayList<Movie> movies) {
        String text = "";

        for(Movie movie : movies)
        {
            text += movie.getTitle() + "\n";
        }

        mTextView.setText(text);
    }

    public MovieGridFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);
        mTextView = (TextView) rootView.findViewById(R.id.tvHello);
        MovieDiscoverTask popularTask = new MovieDiscoverTask(this);
        popularTask.execute("test");
        return rootView;
    }
}
