package com.testinprod.popularmovies.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.testinprod.popularmovies.MovieAdapter;
import com.testinprod.popularmovies.R;
import com.testinprod.popularmovies.data.MovieContract;
import com.testinprod.popularmovies.sync.MovieSyncAdapter;

import hugo.weaving.DebugLog;
import timber.log.Timber;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieGridFragment
        extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MovieGridFragment.class.getSimpleName();
    private static final String MOVIE_LIST = "movies.list";

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_TITLE
    };

    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_POSTER_PATH = 2;
    public static final int COL_TITLE = 3;

    private static final int MOVIE_LOADER = 1;

    private static final String MOVIE_SORT = "movies.sort";
    private GridView mMovieGrid;
    private String mSortKey;
    private MovieAdapter mMovieAdapter;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == MOVIE_LOADER)
        {
            // TODO Change sorting to be by SQL columns
            return new CursorLoader(
                    getActivity(),
                    MovieContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @DebugLog
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
    }

    @DebugLog
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // TODO: Retain state on orientation change
//        outState.putString(MOVIE_SORT, mSortKey);
//        MovieAdapter adapter = (MovieAdapter) mMovieGrid.getAdapter();
//        outState.putParcelable(MOVIE_LIST, Parcels.wrap(adapter.getMovies()));
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

        if(savedInstanceState == null)
        {
            refreshGrid();
        }

        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);
        mMovieGrid.setAdapter(mMovieAdapter);
        mMovieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if(cursor != null)
                {
                    // TODO: Re-enable when the details use the content adapter
                    Timber.d("Movie to view details: " + cursor.getInt(COL_MOVIE_ID));
//                    MovieModel movie = (MovieModel) mMovieGrid.getItemAtPosition(position);
//                    Intent details = new Intent(getActivity(), MovieDetailActivity.class);
//                    details.putExtra(TheMovieDBConsts.EXTRA_MOVIE, Parcels.wrap(movie));
//                    startActivity(details);
                }

            }
        });

        return rootView;
    }

    private void refreshGrid()
    {
        MovieSyncAdapter.syncDiscoveredMovies(getActivity());

    }
}
