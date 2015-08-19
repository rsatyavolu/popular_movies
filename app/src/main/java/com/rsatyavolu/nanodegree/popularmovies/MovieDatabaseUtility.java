package com.rsatyavolu.nanodegree.popularmovies;

import android.net.Uri;
import android.provider.MediaStore;

import com.rsatyavolu.nanodegree.popularmovies.model.MovieItemModel;
import com.rsatyavolu.nanodegree.popularmovies.model.VideoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsatyavolu on 8/19/15.
 */
public class MovieDatabaseUtility {

    public static StringBuffer openConnectionToMovieDB(Uri builtUri) {
        URL url = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        StringBuffer jsonString = new StringBuffer();
        InputStream inputStream = null;
        try {
            url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {

                jsonString.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonString;
    }

    public static MovieItemModel extractMovieDetailModel(String jsonString) {
        try {
            JSONObject movie = new JSONObject(jsonString);

            MovieItemModel movieModel = new MovieItemModel();

            movieModel.setId(movie.getInt("id"));
            movieModel.setTitle(movie.getString("original_title"));

            movieModel.setIconUrl(movie.getString("poster_path"));
            movieModel.setOverview(movie.getString("overview"));
            movieModel.setReleaseDate(movie.getString("release_date"));
            movieModel.setRuntime(movie.getInt("runtime"));
            movieModel.setFavorite("false");

            return movieModel;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<VideoModel> extractVideoModels(String jsonString) {
        List<VideoModel> videoModels = new ArrayList<VideoModel>();

        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray results = json.getJSONArray("results");
            for(int i=0; i<results.length(); i++) {
                JSONObject movie = results.getJSONObject(i);
                VideoModel videoModel = new VideoModel();
                videoModel.setId(movie.getString("id"));
                videoModel.setKey(movie.getString("key"));
                videoModel.setName(movie.getString("name"));
                videoModel.setSite(movie.getString("site"));
                videoModel.setType(movie.getString("type"));

                videoModels.add(videoModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return videoModels;
    }
}
