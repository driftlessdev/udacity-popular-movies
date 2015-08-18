package com.testinprod.popularmovies.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.testinprod.popularmovies.models.MovieModel;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by Tim on 8/3/2015.
 */
public class TestProvider extends AndroidTestCase {

    private static final int BULK_INSERT_COUNT = 10;
    private static final String TEST_SORT_TYPE = "test.desc";

    public void deleteAllRecordsFromDB()
    {
        MovieDBHelper movieDBHelper = new MovieDBHelper(mContext);
        SQLiteDatabase db = movieDBHelper.getWritableDatabase();

        db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
        db.delete(MovieContract.DiscoverEntry.TABLE_NAME, null, null);
        db.delete(MovieContract.ReviewEntry.TABLE_NAME, null, null);
        db.delete(MovieContract.VideoEntry.TABLE_NAME, null, null);
        db.close();
    }



    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsFromDB();
    }

    public void testGetType()
    {
        String type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);

        assertEquals("Error: the /movie type is incorrect", MovieContract.MovieEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(MovieContract.MovieEntry.buildMovieUri(42));
        assertEquals("Error: the /movie/<ID> type is incorrect", MovieContract.MovieEntry.CONTENT_ITEM_TYPE, type);
    }

    // <editor-fold desc="Movie Tests">

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

        // Test replace
        ContentValues replacement = new ContentValues(values);
        replacement.put(MovieContract.MovieEntry.ACTION_REPLACE, true);
        replacement.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "THIS IS AN REPLACEMENT!");
        movieUri =  mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, replacement);
        movieId = ContentUris.parseId(movieUri);

        assertTrue(movieId != -1);

        // Validate through read
        validateSingleMovie(movieId, replacement);

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

    public void testMovieDelete()
    {
        testMovieInserts();
        testDeleteAllEntries(MovieContract.MovieEntry.CONTENT_URI);
    }

    public void testMovieBulkInsert()
    {
        ContentValues[] movies = createBulkMovies();

        TestUtilities.TestContentObserver observer = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, observer);

        mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, movies);

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI
                , null
                , null
                , null
                , null
        );

        assertEquals(cursor.getCount(), BULK_INSERT_COUNT);

        cursor.moveToFirst();
        for(int i = 0; i < BULK_INSERT_COUNT; i++, cursor.moveToNext())
        {
            ContentValues movie = movies[i];
            TestUtilities.validateCurrentRecord("Error in bulk insert", cursor, movie);
        }

        cursor.close();
    }

    public void testMovieModelConversion()
    {
        MovieModel movie = TestUtilities.createMovieModel();

        ContentValues values = movie.getContentValues();

        Uri movieUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);

        long movieId = ContentUris.parseId(movieUri);

        assertTrue(movieId != -1);

        // Validate through read
        validateSingleMovie(movieId, values);
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

    // </editor-fold>

    //<editor-fold desc="Discovery Tests">
    public void testDiscoveryInsert()
    {
        ContentValues values = TestUtilities.createDiscoveryValues();
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.DiscoverEntry.CONTENT_URI, true, tco);
        Uri discoverUri = mContext.getContentResolver().insert(MovieContract.DiscoverEntry.CONTENT_URI, values);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long id = ContentUris.parseId(discoverUri);

        assertTrue(id != -1);

        validateSingleDiscovery(id, values);

        ContentValues replace = TestUtilities.createDiscoveryValues();
        replace.put(MovieContract.DiscoverEntry.COLUMN_MOVIE_ID, 5386);
        discoverUri = mContext.getContentResolver().insert(MovieContract.DiscoverEntry.CONTENT_URI, replace);
        long replaceId = ContentUris.parseId(discoverUri);

        assertTrue(replaceId != -1);

        validateSingleDiscovery(replaceId, replace);
    }

    public void testDiscoverUpdate()
    {
        ContentValues values = TestUtilities.createDiscoveryValues();
        Uri uri = mContext.getContentResolver().insert(MovieContract.DiscoverEntry.CONTENT_URI, values);
        long id = ContentUris.parseId(uri);

        assertTrue(id != -1);

        ContentValues updated = TestUtilities.createDiscoveryValues();
        updated.put(MovieContract.DiscoverEntry.COLUMN_MOVIE_ID, 5386);

        Cursor cursor = mContext.getContentResolver().query(MovieContract.DiscoverEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        cursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(uri, updated, null, null);
        assertEquals(count, 1);

        tco.waitForNotificationOrFail();
        cursor.unregisterContentObserver(tco);

        cursor = mContext.getContentResolver().query(uri, null,null, null, null);
        TestUtilities.validateCursor("Update discovery failed", cursor, updated);
    }

    public void testBulkDiscoveryInsert()
    {
        ContentValues[] values = createBulkDiscoveries();

        mContext.getContentResolver().bulkInsert(MovieContract.DiscoverEntry.CONTENT_URI, values);

        Cursor cursor = mContext.getContentResolver().query(MovieContract.DiscoverEntry.CONTENT_URI, null, null, null, null);

        assertEquals(cursor.getCount(), BULK_INSERT_COUNT);

        cursor.moveToFirst();
        for(int i = 0; i < BULK_INSERT_COUNT; i++, cursor.moveToNext())
        {
            ContentValues value = values[i];
            TestUtilities.validateCurrentRecord("Error in bulk insert", cursor, value);
        }

        cursor.close();
    }

    public void testDiscoverMovies()
    {
        testBulkDiscoveryInsert();
        testMovieBulkInsert();

        Cursor cursor = mContext.getContentResolver().query(MovieContract.MovieEntry.buildMovieDiscovery(TEST_SORT_TYPE), null, null, null, null);

        assertEquals(cursor.getCount(), BULK_INSERT_COUNT);

        // Movies should come back in reverse ID order
        int i;
        int movieIdCol = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        cursor.moveToFirst();
        for(i = 0 ; i < BULK_INSERT_COUNT ; cursor.moveToNext(), i++)
        {
            assertEquals(cursor.getLong(movieIdCol), BULK_INSERT_COUNT - i - 1);
        }
    }

    private void validateSingleDiscovery(long discoveryId, ContentValues values)
    {
        validateSingleEntry(
                MovieContract.DiscoverEntry.buildDiscoverUri(discoveryId),
                MovieContract.DiscoverEntry.CONTENT_URI,
                values
        );
    }
    //</editor-fold>

    //<editor-fold desc="Review Tests">
    public void testReviewInsert()
    {
        ContentValues values = TestUtilities.createReviewValues();
        testEntryInsert(
                MovieContract.ReviewEntry.CONTENT_URI,
                values
        );

        Uri byMovieUri = MovieContract.ReviewEntry.buildMovieReviewsUrl(values.getAsLong(MovieContract.ReviewEntry.COLUMN_MOVIE_ID));
        validateSingleEntry(
                byMovieUri,
                MovieContract.ReviewEntry.CONTENT_URI,
                values
        );
    }

    public void testReviewUpdate()
    {
        testEntryUpdate(
                MovieContract.ReviewEntry.CONTENT_URI,
                TestUtilities.createReviewValues(),
                MovieContract.ReviewEntry.COLUMN_AUTHOR,
                "MORE COWBELL!"
        );
    }

    public void testBulkReviewInsert()
    {
        testBulkEntryInsert(
                MovieContract.ReviewEntry.CONTENT_URI,
                buildBulkReviews(42)
        );
    }

    public void testReviewDelete()
    {
        testReviewInsert();
        testDeleteAllEntries(MovieContract.ReviewEntry.CONTENT_URI);

        testBulkReviewInsert();
        Uri movieReviewUri = MovieContract.ReviewEntry.buildMovieReviewsUrl(42);
        mContext.getContentResolver().delete(movieReviewUri, null, null);

        Cursor cursor = mContext.getContentResolver().query(MovieContract.ReviewEntry.CONTENT_URI, null, null, null, null);

        assertEquals(cursor.getCount(), 0);
        cursor.close();
    }

    private ContentValues[] buildBulkReviews(long movieId)
    {
        ContentValues[] bulkValues = new ContentValues[BULK_INSERT_COUNT];

        for(int i = 0; i < BULK_INSERT_COUNT; i ++)
        {
            ContentValues values = new ContentValues();
            values.put(MovieContract.ReviewEntry.COLUMN_API_ID, "API ID" + i);
            values.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
            values.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, "Review #" + i + " Author");
            values.put(MovieContract.ReviewEntry.COLUMN_CONTENT, "Review #" + i + " Content");
            values.put(MovieContract.ReviewEntry.COLUMN_URL, "Review #" + i + " URL");
            bulkValues[i] = values;
        }
        return bulkValues;
    }
    //</editor-fold>

    //<editor-fold desc="Video Tests">
    public void testVideoInsert()
    {
        ContentValues values = TestUtilities.createVideoValues();
        testEntryInsert(
                MovieContract.VideoEntry.CONTENT_URI,
                values
        );

        Uri byMovieUri = MovieContract.VideoEntry.buildMovieVideosUrl(values.getAsLong(MovieContract.VideoEntry.COLUMN_MOVIE_ID));
        validateSingleEntry(
                byMovieUri,
                MovieContract.VideoEntry.CONTENT_URI,
                values
        );
    }

    public void testVideoUpdate()
    {
        testEntryUpdate(
                MovieContract.VideoEntry.CONTENT_URI,
                TestUtilities.createVideoValues(),
                MovieContract.VideoEntry.COLUMN_NAME,
                "MORE COWBELL!"
        );
    }

    public void testBulkVideoInsert()
    {
        testBulkEntryInsert(
                MovieContract.VideoEntry.CONTENT_URI,
                buildBulkVideos(42)
        );
    }

    public void testVideoDelete()
    {
        testVideoInsert();
        testDeleteAllEntries(MovieContract.VideoEntry.CONTENT_URI);

        testBulkVideoInsert();
        Uri movieVideoUri = MovieContract.VideoEntry.buildMovieVideosUrl(42);
        mContext.getContentResolver().delete(movieVideoUri, null, null);

        Cursor cursor = mContext.getContentResolver().query(MovieContract.VideoEntry.CONTENT_URI, null, null, null, null);

        assertEquals(cursor.getCount(), 0);
        cursor.close();
    }

    private ContentValues[] buildBulkVideos(long movieId)
    {
        ContentValues[] bulkValues = new ContentValues[BULK_INSERT_COUNT];

        for(int i = 0; i < BULK_INSERT_COUNT; i++)
        {
            ContentValues values = new ContentValues();
            values.put(MovieContract.VideoEntry.COLUMN_API_ID, "API Entry " + i);
            values.put(MovieContract.VideoEntry.COLUMN_MOVIE_ID, movieId);
            values.put(MovieContract.VideoEntry.COLUMN_KEY, "KEY Entry " + i);
            values.put(MovieContract.VideoEntry.COLUMN_NAME, "NAME Entry " + i);
            values.put(MovieContract.VideoEntry.COLUMN_SITE, "Site Entry " + i);
            values.put(MovieContract.VideoEntry.COLUMN_SIZE, 720);
            values.put(MovieContract.VideoEntry.COLUMN_TYPE, "Type Entry " + i);
            bulkValues[i] = values;
        }
        return bulkValues;

    }
    //</editor-fold>

    private void testDeleteAllEntries(Uri contentUri)
    {
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(contentUri, true, tco);

        mContext.getContentResolver().delete(
                contentUri,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                contentUri,
                null,
                null,
                null,
                null
        );

        assertEquals(cursor.getCount(), 0);

        cursor.close();

        tco.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(tco);

    }

    private void testBulkEntryInsert(Uri contentUri, ContentValues[] values)
    {
        mContext.getContentResolver().bulkInsert(contentUri, values);

        Cursor cursor = mContext.getContentResolver().query(contentUri, null, null, null, null);

        assertEquals(cursor.getCount(), BULK_INSERT_COUNT);

        cursor.moveToFirst();
        for(int i = 0; i < BULK_INSERT_COUNT; i++, cursor.moveToNext())
        {
            ContentValues value = values[i];
            TestUtilities.validateCurrentRecord("Error in bulk insert for " + contentUri.toString(), cursor, value);
        }

        cursor.close();
    }

    private void testEntryInsert(Uri contentUri, ContentValues values)
    {
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(contentUri, true, tco);
        Uri insertUri = mContext.getContentResolver().insert(contentUri, values);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long id = ContentUris.parseId(insertUri);

        assertTrue(id != -1);

        validateSingleEntry(
                insertUri,
                contentUri,
                values
        );
    }

    private void testEntryUpdate(Uri contentUri, ContentValues values, String fieldToChange, String newValue)
    {
        Uri uri = mContext.getContentResolver().insert(contentUri, values);
        long id = ContentUris.parseId(uri);

        assertTrue(id != -1);

        ContentValues updated = new ContentValues(values);
        updated.put(fieldToChange, newValue);

        Cursor cursor = mContext.getContentResolver().query(contentUri, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        cursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(uri, updated, null, null);
        assertEquals(count, 1);

        tco.waitForNotificationOrFail();
        cursor.unregisterContentObserver(tco);

        cursor = mContext.getContentResolver().query(uri, null,null, null, null);
        TestUtilities.validateCursor("Update " + contentUri.toString() + " failed", cursor, updated);
    }

    private void validateSingleEntry(Uri entryUri, Uri baseUri, ContentValues values)
    {
        // Validate through read all
        Cursor cursor = mContext.getContentResolver().query(
                baseUri,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("Error: " + baseUri.toString() + " query did not match expected results", cursor, values);

        cursor = mContext.getContentResolver().query(
                entryUri,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("Error: URI " + entryUri.toString() + " single query did not match expected results", cursor, values);
    }


    private static ContentValues[] createBulkMovies(){
        ContentValues[] movies = new ContentValues[BULK_INSERT_COUNT];

        Random random = new Random();

        for(int i = 0; i < BULK_INSERT_COUNT ; i++)
        {
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry.COLUMN_ADULT, random.nextInt(2));
            movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, new BigInteger(130, random).toString(32));
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, "en");
            movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, new BigInteger(130, random).toString(32));
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, new BigInteger(130, random).toString(32));
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, 1423807200);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, new BigInteger(130, random).toString(32));
            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 17.71923d);
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, new BigInteger(130, random).toString(32));
            movieValues.put(MovieContract.MovieEntry.COLUMN_VIDEO, random.nextInt(2)); // false
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, random.nextFloat() * 10);
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, random.nextInt(300000));

            movies[i] = movieValues;
        }

        return movies;
    }



    private static ContentValues[] createBulkDiscoveries()
    {
        ContentValues[] entries = new ContentValues[BULK_INSERT_COUNT];

        for(int i = 0 ;i < BULK_INSERT_COUNT; i++)
        {
            ContentValues values = new ContentValues();
            values.put(MovieContract.DiscoverEntry.COLUMN_MOVIE_ID, i);
            values.put(MovieContract.DiscoverEntry.COLUMN_SORTING, TEST_SORT_TYPE);
            values.put(MovieContract.DiscoverEntry.COLUMN_ORDER, BULK_INSERT_COUNT - i - 1);
            entries[i] = values;
        }

        return entries;
    }






}
