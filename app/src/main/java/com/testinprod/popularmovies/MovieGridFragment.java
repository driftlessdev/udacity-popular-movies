package com.testinprod.popularmovies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
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
    private static final String LOG_TAG = MovieGridFragment.class.getSimpleName();
    private static final String MOVIE_LIST = "movies.list";
    private static final String MOVIE_SORT = "movies.sort";
    private GridView mMovieGrid;
    private String mSortKey;

    @Override
    public void handleMovieDiscoverResults(ArrayList<Movie> movies) {
        mMovieGrid.setAdapter(new MovieAdapter(getActivity(), movies));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MOVIE_SORT, mSortKey);
        MovieAdapter adapter = (MovieAdapter) mMovieGrid.getAdapter();
        outState.putParcelableArrayList(MOVIE_LIST, adapter.getMovies());
    }

    public MovieGridFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "onStart");
        refreshGrid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // TODO: Loading spinner
        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);
        mMovieGrid = (GridView) rootView.findViewById(R.id.gvMovies);

        if(savedInstanceState != null)
        {
            Log.v(LOG_TAG, "Restoring state");
            ArrayList<Movie> movies =  savedInstanceState.getParcelableArrayList(MOVIE_LIST);
            MovieAdapter adapter = new MovieAdapter(getActivity(), movies);
            mMovieGrid.setAdapter(adapter);
            mSortKey = savedInstanceState.getString(MOVIE_SORT);
        }
        else
        {
            refreshGrid();
        }


        return rootView;
    }

    private void refreshGrid()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortKey = preferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        sortKey += "." + preferences.getString(getString(R.string.pref_sort_dir_key), getString(R.string.pref_sort_dir_default));

        if(sortKey.equals(mSortKey))
        {
            Log.v(LOG_TAG, "Sorting hasn't changed, skipping refresh");
            return;
        }
        mSortKey = sortKey;

        MovieDiscoverTask popularTask = new MovieDiscoverTask(this);
        popularTask.execute(sortKey);
    }
}
