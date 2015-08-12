package com.testinprod.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.testinprod.popularmovies.data.MovieContract.MovieEntry;



/**
 * Created by Tim on 8/2/2015.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "movie.db";

    public MovieDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                MovieEntry.COLUMN_ADULT + " BOOL NOT NULL, " +
                MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POPULARITY + " NUMERIC NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VIDEO + " BOOL NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL);";

        final String SQL_CREATE_DISCOVERY_TABLE = "CREATE TABLE " + MovieContract.DiscoverEntry.TABLE_NAME + " (" +
                MovieContract.DiscoverEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.DiscoverEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                MovieContract.DiscoverEntry.COLUMN_SORTING + " TEXT NOT NULL," +
                MovieContract.DiscoverEntry.COLUMN_ORDER + " INTEGER NOT NULL," +
                "UNIQUE(" + MovieContract.DiscoverEntry.COLUMN_SORTING + ", " + MovieContract.DiscoverEntry.COLUMN_ORDER + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_DISCOVERY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.DiscoverEntry.TABLE_NAME);
        onCreate(db);
    }
}
