package com.rsatyavolu.nanodegree.popularmovies.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsatyavolu on 7/18/15.
 */
public class MovieItemModel implements Serializable {

    private static final String POSTER_BASE_URL =  "http://image.tmdb.org/t/p/";
    private static final String POSTER_SMALL_SIZE = "w92";
    private static final String POSTER_MED_SIZE = "w154";
    private static final String POSTER_LARGE_SIZE = "w185";

    private String title;
    private String iconUrl;
    private String overview;
    private int id;
    private String releaseDate;
    private int runtime;

    public void setTrailers(List<VideoModel> trailers) {
        this.trailers = trailers;
    }

    private List<VideoModel> trailers = new ArrayList<VideoModel>();
    private String favorite;

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIconUrl() {
        return getSmallIconUrl();
    }

    public String getSmallIconUrl() {
        return POSTER_BASE_URL + POSTER_SMALL_SIZE + this.iconUrl;
    }

    public String getMediumIconUrl() {
        return POSTER_BASE_URL + POSTER_MED_SIZE + this.iconUrl;
    }

    public String getLargeIconUrl() {
        return POSTER_BASE_URL + POSTER_LARGE_SIZE + this.iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public List<VideoModel> getTrailers() {
        return trailers;
    }

    public void addTrailer(VideoModel trailer) {
        trailers.add(trailer);
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }
}
