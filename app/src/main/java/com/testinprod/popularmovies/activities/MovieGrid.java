package com.testinprod.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.testinprod.popularmovies.R;
import com.testinprod.popularmovies.api.TheMovieDBConsts;
import com.testinprod.popularmovies.fragments.MovieGridFragment;

import timber.log.Timber;


public class MovieGrid extends AppCompatActivity implements MovieGridFragment.Callback {
    private static final String LOG_TAG = MovieGrid.class.getSimpleName();

    @Override
    public void onItemSelected(long movieId) {
        if(mTwoPane)
        {
            Timber.v("NYI");
        }
        else
        {
            Intent details = new Intent(this, MovieDetailActivity.class);
            details.putExtra(TheMovieDBConsts.EXTRA_MOVIE, movieId);
            startActivity(details);
        }
    }

    private boolean mTwoPane;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FrameLayout detailView = (FrameLayout) findViewById(R.id.flMovieDetails);
        mTwoPane = (detailView != null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_grid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
