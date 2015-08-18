package com.testinprod.popularmovies.api;

import com.testinprod.popularmovies.models.MovieDiscovery;
import com.testinprod.popularmovies.models.MovieModel;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by tschaab on 7/10/2015.
 */
public interface TheMovieDBApi {

    @GET("/3/discover/movie?vote_count.gte=100")
    MovieDiscovery discoverMovies(@Query("api_key") String apiKey, @Query("sort_by") String sorting);

    @GET("/3/movie/{id}?append_to_response=reviews,videos")
    MovieModel movieDetails(@Path("id") long movieId, @Query("api_key") String apiKey);
}
