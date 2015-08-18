package com.testinprod.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.Arrays;
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
        tableNames.add(MovieContract.DiscoverEntry.TABLE_NAME);
        tableNames.add(MovieContract.ReviewEntry.TABLE_NAME);
        tableNames.add(MovieContract.VideoEntry.TABLE_NAME);

        deleteDatabase();

        SQLiteDatabase db = new MovieDBHelper(this.mContext).getWritableDatabase();

        assertTrue("Error: Could not open the database", db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: could not get the table names from the DB", c.moveToFirst());

        do {
            tableNames.remove(c.getString(0));
        } while(c.moveToNext());

        assertTrue("Error: Not all expected tables found", tableNames.isEmpty());
        c.close();

        validateTableColumns(db, MovieContract.MovieEntry.TABLE_NAME, MovieContract.MovieEntry.PROJECTION_ALL);
        validateTableColumns(db, MovieContract.DiscoverEntry.TABLE_NAME, MovieContract.DiscoverEntry.PROJECTION_ALL);
        validateTableColumns(db, MovieContract.VideoEntry.TABLE_NAME, MovieContract.VideoEntry.PROJECTION_ALL);
        validateTableColumns(db, MovieContract.ReviewEntry.TABLE_NAME, MovieContract.ReviewEntry.PROJECTION_ALL);
        db.close();


    }

    private void validateTableColumns(SQLiteDatabase db, String tableName, String[] columns)
    {
        Cursor c = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);

        assertTrue("Error: unable to get information about table", c.moveToFirst());

        ArrayList<String> cols = new ArrayList<>(Arrays.asList(columns));

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            cols.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: table doesn't contain all of the columns", cols.isEmpty());
        c.close();

    }

    private void testTableReadWrite(SQLiteDatabase db, String tableName, ContentValues values) throws Throwable
    {

        long entryId = db.insert(tableName, null, values);

        assertTrue("Error inserting entry into " + tableName, entryId != -1);

        Cursor cursor = db.query(
                tableName,
                null,
                null,
                null,
                null,
                null,
                null
        );
        assertTrue("Error: no rows found from insert on " + tableName, cursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Failed to validate inserted values into " + tableName, cursor, values);

        assertFalse("Error: More than one entry found in the table " + tableName, cursor.moveToNext());

        cursor.close();
    }



    public void testTablesReadWrite() throws Throwable
    {
        MovieDBHelper dbHelper = new MovieDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        testTableReadWrite(db, MovieContract.MovieEntry.TABLE_NAME, TestUtilities.createMovieValues());
        testTableReadWrite(db, MovieContract.DiscoverEntry.TABLE_NAME, TestUtilities.createDiscoveryValues());
        testTableReadWrite(db, MovieContract.ReviewEntry.TABLE_NAME, TestUtilities.createReviewValues());
        testTableReadWrite(db, MovieContract.VideoEntry.TABLE_NAME, TestUtilities.createVideoValues());
        dbHelper.close();
    }
}
