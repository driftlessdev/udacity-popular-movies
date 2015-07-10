package com.testinprod.popularmovies.api;

import com.testinprod.popularmovies.models.MovieModel;

/**
 * Created by tschaab on 7/10/2015.
 */
public final class TheMovieDBConsts {
    public static final String API_KEY = "***REMOVED***";
    public static final String API_PARAM = "api_key";
    public static final String JSON_RESULTS = "results";
    public static final String SORT_PARAM = "sort_by";
    public static final String API_URL = "http://api.themoviedb.org";
    public static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";
    public static final String EXTRA_MOVIE = MovieModel.class.getCanonicalName();
}
