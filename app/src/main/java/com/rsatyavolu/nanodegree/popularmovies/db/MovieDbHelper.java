package com.rsatyavolu.nanodegree.popularmovies.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rsatyavolu.nanodegree.popularmovies.model.MovieItemModel;

/**
 * Created by rsatyavolu on 8/18/15.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Movies.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String INT_TYPE = " INTEGER";

    private static final String TRUE = "true";
    private static final String FALSE = "false";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MovieDbContract.MovieEntry.TABLE_NAME + " (" +
                    MovieDbContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                    MovieDbContract.MovieEntry.COLUMN_MOVIE_ID + INT_TYPE + COMMA_SEP +
                    MovieDbContract.MovieEntry.COLUMN_MOVIE_NAME + TEXT_TYPE + COMMA_SEP +
                    MovieDbContract.MovieEntry.COLUMN_MOVIE_FAVORITE + TEXT_TYPE +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MovieDbContract.MovieEntry.TABLE_NAME;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void insertMovieRow(MovieItemModel movie) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MovieDbContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        values.put(MovieDbContract.MovieEntry.COLUMN_MOVIE_NAME, movie.getTitle());
        values.put(MovieDbContract.MovieEntry.COLUMN_MOVIE_FAVORITE, movie.getFavorite());

        long newRowId = db.insert(
                MovieDbContract.MovieEntry.TABLE_NAME,
                null,
                values);
        Log.d("MyClass", String.valueOf(newRowId) + " - " + String.valueOf(movie.getId()) + " - " + movie.getTitle());
        db.close();
    }

    public String movieExists(MovieItemModel movie) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
            MovieDbContract.MovieEntry.COLUMN_MOVIE_FAVORITE
        };

        String selection = MovieDbContract.MovieEntry.COLUMN_MOVIE_ID + "=?";

        String[] selectionArgs = {
                String.valueOf(movie.getId())
        };

        Cursor cursor = db.query(
                MovieDbContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        boolean movieExists = cursor.moveToFirst();
        Log.d("MyClass", String.valueOf(movieExists) + " - " + String.valueOf(movie.getId()) + " - " + movie.getTitle());
        if(movieExists) {
            String favorite = cursor.getString(cursor.getColumnIndex(MovieDbContract.MovieEntry.COLUMN_MOVIE_FAVORITE));
            db.close();
            return favorite;
        } else {
            db.close();
            return null;
        }
    }

    public void markMovieFavorite(MovieItemModel movie, boolean favorite) {
        SQLiteDatabase db = getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(MovieDbContract.MovieEntry.COLUMN_MOVIE_FAVORITE, (favorite ? TRUE : FALSE));

        String selection = MovieDbContract.MovieEntry.COLUMN_MOVIE_ID + "= ?";
        String[] selectionArgs = { String.valueOf(movie.getId()) };

        int count = db.update(
                MovieDbContract.MovieEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        db.close();
    }
}
