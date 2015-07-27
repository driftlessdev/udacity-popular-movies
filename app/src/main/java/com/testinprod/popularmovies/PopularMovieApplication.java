package com.testinprod.popularmovies;

import android.app.Application;
import android.util.Log;

import timber.log.Timber;
import static timber.log.Timber.DebugTree;

/**
 * Created by Tim on 7/26/2015.
 */
public class PopularMovieApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
        }
        else
        {
            Timber.plant(new ReleaseLoggingTree());
        }
    }

    private static class ReleaseLoggingTree extends DebugTree {

        @Override
        protected boolean isLoggable(int priority) {
            return !(priority == Log.VERBOSE || priority == Log.DEBUG);
        }
    }
}
