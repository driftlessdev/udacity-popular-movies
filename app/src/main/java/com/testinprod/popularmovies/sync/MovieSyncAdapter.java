package com.testinprod.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;

import com.testinprod.popularmovies.R;
import com.testinprod.popularmovies.api.TheMovieDBApi;
import com.testinprod.popularmovies.api.TheMovieDBConsts;
import com.testinprod.popularmovies.data.MovieContract;
import com.testinprod.popularmovies.models.MovieDiscovery;
import com.testinprod.popularmovies.models.MovieModel;

import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;
import retrofit.RestAdapter;
import timber.log.Timber;

/**
 * Created by Tim on 8/9/2015.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    private static String EXTRA_SORT = "sortkey";

    private TheMovieDBApi mMovieDBApi;

    public MovieSyncAdapter(Context context, boolean autoInitialize)
    {
        super(context, autoInitialize);
        Timber.tag(MovieSyncAdapter.class.getSimpleName());
    }

    @DebugLog
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        String sortKey = extras.getString(EXTRA_SORT);
        if(TextUtils.isEmpty(sortKey))
        {
            sortKey = getContext().getString(R.string.pref_sort_default) + "." + getContext().getString(R.string.pref_sort_dir_default);
        }

        if( mMovieDBApi == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(TheMovieDBConsts.API_URL)
                    .build();

            mMovieDBApi = restAdapter.create(TheMovieDBApi.class);
        }

        MovieDiscovery results = mMovieDBApi.discoverMovies(TheMovieDBConsts.API_KEY, sortKey);

        ContentValues[] newData = parseMovies(results);
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        int i;
        for( i = 0 ; i < newData.length ; i++)
        {
            ContentProviderOperation newOp = ContentProviderOperation.newInsert(MovieContract.MovieEntry.CONTENT_URI)
                    .withValues(newData[i])
                    .build();
            ops.add(newOp);
        }
        newData = parseSorting(results, sortKey);
        for( i = 0 ; i < newData.length ; i++)
        {
            ContentProviderOperation newOp = ContentProviderOperation.newInsert(MovieContract.DiscoverEntry.CONTENT_URI)
                    .withValues(newData[i])
                    .build();
            ops.add(newOp);
        }

        try{
            getContext().getContentResolver().applyBatch(MovieContract.CONTENT_AUTHORITY, ops);
        }
        catch (OperationApplicationException|RemoteException e)
        {
            Timber.e(e, "Error syncing movie discovery");
        }

        Timber.v("Inserted movies: " + results.getResults().size());
    }

    private ContentValues[] parseMovies(MovieDiscovery movieDiscovery)
    {
        List<MovieModel> movies = movieDiscovery.getResults();
        ContentValues[] results = new ContentValues[movies.size()];

        for(int i = 0; i < movies.size(); i++)
        {
            MovieModel movie = movies.get(i);
            ContentValues entry = movie.getContentValues();
            entry.put(MovieContract.MovieEntry.ACTION_REPLACE, true);
            results[i] = entry;
        }

        return results;

    }

    private ContentValues[] parseSorting(MovieDiscovery movieDiscovery, String sorting)
    {
        List<MovieModel> movies = movieDiscovery.getResults();
        ContentValues[] results = new ContentValues[movies.size()];

        for(int i = 0; i < movies.size(); i++)
        {
            MovieModel movie = movies.get(i);
            ContentValues entry = new ContentValues();
            entry.put(MovieContract.DiscoverEntry.COLUMN_MOVIE_ID, movie.getId());
            entry.put(MovieContract.DiscoverEntry.COLUMN_ORDER, i);
            entry.put(MovieContract.DiscoverEntry.COLUMN_SORTING, sorting);
            results[i] = entry;
        }

        return results;
    }

    @DebugLog
    public static void syncDiscoveredMovies(Context context, String sorting)
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putString(EXTRA_SORT, sorting);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.sync_account_type), bundle);
    }

    public static Account getSyncAccount(Context context)
    {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if( accountManager.getPassword(newAccount) == null)
        {
            if(!accountManager.addAccountExplicitly(newAccount, "", null))
            {
                return null;
            }
        }
        return newAccount;
    }
}
