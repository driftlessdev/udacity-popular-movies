package com.testinprod.popularmovies.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.testinprod.popularmovies.R;
import com.testinprod.popularmovies.api.TheMovieDBConsts;
import com.testinprod.popularmovies.data.MovieContract;
import com.testinprod.popularmovies.models.MovieModel;
import com.testinprod.popularmovies.sync.MovieSyncAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import hugo.weaving.DebugLog;
import timber.log.Timber;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment
        extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();


    private static final int MOVIE_LOADER = 1;
    private static final int REVIEW_LOADER = 2;
    private static final int VIDEO_LOADER = 3;


    //<editor-fold desc="Loader Callbacks">
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id)
        {
            case MOVIE_LOADER:
                long movieId = args.getLong(TheMovieDBConsts.EXTRA_MOVIE);
                return new CursorLoader(
                        getActivity(),
                        MovieContract.MovieEntry.buildMovieExternalIDUri(movieId),
                        MovieModel.ALL_COLUMN_PROJECTION,
                        null,
                        null,
                        null
                );

            default:
                throw new UnsupportedOperationException("Unsupported loader called");
        }
    }

    @Override
    @DebugLog
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId())
        {
            case MOVIE_LOADER:
                data.moveToFirst();
                mMovie = new MovieModel(data);
                displayMovie();
                break;

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    //</editor-fold>

    private ImageView mPoster;
    private ImageView mBackdrop;
    private ActionBar mBar;
    private MovieModel mMovie;
    private TextView mOverview;
    private TextView mReleaseDate;
    private TextView mRating;

    public static MovieDetailFragment newInstance(long movieId)
    {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putLong(TheMovieDBConsts.EXTRA_MOVIE, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.tag(LOG_TAG);
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        mPoster = (ImageView) rootView.findViewById(R.id.ivMovieHeader);
        mOverview = (TextView) rootView.findViewById(R.id.tvOverview);
        mBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        mRating = (TextView) rootView.findViewById(R.id.tvRating);
        mReleaseDate = (TextView) rootView.findViewById(R.id.tvReleaseDate);
        mBackdrop = (ImageView) rootView.findViewById(R.id.ivMovieBackdrop);

        Bundle args = getArguments();
        getLoaderManager().initLoader(MOVIE_LOADER, args, this);

        long movieId = args.getLong(TheMovieDBConsts.EXTRA_MOVIE);
        MovieSyncAdapter.syncMovieDetails(getActivity(), movieId);

        return rootView;
    }

    private void displayMovie()
    {
        mOverview.setText(mMovie.getOverview());

        if(mBar != null)
        {
            mBar.setTitle(mMovie.getTitle());
            mBar.setElevation(0);
        }


        String path = mMovie.getPosterPath();
        if(path != null && !path.isEmpty())
        {
            Picasso.with(getActivity())
                    .load(TheMovieDBConsts.POSTER_BASE_URL + path)
                    .into(mPoster) ; //, new ImageLoadedCallback());
        }

        path = mMovie.getBackdropPath();
        if(path != null && !path.isEmpty())
        {
            Picasso.with(getActivity())
                    .load(TheMovieDBConsts.BACKDROP_BASE_URL + path)
                    .into(mBackdrop);
        }

        mRating.setText(mMovie.getVoteAverage() + "/10");


        Date releaseDate = mMovie.getReleaseDateClass();

        String dateText = "Unknown";
        if(releaseDate != null)
        {
            dateText = SimpleDateFormat.getDateInstance().format(releaseDate);
        }
        mReleaseDate.setText(dateText);

    }




}
