package com.testinprod.popularmovies.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.testinprod.popularmovies.models.MovieModel;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {
    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private CardView mHeader;
    private ImageView mPoster;
    private ActionBar mBar;
    private MovieModel mMovie;

    public static MovieDetailFragment newInstance(MovieModel movie)
    {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(TheMovieDBConsts.EXTRA_MOVIE, Parcels.wrap(movie));
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

        Bundle args = getArguments();
        mMovie = Parcels.unwrap(args.getParcelable(TheMovieDBConsts.EXTRA_MOVIE));

        TextView overview = (TextView) rootView.findViewById(R.id.tvOverview);
        overview.setText(mMovie.getOverview());

        mBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(mBar != null)
        {
            mBar.setTitle(mMovie.getTitle());
            mBar.setElevation(0);
        }

        setDefaultHeaderColors();

        mPoster = (ImageView) rootView.findViewById(R.id.ivMovieHeader);
        String path = mMovie.getPosterPath();
        if(path != null && !path.isEmpty())
        {
            Picasso.with(getActivity())
                    .load(TheMovieDBConsts.POSTER_BASE_URL + path)
                    .into(mPoster, new ImageLoadedCallback());
        } else {
            setDefaultHeaderColors();
        }


        TextView rating = (TextView) rootView.findViewById(R.id.tvRating);
        rating.setText(mMovie.getVoteAverage() + "/10");

        TextView release = (TextView) rootView.findViewById(R.id.tvReleaseDate);
        Date releaseDate = mMovie.getReleaseDateClass();

        String dateText = "Unknown";
        if(releaseDate != null)
        {
            dateText = SimpleDateFormat.getDateInstance().format(releaseDate);
        }
        release.setText(dateText);
        return rootView;
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
