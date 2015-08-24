
package com.testinprod.popularmovies.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.testinprod.popularmovies.data.MovieContract;

import org.parceler.Parcel;

// Generated via http://www.jsonschema2pojo.org/
@Parcel
public class VideoModel {

    @Expose
    private String id;
    @SerializedName("iso_639_1")
    @Expose
    private String iso6391;
    @Expose
    private String key;
    @Expose
    private String name;
    @Expose
    private String site;
    @Expose
    private Integer size;
    @Expose
    private String type;

    public VideoModel()
    {

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
     *     The iso6391
     */
    public String getIso6391() {
        return iso6391;
    }

    /**
     *
     * @param iso6391
     *     The iso_639_1
     */
    public void setIso6391(String iso6391) {
        this.iso6391 = iso6391;
    }

    /**
     *
     * @return
     *     The key
     */
    public String getKey() {
        return key;
    }

    /**
     *
     * @param key
     *     The key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     *
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     *     The site
     */
    public String getSite() {
        return site;
    }

    /**
     *
     * @param site
     *     The site
     */
    public void setSite(String site) {
        this.site = site;
    }

    /**
     *
     * @return
     *     The size
     */
    public Integer getSize() {
        return size;
    }

    /**
     *
     * @param size
     *     The size
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     *
     * @return
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
    }

    public ContentValues getContentValues()
    {
        ContentValues values = new ContentValues();
        values.put(MovieContract.VideoEntry.COLUMN_TYPE, getType());
        values.put(MovieContract.VideoEntry.COLUMN_SIZE, getSize());
        values.put(MovieContract.VideoEntry.COLUMN_SITE, getSite());
        values.put(MovieContract.VideoEntry.COLUMN_NAME, getName());
        values.put(MovieContract.VideoEntry.COLUMN_KEY, getKey());
        values.put(MovieContract.VideoEntry.COLUMN_API_ID, getId());
        return values;
    }

    public Uri getVideoURI()
    {
        if(!site.equals("YouTube")) {
            return null;
        }
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.youtube.com")
                .appendPath("v")
                .appendPath(key);
        return builder.build();
    }

    public static final String[] ALL_COLUMN_PROJECTION = {
            MovieContract.VideoEntry._ID,
            MovieContract.VideoEntry.COLUMN_KEY,
            MovieContract.VideoEntry.COLUMN_SIZE,
            MovieContract.VideoEntry.COLUMN_API_ID,
            MovieContract.VideoEntry.COLUMN_SITE,
            MovieContract.VideoEntry.COLUMN_NAME,
            MovieContract.VideoEntry.COLUMN_TYPE,
            MovieContract.VideoEntry.COLUMN_MOVIE_ID
    };

    public static final int COL_ID = 0;
    public static final int COL_KEY = 1;
    public static final int COL_SIZE = 2;
    public static final int COL_API_ID = 3;
    public static final int COL_SITE = 4;
    public static final int COL_NAME = 5;
    public static final int COL_TYPE = 6;
    public static final int COL_MOVIE_ID = 7;

    public VideoModel(Cursor cursor)
    {
        setKey(cursor.getString(COL_KEY));
        setSize(cursor.getInt(COL_SIZE));
        setId(cursor.getString(COL_API_ID));
        setSite(cursor.getString(COL_SITE));
        setName(cursor.getString(COL_NAME));
        setType(cursor.getString(COL_TYPE));
    }

}
