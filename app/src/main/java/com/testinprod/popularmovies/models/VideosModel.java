
package com.testinprod.popularmovies.models;

import com.google.gson.annotations.Expose;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class VideosModel {

    @Expose
    private List<VideoModel> results = new ArrayList<VideoModel>();

    public VideosModel()
    {

    }

    /**
     *
     * @return
     *     The results
     */
    public List<VideoModel> getResults() {
        return results;
    }

    /**
     *
     * @param results
     *     The results
     */
    public void setResults(List<VideoModel> results) {
        this.results = results;
    }

}
