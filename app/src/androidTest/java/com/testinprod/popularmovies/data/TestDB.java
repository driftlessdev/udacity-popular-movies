package com.testinprod.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by Tim on 8/2/2015.
 */
public class TestDB extends AndroidTestCase{

    private void deleteDatabase() {
        mContext.deleteDatabase(MovieDBHelper.DATABASE_NAME);
    }

    public void setUp() throws Exception{
        super.setUp();
        deleteDatabase();
    }

    public void testCreateDB() throws Throwable {

        HashSet<String> tableNames = new HashSet<>();
        tableNames.add(MovieContract.MovieEntry.TABLE_NAME);

        deleteDatabase();

        SQLiteDatabase db = new MovieDBHelper(this.mContext).getWritableDatabase();

        assertTrue("Error: Could not open the database", db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: coule not get the table names from the DB", c.moveToFirst());

        do {
            tableNames.remove(c.getString(0));
        } while(c.moveToNext());

        assertTrue("Error: Expected tables found in the database", tableNames.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")", null);

        assertTrue("Error: unable to get information about movie table", c.moveToFirst());

        HashSet<String> movieColumns = new HashSet<>();
        movieColumns.add(MovieContract.MovieEntry._ID);
        movieColumns.add(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        movieColumns.add(MovieContract.MovieEntry.COLUMN_ADULT);
        movieColumns.add(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH);
        movieColumns.add(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE);
        movieColumns.add(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        movieColumns.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        movieColumns.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        movieColumns.add(MovieContract.MovieEntry.COLUMN_POPULARITY);
        movieColumns.add(MovieContract.MovieEntry.COLUMN_TITLE);
        movieColumns.add(MovieContract.MovieEntry.COLUMN_VIDEO);
        movieColumns.add(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        movieColumns.add(MovieContract.MovieEntry.COLUMN_VOTE_COUNT);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumns.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: database doesn't containe all of the movie columns", movieColumns.isEmpty());
        db.close();


    }

    public void testMovieTable() throws Throwable
    {
        MovieDBHelper dbHelper = new MovieDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues movieValues = TestUtilities.createMovieValues();

        long movieId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movieValues);

        Cursor cursor = db.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        assertTrue("Error: no rows found from insert", cursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Failed to validate inserted movie", cursor, movieValues);

        assertFalse("Error: More than one entry found in the database", cursor.moveToNext());

        cursor.close();
        dbHelper.close();
    }
}
