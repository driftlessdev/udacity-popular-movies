package com.testinprod.popularmovies.fragments;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
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
    private CardView mHeader;
    private ImageView mPoster;

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

        mHeader = (CardView) rootView.findViewById(R.id.cvDetailHeader);
        mPoster = (ImageView) rootView.findViewById(R.id.ivMovieHeader);
        mOverview = (TextView) rootView.findViewById(R.id.tvOverview);
        mBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        mRating = (TextView) rootView.findViewById(R.id.tvRating);
        mReleaseDate = (TextView) rootView.findViewById(R.id.tvReleaseDate);


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

        setDefaultHeaderColors();

        String path = mMovie.getPosterPath();
        if(path != null && !path.isEmpty())
        {
            Picasso.with(getActivity())
                    .load(TheMovieDBConsts.POSTER_BASE_URL + path)
                    .into(mPoster, new ImageLoadedCallback());
        } else {
            setDefaultHeaderColors();
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

    private void parseHeaderColors()
    {
        Bitmap bitmap = ((BitmapDrawable) mPoster.getDrawable()).getBitmap();
        Palette.from(bitmap)
                .generate(new PosterPaletteListener());
    }

    private void setDefaultHeaderColors()
    {
        setHeaderColors(R.color.primary, R.color.primary_text);
    }

    private void setHeaderColors(int backgroundColor, int textColor)
    {
        mHeader.setBackgroundColor(backgroundColor);
        if(mBar != null)
        {
            mBar.setBackgroundDrawable(new ColorDrawable(backgroundColor));

            // Kudos to http://stackoverflow.com/questions/9920277/how-to-change-action-bar-title-color-in-code
            SpannableString title = new SpannableString(mMovie.getTitle());
            title.setSpan(new ForegroundColorSpan(textColor), 0, title.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            mBar.setTitle(title);
        }
        else
        {

            Timber.v("ActionBar not found");
        }
    }

    private class ImageLoadedCallback implements Callback
    {
        @Override
        public void onSuccess() {
            Timber.v( "Image Loaded, extracting colors");
            parseHeaderColors();
        }

        @Override
        public void onError() {
            setDefaultHeaderColors();
        }
    }

    private class PosterPaletteListener implements Palette.PaletteAsyncListener
    {
        @Override
        public void onGenerated(Palette palette) {
            Palette.Swatch swatch = palette.getVibrantSwatch();
            int textColor;
            int bgColor;
            // No vibrant, inconceivable!
            if(swatch == null) {
                Timber.v( "Falling back to Muted");
                swatch = palette.getMutedSwatch();
            }
            if(swatch != null)
            {
                textColor = swatch.getTitleTextColor();
                bgColor = swatch.getRgb();
            }
            else
            {
                Timber.v( "Falling back to default colors");
                setDefaultHeaderColors();
                return;
            }

            setHeaderColors(bgColor, textColor);

        }
    }


}
