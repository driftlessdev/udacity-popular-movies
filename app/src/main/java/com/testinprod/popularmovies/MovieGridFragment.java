package com.testinprod.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.testinprod.popularmovies.models.Movie;
import com.testinprod.popularmovies.tasks.MovieDiscoverTask;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieGridFragment extends Fragment implements MovieDiscoverTask.MovieDiscoverTaskResults{
    private GridView mMovieGrid;

    @Override
    public void handleMovieDiscoverResults(ArrayList<Movie> movies) {
        mMovieGrid.setAdapter(new MovieAdapter(getActivity(), movies));
    }

    public MovieGridFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // TODO: Loading spinner
        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);
        mMovieGrid = (GridView) rootView.findViewById(R.id.gvMovies);

        MovieDiscoverTask popularTask = new MovieDiscoverTask(this);
        popularTask.execute("test");
        return rootView;
    }
}
