package com.testinprod.popularmovies.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.testinprod.popularmovies.models.Movie;

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
public class MovieDiscoverTask extends AsyncTask<String, Void, ArrayList<Movie>> {
    private static final String API_KEY = "***REMOVED***";
    private static final String API_PARAM = "api_key";
    private static final String JSON_RESULTS = "results";
    private static final String LOG_TAG = MovieDiscoverTask.class.getSimpleName();

    private MovieDiscoverTaskResults mCallback;

    public MovieDiscoverTask(MovieDiscoverTaskResults callback)
    {
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> movies) {
        super.onPostExecute(movies);
        if(mCallback != null)
        {
            mCallback.handleMovieDiscoverResults(movies);
        }
    }

    // TODO: Pass in sorting
    protected ArrayList<Movie> doInBackground(String... params) {
        if(mCallback == null)
        {
            return null;
        }

        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        String jsonResults;

        try{
            Uri.Builder builder = Uri.parse("http://api.themoviedb.org/3/discover/movie").buildUpon();
            builder.appendQueryParameter(API_PARAM, API_KEY);
            String urlStr = builder.build().toString();
            URL url = new URL(urlStr);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            if(inputStream == null)
            {
                return new ArrayList<>();
            }
            StringBuffer buffer = new StringBuffer();

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
            Log.v(LOG_TAG, "JSON Results: " + jsonResults);
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

        ArrayList<Movie> movies = new ArrayList<>();
        try {
            JSONObject result = new JSONObject(jsonResults);
            JSONArray moviesArray = result.getJSONArray(JSON_RESULTS);
            for(int i = 0; i < moviesArray.length(); i++)
            {
                movies.add(new Movie(moviesArray.getJSONObject(i)));
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
        void handleMovieDiscoverResults(ArrayList<Movie> movies);
    }
}
