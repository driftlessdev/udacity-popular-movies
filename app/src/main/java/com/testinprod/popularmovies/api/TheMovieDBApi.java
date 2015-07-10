package com.testinprod.popularmovies.api;

import com.testinprod.popularmovies.models.MovieDiscovery;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by tschaab on 7/10/2015.
 */
public interface TheMovieDBApi {

    @GET("/3/discover/movie")
    public void discoverMovies(@Query("api_key") String apiKey, @Query("sort_by") String sorting, Callback<MovieDiscovery> callback);
}
