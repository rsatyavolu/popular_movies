package com.rsatyavolu.nanodegree.popularmovies.db;

import android.provider.BaseColumns;

/**
 * Created by rsatyavolu on 8/18/15.
 */
public final class MovieDbContract {

    public MovieDbContract(){}

    public static abstract class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_NAME = "movieName";
        public static final String COLUMN_MOVIE_FAVORITE = "movieIsFavorite";
    }
}
