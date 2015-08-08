package com.testinprod.popularmovies.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Tim on 8/3/2015.
 */
public class TestProvider extends AndroidTestCase {

    public void deleteAllRecords()
    {
        MovieDBHelper movieDBHelper = new MovieDBHelper(mContext);
        SQLiteDatabase db = movieDBHelper.getWritableDatabase();

        db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
        db.close();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testGetType()
    {
        String type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);

        assertEquals("Error: the /movie type is incorrect", MovieContract.MovieEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(MovieContract.MovieEntry.buildMovieUri(42));
        assertEquals("Error: the /movie/<ID> type is incorrect", MovieContract.MovieEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testBasicMovieQuery()
    {
        long movieRow = TestUtilities.insertExampleMovie(mContext);
        ContentValues values = TestUtilities.createMovieValues();

        validateSingleMovie(movieRow, values);
    }

    public void testMovieInserts()
    {
        ContentValues values = TestUtilities.createMovieValues();

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, tco);
        Uri movieUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long movieId = ContentUris.parseId(movieUri);

        assertTrue(movieId != -1);

        // Validate through read
        validateSingleMovie(movieId, values);
    }

    public void testMovieUpdate()
    {
        ContentValues values = TestUtilities.createMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
        long movieIntId = ContentUris.parseId(movieUri);

        assertTrue(movieIntId != -1);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(MovieContract.MovieEntry._ID, movieIntId);
        updatedValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "THIS IS AN UPDATE!");

        Cursor movieCusor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        movieCusor.registerContentObserver(tco);

        int updated = mContext.getContentResolver().update(movieUri, updatedValues, null, null);
        assertEquals(updated, 1);

        tco.waitForNotificationOrFail();
        movieCusor.unregisterContentObserver(tco);

        Cursor cursor = mContext.getContentResolver().query(movieUri, null, null, null, null);
        TestUtilities.validateCursor("Update check failed", cursor, updatedValues);

        // Update by external ID
        Uri externalIdUri = MovieContract.MovieEntry.buildMovieExternalIDUri(updatedValues.getAsLong(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
        updatedValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "YET ANOTHER UPDATE!:");

        updated = mContext.getContentResolver().update(externalIdUri, updatedValues, null, null);
        assertEquals(updated, 1);

        cursor = mContext.getContentResolver().query(externalIdUri, null, null, null, null);
        TestUtilities.validateCursor("Update by external ID check failed", cursor, updatedValues);

    }

    private void validateSingleMovie(long movieId, ContentValues values)
    {
        // Validate through read all
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("Error: Movie query did not match expected results", cursor, values);

        // Try getting a single movie
        Uri singleMovie = MovieContract.MovieEntry.buildMovieUri(movieId);

        cursor = mContext.getContentResolver().query(
                singleMovie,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("Error: Movie single query did not match expected results", cursor, values);

        long externalId = values.getAsLong(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        singleMovie = MovieContract.MovieEntry.buildMovieExternalIDUri(externalId);

        cursor = mContext.getContentResolver().query(
                singleMovie,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("Error: Movie single external ID query for " + singleMovie.toString() + " did not match expected results", cursor, values);
    }


}
