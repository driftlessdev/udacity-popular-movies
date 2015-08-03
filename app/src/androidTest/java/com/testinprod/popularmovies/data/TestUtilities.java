package com.testinprod.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by Tim on 8/2/2015.
 */
public class TestUtilities extends AndroidTestCase {

    // Taken from Udacity Sunshine 2 examples
    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    // Adapted from Udacity Sunshine 2 examples
    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            switch (valueCursor.getType(idx))
            {
                case Cursor.FIELD_TYPE_FLOAT:
                    float floatValue = Float.parseFloat(entry.getValue().toString());
                    assertEquals("Value '" + valueCursor.getFloat(idx) +
                            "' did not match the expected value '" +
                            floatValue + "'. " + error, floatValue, valueCursor.getFloat(idx));
                    break;
                default:
                    assertEquals("Value '" + valueCursor.getString(idx) +
                            "' did not match the expected value '" +
                            expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));

            }
        }
    }


    public static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_ADULT, 0); // false
        movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, "/9eKd1DDDAbrDNXR2he7ZJEu7UkI.jpg");
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 207703);
        movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, "en");
        movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Kingsman: The Secret Service");
        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "Kingsman: The Secret Service tells the story of a super-secret spy organization that recruits an unrefined but promising street kid into the agency's ultra-competitive training program just as a global threat emerges from a twisted tech genius.");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, 1423807200);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "/oAISjx6DvR2yUn9dxj00vP8OcJJ.jpg");
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 17.71923d);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Kingsman: The Secret Service");
        movieValues.put(MovieContract.MovieEntry.COLUMN_VIDEO, 0); // false
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 7.7);
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, 1044);

        return movieValues;
    }
}
