package com.testinprod.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Tim on 8/8/2015.
 */
public class MovieAuthenticatorService extends Service {
    private MovieAuthenticator mAuthenticator;
    @Override
    public void onCreate() {
        mAuthenticator = new MovieAuthenticator(this);
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
