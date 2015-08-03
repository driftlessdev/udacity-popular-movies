package com.testinprod.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by Tim on 8/2/2015.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.testinprod.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_VIDEO = "video";
    public static final String PATH_REVIEW = "review";

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static final class MovieEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movies";

        // INTEGER - ID of the movie from the movie database, used in API calls
        public static final String COLUMN_MOVIE_ID = "movie_id";

        // BOOL - Is this an adult movie
        public static final String COLUMN_ADULT = "adult";

        // TEXT - Filename of the backdrop image
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";

        // TEXT
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";

        // TEXT
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";

        // TEXT
        public static final String COLUMN_OVERVIEW = "overview";

        // INTEGER - Dates are stored as milliseconds since the epoch
        public static final String COLUMN_RELEASE_DATE = "release_date";

        // TEXT
        public static final String COLUMN_POSTER_PATH = "poster_path";

        // REAL
        public static final String COLUMN_POPULARITY = "popularity";

        // TEXT
        public static final String COLUMN_TITLE = "title";

        // BOOL - Area videos available?
        public static final String COLUMN_VIDEO = "video";

        // REAL
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        // INTEGER
        public static final String COLUMN_VOTE_COUNT = "vote_count";

        public static Uri buildMovieUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
