package com.testinprod.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tim on 7/8/2015.
 */
public class MovieParcel implements Parcelable {

    public static final String EXTRA_MOVIE = MovieParcel.class.getCanonicalName();

    private static final String ADULT_FIELD = "adult";
    private static final String BACKDROP_PATH_FIELD = "backdrop_path";
    private static final String ID_FIELD = "id";
    private static final String ORIGINAL_TITLE_FIELD = "original_title";
    private static final String OVERVIEW_FIELD = "overview";
    private static final String RELEASE_DATE_FIELD = "release_date";
    private static final String POSTER_PATH_FIELD = "poster_path";
    private static final String POPULARITY_FIELD = "popularity";
    private static final String TITLE_FIELD = "title";
    private static final String VOTE_AVERAGE_FIELD = "vote_average";
    private static final String VOTE_COUNT_FIELD = "vote_count";

    private boolean mIsAdult;
    private String mBackdropPath;
    private String mId;
    private String mOriginalTitle;
    private String mOverview;
    private Date mReleaseDate;
    private String mPosterPath;
    private double mPopularity;
    private String mTitle;
    private double mVoteAverage;
    private int mVoteCount;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mIsAdult ? 1:0));
        dest.writeString(mId);
        dest.writeString(mOriginalTitle);
        dest.writeString(mOverview);
        dest.writeSerializable(mReleaseDate);
        dest.writeString(mPosterPath);
        dest.writeDouble(mPopularity);
        dest.writeString(mTitle);
        dest.writeDouble(mVoteAverage);
        dest.writeInt(mVoteCount);
    }

    private MovieParcel(Parcel source)
    {
        mIsAdult = source.readByte() == 1;
        mId = source.readString();
        mOriginalTitle = source.readString();
        mOverview = source.readString();
        mReleaseDate = (Date) source.readSerializable();
        mPosterPath = source.readString();
        mPopularity = source.readDouble();
        mTitle = source.readString();
        mVoteAverage = source.readDouble();
        mVoteCount = source.readInt();
    }

    public static final Parcelable.Creator<MovieParcel> CREATOR = new Parcelable.Creator<MovieParcel>()
    {
        @Override
        public MovieParcel[] newArray(int size) {
            return new MovieParcel[0];
        }

        @Override
        public MovieParcel createFromParcel(Parcel source) {
            return new MovieParcel(source);
        }
    };

    public MovieParcel(String JsonString)
            throws JSONException
    {
        JSONObject movieJSON = new JSONObject(JsonString);
        loadFromJSON(movieJSON);
    }

    public MovieParcel(JSONObject movieJSON)
            throws JSONException
    {
        loadFromJSON(movieJSON);
    }

    private void loadFromJSON(JSONObject movieJSON)
            throws JSONException
    {
        mId = movieJSON.getString(ID_FIELD);
        mOriginalTitle = movieJSON.getString(ORIGINAL_TITLE_FIELD);
        mOverview = movieJSON.getString(OVERVIEW_FIELD);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            mReleaseDate = dateFormat.parse(movieJSON.getString(RELEASE_DATE_FIELD));
        } catch (ParseException e)
        {
            mReleaseDate = null;
        }

        mPosterPath = movieJSON.getString(POSTER_PATH_FIELD);
        mPopularity = movieJSON.getDouble(POPULARITY_FIELD);
        mTitle = movieJSON.getString(TITLE_FIELD);
        mVoteAverage = movieJSON.getDouble(VOTE_AVERAGE_FIELD);
        mVoteCount = movieJSON.getInt(VOTE_COUNT_FIELD);
        mIsAdult = movieJSON.getBoolean(ADULT_FIELD);
        mBackdropPath = movieJSON.getString(BACKDROP_PATH_FIELD);
    }

    public boolean getIsAdult()
    {
        return mIsAdult;
    }

    public String getID()
    {
        return mId;
    }

    // TODO: Add option to change image size
    public String getPosterPath()
    {
        return "http://image.tmdb.org/t/p/w185" + mPosterPath;
    }

    public String getOriginalTitle()
    {
        return mOriginalTitle;
    }

    public String getOverview()
    {
        return mOverview;
    }

    public Date getReleaseDate()
    {
        return mReleaseDate;
    }

    public double getPopularity()
    {
        return mPopularity;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public double getVoteAverage()
    {
        return mVoteAverage;
    }

    public int getVoteCount()
    {
        return mVoteCount;
    }

    public String getBackdropPath()
    {
        return mBackdropPath;
    }
}
