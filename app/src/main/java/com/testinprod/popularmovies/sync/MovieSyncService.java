package com.testinprod.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import timber.log.Timber;

/**
 * Created by Tim on 8/9/2015.
 */
public class MovieSyncService extends Service {
    public static final Object sSyncAdapterLock = new Object();
    public static MovieSyncAdapter sMovieSyncAdapter = null;

    @Override
    public void onCreate() {
        Timber.tag(MovieSyncService.class.getSimpleName());
        Timber.d("in onCreate");
        synchronized (sSyncAdapterLock)
        {
            if(sMovieSyncAdapter == null)
            {
                sMovieSyncAdapter = new MovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sMovieSyncAdapter.getSyncAdapterBinder();
    }
}
