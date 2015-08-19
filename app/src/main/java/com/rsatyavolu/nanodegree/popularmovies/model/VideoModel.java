package com.rsatyavolu.nanodegree.popularmovies.model;

import java.io.Serializable;

/**
 * Created by rsatyavolu on 8/18/15.
 */
public class VideoModel implements Serializable {
    String id;
    String key;
    String name;
    String site;
    String type;

    public static final String TRAILER_VIDEO_TYPE = "Trailer";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
