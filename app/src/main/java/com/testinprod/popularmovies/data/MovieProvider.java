package com.testinprod.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import timber.log.Timber;

/**
 * Created by Tim on 8/3/2015.
 */
public class MovieProvider extends ContentProvider {

    private static final int MOVIE_INT_ID = 1;
    private static final int MOVIES = 2;
    private static final int MOVIE_EXT_ID = 3;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, "movie/", MOVIES);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, "movie/#", MOVIE_INT_ID);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, "movie/#/byExtId", MOVIE_EXT_ID);
        // Future URIs
        // movie/#/reviews
        // movie/#/videos

        return uriMatcher;
    }

    private MovieDBHelper mDBHelper;

    @Override
    public boolean onCreate() {
        Timber.tag(MovieProvider.class.getSimpleName());
        mDBHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        String idCol = null;
        String id = null;

        switch (sUriMatcher.match(uri))
        {
            case MOVIE_EXT_ID:
                idCol = MovieContract.MovieEntry.COLUMN_MOVIE_ID;
                id = MovieContract.MovieEntry.getExternalIdFromUri(uri);
            case MOVIE_INT_ID:
                if(idCol == null)
                {
                    idCol = MovieContract.MovieEntry._ID;
                    id = MovieContract.MovieEntry.getInternalIdFromUri(uri);
                }
                builder.setTables(MovieContract.MovieEntry.TABLE_NAME);
                Timber.v("Query for specific movie: " + idCol + " = " + id);
                builder.appendWhere(idCol + " = " + id);
                break;

            case MOVIES:
                builder.setTables(MovieContract.MovieEntry.TABLE_NAME);
                break;

            default:
                throw new UnsupportedOperationException("Unknown query uri: " + uri);
        }

        Cursor cursor = builder.query(
                db,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match)
        {
            case MOVIE_INT_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE_EXT_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch(match)
        {
            case MOVIES:
                // Replace action adapted from https://www.buzzingandroid.com/2013/01/sqlite-insert-or-replace-through-contentprovider/
                boolean replace = false;
                if(values.containsKey(MovieContract.MovieEntry.ACTION_REPLACE))
                {
                    replace = values.getAsBoolean(MovieContract.MovieEntry.ACTION_REPLACE);
                    values.remove(MovieContract.MovieEntry.ACTION_REPLACE);
                }
                long _id;
                if(replace)
                {
                    _id = db.replace(MovieContract.MovieEntry.TABLE_NAME, null, values);
                }
                else
                {
                    _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                }

                if( _id > 0)
                {
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                }
                else
                {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int deleted = 0;
        String idCol = null;
        String id = null;

        switch (sUriMatcher.match(uri))
        {
            case MOVIE_EXT_ID:
                idCol = MovieContract.MovieEntry.COLUMN_MOVIE_ID;
                id = MovieContract.MovieEntry.getExternalIdFromUri(uri);
            case MOVIE_INT_ID:
                if(idCol == null)
                {
                    idCol = MovieContract.MovieEntry._ID;
                    id = MovieContract.MovieEntry.getInternalIdFromUri(uri);
                }
                String where = idCol + " = " + id;
                if(selection != null && !selection.isEmpty())
                {
                    where += " AND " + selection;
                }
                deleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        where,
                        selectionArgs
                );
                break;

            case MOVIES:
                deleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(deleted > 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int updated = 0;
        String idCol = null;
        String id = null;

        switch (sUriMatcher.match(uri))
        {
            case MOVIE_EXT_ID:
                idCol = MovieContract.MovieEntry.COLUMN_MOVIE_ID;
                id = MovieContract.MovieEntry.getExternalIdFromUri(uri);
            case MOVIE_INT_ID:
                if(idCol == null)
                {
                    idCol = MovieContract.MovieEntry._ID;
                    id = MovieContract.MovieEntry.getInternalIdFromUri(uri);
                }
                String where = idCol + " = " + id;
                if(selection != null && !selection.isEmpty())
                {
                    where += " AND " + selection;
                }
                Timber.v("Update conditions: " + where, values);
                updated = db.update(
                        MovieContract.MovieEntry.TABLE_NAME,
                        values,
                        where,
                        selectionArgs
                );
                break;

            case MOVIES:
                updated = db.update(
                        MovieContract.MovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(updated > 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updated;
    }
}
