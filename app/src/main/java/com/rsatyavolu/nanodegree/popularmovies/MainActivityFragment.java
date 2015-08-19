package com.rsatyavolu.nanodegree.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.rsatyavolu.nanodegree.popularmovies.adapter.MovieIconViewAdapter;
import com.rsatyavolu.nanodegree.popularmovies.model.MovieItemModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivityFragment extends Fragment {

    private static final String GET_METHOD = "GET";
    private static final String SORT_ORDER = "desc";
    public static final String SELECTED_MOVIE = "selected_movie";
    private static final String DISCOVER_MOVIES_URL = "http://api.themoviedb.org/3/discover/movie";
    private static final String ERROR_STRING = "Unable to connect and retrive movies. Please verify your \"license key\" in settings.";

    private MovieIconViewAdapter movieListAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setColumnWidth(200);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent explicitIntent = new Intent(getActivity(), MovieDetailActivity.class);

                Bundle b = new Bundle();
                b.putSerializable(SELECTED_MOVIE, movieListAdapter.getItem(position));

                explicitIntent.putExtras(b);
                startActivity(explicitIntent);

            }
        });

        ArrayList<MovieItemModel> data = new ArrayList<MovieItemModel>();

        movieListAdapter = new MovieIconViewAdapter(getActivity(), data);
        gridView.setAdapter(movieListAdapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_settings, false);
        String apiToken = prefs.getString(getString(R.string.pref_api_token_key), "");

        if(apiToken.length() <= 25) {
            Intent settings = new Intent(getActivity(), SettingsActivity.class);
            startActivity(settings);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_settings, false);
        String apiToken = prefs.getString(getString(R.string.pref_api_token_key), "");
        String sortOption = prefs.getString(getString(R.string.pref_sort_key), "");

        if(apiToken.length() > 25) {
            movieListAdapter.clearContent();
            DiscoverMoviesTask discoverMoviesTask = new DiscoverMoviesTask();
            discoverMoviesTask.execute(DISCOVER_MOVIES_URL, apiToken, sortOption);
        }
    }

    class DiscoverMoviesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String movieDbBaseURL = params[0];
            String apiToken = params[1];
            String sortOption = params[2];
            String sortOrder = SORT_ORDER;

            Uri builtUri = Uri.parse(movieDbBaseURL).buildUpon()
                    .appendQueryParameter("sort_by", sortOption + "." + sortOrder)
                    .appendQueryParameter("api_key", apiToken).build();

            return MovieDatabaseUtility.openConnectionToMovieDB(builtUri).toString();
        }

        @Override
        protected void onPostExecute(String jsonString) {
            if(jsonString.toString().length() == 0) {
                Toast.makeText(getActivity(), ERROR_STRING, Toast.LENGTH_LONG).show();
                return;
            }
            try {
                JSONObject json = new JSONObject(jsonString);
                JSONArray results = json.getJSONArray("results");
                for(int i=0; i<results.length(); i++) {
                    JSONObject movie = results.getJSONObject(i);
                    MovieItemModel movieModel = new MovieItemModel();
                    movieModel.setId(movie.getInt("id"));
                    movieModel.setTitle(movie.getString("original_title"));

                    movieModel.setIconUrl(movie.getString("poster_path"));
                    movieModel.setOverview(movie.getString("overview"));
                    movieModel.setReleaseDate(movie.getString("release_date"));

                    movieListAdapter.addMovie(movieModel);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
