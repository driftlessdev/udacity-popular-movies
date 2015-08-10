package com.testinprod.popularmovies.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.testinprod.popularmovies.R;
import com.testinprod.popularmovies.api.TheMovieDBConsts;
import com.testinprod.popularmovies.fragments.MovieDetailFragment;


public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);



        if(savedInstanceState == null)
        {
            long movieId = getIntent().getExtras().getLong(TheMovieDBConsts.EXTRA_MOVIE);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.flMovieDetails, MovieDetailFragment.newInstance(movieId))
                    .commit();
        }
    }


}
