package com.testinprod.popularmovies.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.testinprod.popularmovies.models.MovieParcel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Tim on 7/8/2015.
 */
//TODO: Replace with Retrofit: http://square.github.io/retrofit/
public class MovieDiscoverTask extends AsyncTask<String, Void, ArrayList<MovieParcel>> {
    private static final String API_KEY = "***REMOVED***";
    private static final String API_PARAM = "api_key";
    private static final String JSON_RESULTS = "results";
    private static final String SORT_PARAM = "sort_by";
    private static final String LOG_TAG = MovieDiscoverTask.class.getSimpleName();

    private MovieDiscoverTaskResults mCallback;

    public MovieDiscoverTask(MovieDiscoverTaskResults callback)
    {
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(ArrayList<MovieParcel> movies) {
        super.onPostExecute(movies);
        if(mCallback != null)
        {
            Log.v(LOG_TAG, "Movies found: " + movies.size());
            mCallback.handleMovieDiscoverResults(movies);
        }
    }

    protected ArrayList<MovieParcel> doInBackground(String... params) {
        if(mCallback == null)
        {
            return null;
        }

        String sortKey = "";

        if(params.length>0)
        {
            sortKey = params[0];
        }


        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        String jsonResults;

        Log.v(LOG_TAG, "Fetching with sorting: " + sortKey);

        try{
            Uri.Builder builder = Uri.parse("http://api.themoviedb.org/3/discover/movie").buildUpon();
            builder.appendQueryParameter(API_PARAM, API_KEY);
            if(!sortKey.isEmpty())
            {
                builder.appendQueryParameter(SORT_PARAM, sortKey);
            }
            String urlStr = builder.build().toString();
            Log.v(LOG_TAG, "Query: " + urlStr);
            URL url = new URL(urlStr);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            if(inputStream == null)
            {
                return new ArrayList<>();
            }
            StringBuilder buffer = new StringBuilder();

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while((line = bufferedReader.readLine()) != null)
            {
                buffer.append(line + "\n");
            }

            if(buffer.length() == 0)
            {
                return new ArrayList<>();
            }
            jsonResults = buffer.toString();
        } catch(IOException e)
        {
            Log.e(LOG_TAG, "Error loading discover list", e);
            return new ArrayList<>();
        } finally{
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        ArrayList<MovieParcel> movies = new ArrayList<>();
        try {
            JSONObject result = new JSONObject(jsonResults);
            JSONArray moviesArray = result.getJSONArray(JSON_RESULTS);
            for(int i = 0; i < moviesArray.length(); i++)
            {
                movies.add(new MovieParcel(moviesArray.getJSONObject(i)));
            }
        } catch (JSONException e)
        {
            Log.e(LOG_TAG, "Error parsing JSON", e);
            e.printStackTrace();
            return new ArrayList<>();
        }
        return movies;
    }

    public interface MovieDiscoverTaskResults{
        void handleMovieDiscoverResults(ArrayList<MovieParcel> movies);
    }
}
