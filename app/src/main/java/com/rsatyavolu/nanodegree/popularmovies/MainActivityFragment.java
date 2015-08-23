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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MainActivityFragment extends Fragment {

    private static final String GET_METHOD = "GET";
    private static final String SORT_ORDER = "desc";
    public static final String SELECTED_MOVIE = "selected_movie";
    private static final String DISCOVER_MOVIES_URL = "http://api.themoviedb.org/3/discover/movie";
    private static final String ERROR_STRING = "Unable to connect and retrive movies. Please verify your \"license key\" in settings.";
    private static final String MOVIE_SEARCH_RESULTS = "movie_list";

    private MovieIconViewAdapter movieListAdapter;
    private List<MovieItemModel> data;

    public interface Callback {
        public void onItemSelected(MovieItemModel dateUri);
    }

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
                if (parent.getItemAtPosition(position) != null) {
                    ((Callback) getActivity())
                            .onItemSelected(movieListAdapter.getItem(position)
                            );
                }
            }
        });

        if(savedInstanceState != null) {
            data = (List<MovieItemModel>) savedInstanceState.getSerializable(MOVIE_SEARCH_RESULTS);
        } else {
            data = new ArrayList<MovieItemModel>();
        }

        movieListAdapter = new MovieIconViewAdapter(getActivity(), data);
        gridView.setAdapter(movieListAdapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_settings, false);
        String apiToken = prefs.getString(getString(R.string.pref_api_token_key), "");
        String sortOption = prefs.getString(getString(R.string.pref_sort_key), "");

        if(apiToken.length() <= 25) {
            Intent settings = new Intent(getActivity(), SettingsActivity.class);
            startActivity(settings);

            return rootView;
        }

        if(data.size() == 0) {
            DiscoverMoviesTask discoverMoviesTask = new DiscoverMoviesTask();
            discoverMoviesTask.execute(DISCOVER_MOVIES_URL, apiToken, sortOption);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(movieListAdapter != null) {
            outState.putSerializable(MOVIE_SEARCH_RESULTS, (Serializable) movieListAdapter.getMovieList());
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
