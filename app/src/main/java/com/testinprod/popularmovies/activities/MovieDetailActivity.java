package com.testinprod.popularmovies.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.testinprod.popularmovies.R;
import com.testinprod.popularmovies.fragments.MovieDetailFragment;
import com.testinprod.popularmovies.models.MovieParcel;

import java.security.InvalidParameterException;


public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);



        if(savedInstanceState == null)
        {
            MovieParcel movie = getIntent().getExtras().getParcelable(MovieParcel.EXTRA_MOVIE);
            if(movie == null)
            {
                throw new InvalidParameterException("Can't do a movie detail without a movie.");
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.flMovieDetails, MovieDetailFragment.newInstance(movie))
                    .commit();
        }
    }


}
