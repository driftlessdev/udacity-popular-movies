package com.testinprod.popularmovies.models;

import android.content.ContentValues;

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
}
