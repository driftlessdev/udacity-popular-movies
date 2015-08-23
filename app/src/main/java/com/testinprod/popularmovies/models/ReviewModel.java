package com.testinprod.popularmovies.models;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.testinprod.popularmovies.data.MovieContract;

import org.parceler.Parcel;

// Generated via http://www.jsonschema2pojo.org/
@Parcel
public class ReviewModel {
    @Expose
    private String id;
    @Expose
    private String author;
    @Expose
    private String content;
    @Expose
    private String url;

    public ReviewModel(){

    }

    /**
     *
     * @return
     *     The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     *     The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     *     The author
     */
    public String getAuthor() {
        return author;
    }

    /**
     *
     * @param author
     *     The author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     *
     * @return
     *     The content
     */
    public String getContent() {
        return content;
    }

    /**
     *
     * @param content
     *     The content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     *
     * @return
     *     The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     *     The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    public ContentValues getContentValues()
    {
        ContentValues values = new ContentValues();
        values.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, getAuthor());
        values.put(MovieContract.ReviewEntry.COLUMN_CONTENT, getContent());
        values.put(MovieContract.ReviewEntry.COLUMN_API_ID, getId());
        values.put(MovieContract.ReviewEntry.COLUMN_URL, getUrl());
        return values;
    }


    public static final String[] ALL_COLUMN_PROJECTION = {
            MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_CONTENT,
            MovieContract.ReviewEntry.COLUMN_URL,
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_API_ID
    };

    public static final int COL_ID = 0;
    public static final int COL_CONTENT = 1;
    public static final int COL_URL = 2;
    public static final int COL_AUTHOR = 3;
    public static final int COL_API_ID = 4;

    public ReviewModel(Cursor cursor)
    {
        setId(cursor.getString(COL_API_ID));
        setContent(cursor.getString(COL_CONTENT));
        setUrl(cursor.getString(COL_URL));
        setAuthor(cursor.getString(COL_AUTHOR));
    }
}
