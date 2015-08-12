package com.testinprod.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.testinprod.popularmovies.data.MovieContract.DiscoverEntry;
import com.testinprod.popularmovies.data.MovieContract.MovieEntry;

import java.util.ArrayList;
import java.util.HashMap;

import timber.log.Timber;

/**
 * Created by Tim on 8/3/2015.
 */
public class MovieProvider extends ContentProvider {

    private static final int MOVIE_INT_ID = 1;
    private static final int MOVIES = 2;
    private static final int MOVIE_EXT_ID = 3;
    private static final int MOVIE_DISCOVERY = 4;
    private static final int DISCOVERIES = 5;
    private static final int DISCOVERY = 6;

    private static final HashMap<String, String> sMovieProjection = buildMovieProjection();
    private static final HashMap<String, String> sDiscoverProjection = buildDiscoverProjection();

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/", MOVIES);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#", MOVIE_INT_ID);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#/byExtId", MOVIE_EXT_ID);
        // /movie/discover/sortKey.direction
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/" + MovieContract.PATH_DISCOVERY + "/*", MOVIE_DISCOVERY);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_DISCOVERY + "/", DISCOVERIES);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_DISCOVERY + "/#", DISCOVERY);
        // Future URIs
        // movie/#/reviews
        // movie/#/videos

        return uriMatcher;
    }

    private static HashMap<String, String> buildMovieProjection()
    {
        HashMap<String, String> projections = new HashMap<>();
        projections.put(MovieEntry._ID, MovieEntry.FULL_ID);
        projections.put(MovieEntry.COLUMN_MOVIE_ID, MovieEntry.FULL_MOVIE_ID);
        projections.put(MovieEntry.COLUMN_ADULT, MovieEntry.FULL_ADULT);
        projections.put(MovieEntry.COLUMN_BACKDROP_PATH, MovieEntry.FULL_BACKDROP_PATH);
        projections.put(MovieEntry.COLUMN_ORIGINAL_LANGUAGE, MovieEntry.FULL_ORIGINAL_LANGUAGE);
        projections.put(MovieEntry.COLUMN_ORIGINAL_TITLE, MovieEntry.FULL_ORIGINAL_TITLE);
        projections.put(MovieEntry.COLUMN_OVERVIEW, MovieEntry.FULL_OVERVIEW);
        projections.put(MovieEntry.COLUMN_RELEASE_DATE, MovieEntry.FULL_RELEASE_DATE);
        projections.put(MovieEntry.COLUMN_POSTER_PATH, MovieEntry.FULL_POSTER_PATH);
        projections.put(MovieEntry.COLUMN_POPULARITY, MovieEntry.FULL_POPULARITY);
        projections.put(MovieEntry.COLUMN_TITLE, MovieEntry.FULL_TITLE);
        projections.put(MovieEntry.COLUMN_VIDEO, MovieEntry.FULL_VIDEO);
        projections.put(MovieEntry.COLUMN_VOTE_AVERAGE, MovieEntry.FULL_VOTE_AVERAGE);
        projections.put(MovieEntry.COLUMN_VOTE_COUNT, MovieEntry.FULL_VOTE_COUNT);

        return projections;
    }

    private static HashMap<String, String> buildDiscoverProjection()
    {
        HashMap<String, String> projections = new HashMap<>();
        projections.put(DiscoverEntry._ID, DiscoverEntry.FULL_ID);
        projections.put(DiscoverEntry.COLUMN_MOVIE_ID, DiscoverEntry.FULL_MOVIE_ID);
        projections.put(DiscoverEntry.COLUMN_SORTING, DiscoverEntry.FULL_SORTING);
        projections.put(DiscoverEntry.COLUMN_ORDER, DiscoverEntry.FULL_ORDER);
        return projections;
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
        ArrayList<Uri> baseUris = new ArrayList<>();

        switch (sUriMatcher.match(uri))
        {
            case MOVIE_EXT_ID:
                idCol = MovieEntry.COLUMN_MOVIE_ID;
                id = MovieEntry.getExternalIdFromUri(uri);
            case MOVIE_INT_ID:
                if(idCol == null)
                {
                    idCol = MovieEntry._ID;
                    id = MovieEntry.getInternalIdFromUri(uri);
                }
                builder.setTables(MovieEntry.TABLE_NAME);
                builder.setProjectionMap(sMovieProjection);
                Timber.v("Query for specific movie: " + idCol + " = " + id);
                builder.appendWhere(idCol + " = " + id);
                break;

            case MOVIES:
                builder.setTables(MovieEntry.TABLE_NAME);
                builder.setProjectionMap(sMovieProjection);
                break;

            case DISCOVERY:
                builder.setTables(DiscoverEntry.TABLE_NAME);
                builder.setProjectionMap(sDiscoverProjection);
                builder.appendWhere(DiscoverEntry.FULL_ID + " = " + DiscoverEntry.getIdFromUri(uri));
                break;

            case DISCOVERIES:
                builder.setTables(MovieContract.DiscoverEntry.TABLE_NAME);
                builder.setProjectionMap(sDiscoverProjection);
                break;

            case MOVIE_DISCOVERY:
                builder.setTables(MovieEntry.TABLE_NAME +
                                " LEFT INNER JOIN " + DiscoverEntry.TABLE_NAME +
                                " ON " + MovieEntry.FULL_MOVIE_ID + " = " + DiscoverEntry.FULL_MOVIE_ID
                );
                builder.setProjectionMap(sMovieProjection);
                builder.appendWhere(DiscoverEntry.FULL_SORTING + " = " + uri.getLastPathSegment());
                if(TextUtils.isEmpty(sortOrder))
                {
                    sortOrder = DiscoverEntry.FULL_ORDER + " ASC";
                }
                // Add both child tables as notification URIs. If either changes, need to refresh
                // parent. This is kind of a brute force approach, but ensures good data.
                // Don't need to add movie, since /movie notification will update all children
                baseUris.add(DiscoverEntry.CONTENT_URI);
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

        ContentResolver resolver = getContext().getContentResolver();
        cursor.setNotificationUri(resolver, uri);
        for (Uri extraUri : baseUris) {
            cursor.setNotificationUri(resolver, extraUri);
        }

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match)
        {
            case MOVIE_INT_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE_EXT_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIES:
                return MovieEntry.CONTENT_TYPE;
            case DISCOVERIES:
                return DiscoverEntry.CONTENT_TYPE;
            case MOVIE_DISCOVERY:
                return MovieEntry.CONTENT_TYPE;
            case DISCOVERY:
                return DiscoverEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        long _id;
        switch(match)
        {
            case MOVIES:
                // Replace action adapted from https://www.buzzingandroid.com/2013/01/sqlite-insert-or-replace-through-contentprovider/
                boolean replace = false;
                if(values.containsKey(MovieEntry.ACTION_REPLACE))
                {
                    replace = values.getAsBoolean(MovieEntry.ACTION_REPLACE);
                    values.remove(MovieEntry.ACTION_REPLACE);
                }

                if(replace)
                {
                    _id = db.replace(MovieEntry.TABLE_NAME, null, values);
                }
                else
                {
                    _id = db.insert(MovieEntry.TABLE_NAME, null, values);
                }

                if( _id > 0)
                {
                    returnUri = MovieEntry.buildMovieUri(_id);
                }
                else
                {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case DISCOVERIES:
                // Not doing the replace trick as this table has the unique set to ON CONFLICT REPLACE
                _id = db.insert(DiscoverEntry.TABLE_NAME, null, values);
                if( _id > 0)
                {
                    returnUri = DiscoverEntry.buildDiscoverUri(_id);
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
        String where = null;

        switch (sUriMatcher.match(uri))
        {
            case MOVIE_EXT_ID:
                idCol = MovieEntry.COLUMN_MOVIE_ID;
                id = MovieEntry.getExternalIdFromUri(uri);
            case MOVIE_INT_ID:
                if(idCol == null)
                {
                    idCol = MovieEntry._ID;
                    id = MovieEntry.getInternalIdFromUri(uri);
                }
                where = idCol + " = " + id;
                if(selection != null && !selection.isEmpty())
                {
                    where += " AND " + selection;
                }
                deleted = db.delete(
                        MovieEntry.TABLE_NAME,
                        where,
                        selectionArgs
                );
                break;

            case DISCOVERY:
                where = DiscoverEntry.FULL_ID + " = " + DiscoverEntry.getIdFromUri(uri);
                if(selection != null && !selection.isEmpty())
                {
                    where += " AND " + selection;
                }
                deleted = db.delete(
                        DiscoverEntry.TABLE_NAME,
                        where,
                        selectionArgs
                );
                break;

            case MOVIES:
                deleted = db.delete(
                        MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;

            case DISCOVERIES:
                deleted = db.delete(
                        DiscoverEntry.TABLE_NAME,
                        selection
                        ,selectionArgs
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
        String where = null;

        switch (sUriMatcher.match(uri))
        {
            case MOVIE_EXT_ID:
                idCol = MovieEntry.COLUMN_MOVIE_ID;
                id = MovieEntry.getExternalIdFromUri(uri);
            case MOVIE_INT_ID:
                if(idCol == null)
                {
                    idCol = MovieEntry._ID;
                    id = MovieEntry.getInternalIdFromUri(uri);
                }
                where = idCol + " = " + id;
                if(selection != null && !selection.isEmpty())
                {
                    where += " AND " + selection;
                }
                Timber.v("Update conditions: " + where, values);
                updated = db.update(
                        MovieEntry.TABLE_NAME,
                        values,
                        where,
                        selectionArgs
                );
                break;

            case DISCOVERY:
                where = DiscoverEntry._ID + " = " + DiscoverEntry.getIdFromUri(uri);
                if(selection != null && !selection.isEmpty())
                {
                    where += " AND " + selection;
                }
                updated = db.update(
                        DiscoverEntry.TABLE_NAME,
                        values,
                        where,
                        selectionArgs
                );
                break;

            case MOVIES:
                updated = db.update(
                        MovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;

            case DISCOVERIES:
                updated = db.update(
                        DiscoverEntry.TABLE_NAME,
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
