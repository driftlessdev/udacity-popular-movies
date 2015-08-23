package com.testinprod.popularmovies.fragments;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

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
    private FloatingActionButton mFavButton;
    private boolean mIsFavorite;

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
        mFavButton = (FloatingActionButton) rootView.findViewById(R.id.fabFavorite);



        mFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFavoriteClick();
            }
        });



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

        Cursor cursor = getActivity().getContentResolver().query(
                MovieContract.DiscoverEntry.CONTENT_URI,
                null,
                MovieContract.DiscoverEntry.COLUMN_SORTING + " =  ?"
                        + " AND " + MovieContract.DiscoverEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{getActivity().getString(R.string.pref_sort_key_favorites), mMovie.getId().toString()},
                null
        );
        mIsFavorite = cursor.moveToFirst();
        cursor.close();

        setFavoriteIcon();
    }

    private void handleFavoriteClick()
    {
        if(mIsFavorite)
        {
            int deleted = getActivity().getContentResolver().delete(
                    MovieContract.DiscoverEntry.CONTENT_URI,
                    MovieContract.DiscoverEntry.COLUMN_SORTING + " =  ?"
                            + " AND " + MovieContract.DiscoverEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{getActivity().getString(R.string.pref_sort_key_favorites), mMovie.getId().toString()}
            );
            if(deleted > 0)
            {
                Toast.makeText(getActivity(),R.string.unfavorited,Toast.LENGTH_SHORT).show();
            }
            else
            {
                Timber.e("Deleting a favorite resulted in no entries deleted, bad activity state detected.");
            }
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put(MovieContract.DiscoverEntry.COLUMN_SORTING, getActivity().getString(R.string.pref_sort_key_favorites));
            values.put(MovieContract.DiscoverEntry.COLUMN_ORDER, mMovie.getId());
            values.put(MovieContract.DiscoverEntry.COLUMN_MOVIE_ID, mMovie.getId());
            Uri newEntry = getActivity().getContentResolver().insert(
                    MovieContract.DiscoverEntry.CONTENT_URI,
                    values
            );
            long _id = ContentUris.parseId(newEntry);
            if(_id > 0)
            {
                Toast.makeText(getActivity(),R.string.favorited,Toast.LENGTH_SHORT).show();
            }
            else
            {
                Timber.e("Setting a favorite resulted in no new entries, bad activity state detected");
            }

        }

        mIsFavorite = !mIsFavorite;
        setFavoriteIcon();
    }

    private void setFavoriteIcon()
    {
        if(mIsFavorite)
        {
            mFavButton.setImageResource(R.drawable.ic_star_black_24dp);
        }
        else
        {
            mFavButton.setImageResource(R.drawable.ic_star_border_black_24dp);
        }
    }


}
