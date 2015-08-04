package com.testinprod.popularmovies.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.testinprod.popularmovies.MovieAdapter;
import com.testinprod.popularmovies.R;
import com.testinprod.popularmovies.activities.MovieDetailActivity;
import com.testinprod.popularmovies.api.TheMovieDBApi;
import com.testinprod.popularmovies.api.TheMovieDBConsts;
import com.testinprod.popularmovies.models.MovieDiscovery;
import com.testinprod.popularmovies.models.MovieModel;

import org.parceler.Parcels;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieGridFragment extends Fragment implements Callback<MovieDiscovery> {
    private static final String LOG_TAG = MovieGridFragment.class.getSimpleName();
    private static final String MOVIE_LIST = "movies.list";

    @Override
    public void success(MovieDiscovery movieDiscovery, Response response) {
        ArrayList<MovieModel> movieModels = null;
        if(movieDiscovery != null)
        {
            movieModels = new ArrayList<>(movieDiscovery.getResults());
        }
        if(movieModels == null)
        {
            movieModels = new ArrayList<>();
        }
        Timber.v("Movies Loaded: " + movieModels.size());
        if(movieModels.size() == 0) {
            Toast.makeText(getActivity(), "No movies found, please try a different search", Toast.LENGTH_LONG).show();
        }
        mMovieGrid.setAdapter(new MovieAdapter(getActivity(), movieModels));
    }

    @Override
    public void failure(RetrofitError error) {
        Log.e(LOG_TAG, "Failure to load: " + error.toString());
    }

    private static final String MOVIE_SORT = "movies.sort";
    private GridView mMovieGrid;
    private String mSortKey;
    private TheMovieDBApi mMovieDBApi;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MOVIE_SORT, mSortKey);
        MovieAdapter adapter = (MovieAdapter) mMovieGrid.getAdapter();
        outState.putParcelable(MOVIE_LIST, Parcels.wrap(adapter.getMovies()));
    }

    public MovieGridFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.v("onStart");
        refreshGrid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Timber.tag(LOG_TAG);
        // TODO: Loading spinner
        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);
        mMovieGrid = (GridView) rootView.findViewById(R.id.gvMovies);

        if(savedInstanceState != null)
        {
            Timber.v("Restoring state");
            ArrayList<MovieModel> movies = Parcels.unwrap(savedInstanceState.getParcelable(MOVIE_LIST));
            MovieAdapter adapter = new MovieAdapter(getActivity(), movies);
            mMovieGrid.setAdapter(adapter);
            mSortKey = savedInstanceState.getString(MOVIE_SORT);
        }
        else
        {
            refreshGrid();
        }

        mMovieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieModel movie = (MovieModel) mMovieGrid.getItemAtPosition(position);
                Intent details = new Intent(getActivity(), MovieDetailActivity.class);
                details.putExtra(TheMovieDBConsts.EXTRA_MOVIE, Parcels.wrap(movie));
                startActivity(details);
            }
        });

        return rootView;
    }

    private void refreshGrid()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortKey = preferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        sortKey += "." + preferences.getString(getString(R.string.pref_sort_dir_key), getString(R.string.pref_sort_dir_default));

        Timber.v("Current: " + mSortKey + ", New: " + sortKey);
        if(sortKey.equals(mSortKey))
        {
            Timber.v("Sorting hasn't changed, skipping refresh");
            return;
        }
        mSortKey = sortKey;

        if( mMovieDBApi == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(TheMovieDBConsts.API_URL)
                    .build();

            mMovieDBApi = restAdapter.create(TheMovieDBApi.class);
        }

        mMovieDBApi.discoverMovies(getActivity().getString(R.string.tmdb_api_key), mSortKey, this);

    }
}
