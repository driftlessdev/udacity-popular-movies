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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.testinprod.popularmovies.R;
import com.testinprod.popularmovies.adapters.MovieDetailAdapter;
import com.testinprod.popularmovies.api.TheMovieDBConsts;
import com.testinprod.popularmovies.data.MovieContract;
import com.testinprod.popularmovies.models.MovieModel;
import com.testinprod.popularmovies.models.ReviewModel;
import com.testinprod.popularmovies.models.VideoModel;
import com.testinprod.popularmovies.sync.MovieSyncAdapter;

import java.util.ArrayList;

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
        long movieId = args.getLong(TheMovieDBConsts.EXTRA_MOVIE);
        switch (id)
        {
            case MOVIE_LOADER:
                return new CursorLoader(
                        getActivity(),
                        MovieContract.MovieEntry.buildMovieExternalIDUri(movieId),
                        MovieModel.ALL_COLUMN_PROJECTION,
                        null,
                        null,
                        null
                );

            case VIDEO_LOADER:
                return new CursorLoader(
                        getActivity(),
                        MovieContract.VideoEntry.buildMovieVideosUrl(movieId),
                        VideoModel.ALL_COLUMN_PROJECTION,
                        null,
                        null,
                        null
                );

            case REVIEW_LOADER:
                return new CursorLoader(
                        getActivity(),
                        MovieContract.ReviewEntry.buildMovieReviewsUrl(movieId),
                        ReviewModel.ALL_COLUMN_PROJECTION,
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
                mAdapter.setMovie(mMovie);
                displayMovie();
                break;

            case VIDEO_LOADER:
                ArrayList<VideoModel> videos = new ArrayList<>();
                while(data.moveToNext())
                {
                    VideoModel video = new VideoModel(data);
                    videos.add(video);
                }
                mAdapter.setVideos(videos);
                break;

            case REVIEW_LOADER:
                ArrayList<ReviewModel> reviews = new ArrayList<>();
                while (data.moveToNext())
                {
                    ReviewModel review = new ReviewModel(data);
                    reviews.add(review);
                }
                mAdapter.setReviews(reviews);
                break;

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId())
        {
            case VIDEO_LOADER:
                mAdapter.setVideos(null);
                break;

            case REVIEW_LOADER:
                mAdapter.setReviews(null);
                break;
        }
    }
    //</editor-fold>

    private ImageView mBackdrop;
    private ActionBar mBar;
    private MovieModel mMovie;
    private FloatingActionButton mFavButton;
    private boolean mIsFavorite;
    private RecyclerView mRecyclerView;
    private MovieDetailAdapter mAdapter;

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

        mBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        mBackdrop = (ImageView) rootView.findViewById(R.id.ivMovieBackdrop);
        mFavButton = (FloatingActionButton) rootView.findViewById(R.id.fabFavorite);
        mFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFavoriteClick();
            }
        });

        // TODO Launch video on click
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvMovieDetails);
        mAdapter = new MovieDetailAdapter(getContext(), null, null, null);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Bundle args = getArguments();
        long movieId = args.getLong(TheMovieDBConsts.EXTRA_MOVIE);
        MovieSyncAdapter.syncMovieDetails(getActivity(), movieId);

        getLoaderManager().initLoader(REVIEW_LOADER, args, this);
        getLoaderManager().initLoader(VIDEO_LOADER, args, this);
        getLoaderManager().initLoader(MOVIE_LOADER, args, this);

        return rootView;
    }

    private void displayMovie()
    {
        if(mBar != null)
        {
            mBar.setTitle(mMovie.getTitle());
            mBar.setElevation(0);
        }


        String path = mMovie.getBackdropPath();
        if(path != null && !path.isEmpty())
        {
            Picasso.with(getActivity())
                    .load(TheMovieDBConsts.BACKDROP_BASE_URL + path)
                    .into(mBackdrop);
        }

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
