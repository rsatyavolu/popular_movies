package com.rsatyavolu.nanodegree.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.rsatyavolu.nanodegree.popularmovies.adapter.MovieTrailerViewAdapter;
import com.rsatyavolu.nanodegree.popularmovies.db.MovieDbHelper;
import com.rsatyavolu.nanodegree.popularmovies.model.MovieItemModel;
import com.rsatyavolu.nanodegree.popularmovies.model.VideoModel;
import com.squareup.picasso.Picasso;

import java.util.Iterator;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment implements OnTaskCompleted {

    private static final String MOVIE_DETAILS = "Movie Details";
    private static final String MOVIE_DETAILS_URL = "http://api.themoviedb.org/3/movie/";
    private static final String MOVIE_VIDEOS_URL_PREFIX = "http://api.themoviedb.org/3/movie/";
    private static final String MOVIE_VIDEOS_URL_POSTFIX = "/videos";

    private static final String ERROR_STRING = "Unable to connect and retrive movie information.";

    TextView title;
    ImageView poster;
    TextView overview;
    TextView year;
    TextView length;
    TextView date;
    private MovieTrailerViewAdapter movieTrailerAdapter;
    private MovieItemModel selectedMovie;
    private Switch favoriteSwitch;
    private MovieDbHelper dbHelper;
    private String apiToken;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        android.support.v7.app.ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        ab.setTitle(MOVIE_DETAILS);

        title = (TextView) rootView.findViewById(R.id.movie_title);
        poster = (ImageView) rootView.findViewById(R.id.movie_poster);
        overview = (TextView) rootView.findViewById(R.id.movie_overview);
        year = (TextView) rootView.findViewById(R.id.movie_year);
        length = (TextView) rootView.findViewById(R.id.movie_length);
        date = (TextView) rootView.findViewById(R.id.movie_release_date);

        movieTrailerAdapter = new MovieTrailerViewAdapter(getActivity());
        dbHelper = new MovieDbHelper(getActivity());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_settings, false);

        apiToken = prefs.getString(getString(R.string.pref_api_token_key), "");

        final Intent intent = getActivity().getIntent();

        if(intent != null && intent.getExtras() != null) {
            Bundle b = intent.getExtras();
            MovieItemModel selectedMovie = (MovieItemModel)b.getSerializable(MainActivityFragment.SELECTED_MOVIE);

            GetMovieDetailsTask movieDetailsTask = new GetMovieDetailsTask(this);
            movieDetailsTask.execute(MOVIE_DETAILS_URL + String.valueOf(selectedMovie.getId()), apiToken);
        }

        favoriteSwitch = (Switch)rootView.findViewById(R.id.favorite);
        favoriteSwitch.setVisibility(View.INVISIBLE);
        favoriteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dbHelper.markMovieFavorite(selectedMovie, isChecked);
            }
        });

        return rootView;
    }

    @Override
    public void showMovieInfo(Object obj) {
        selectedMovie = (MovieItemModel)obj;

        favoriteSwitch.setVisibility(View.VISIBLE);

        title.setText(selectedMovie.getTitle());
        year.setText(selectedMovie.getReleaseDate().substring(0, 4));
        length.setText(String.valueOf(selectedMovie.getRuntime()) + " mins");
        date.setText(selectedMovie.getReleaseDate());

        Picasso.with(getActivity()).load(selectedMovie.getLargeIconUrl()).resize(300,420).into(poster);

        overview.setText(selectedMovie.getOverview());

        String favorite = dbHelper.movieExists(selectedMovie);
        if(favorite == null) {
            dbHelper.insertMovieRow(selectedMovie);
            favoriteSwitch.setChecked(false);
        } else {
            favoriteSwitch.setChecked(Boolean.valueOf(favorite).booleanValue());
            selectedMovie.setFavorite(favorite);
        }

        GetMovieTrailersTask trailersTask = new GetMovieTrailersTask();
        trailersTask.execute(MOVIE_VIDEOS_URL_PREFIX + String.valueOf(selectedMovie.getId()) + MOVIE_VIDEOS_URL_POSTFIX, apiToken);
    }

    class GetMovieTrailersTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String baseUrl = params[0];
            String key = params[1];

            Uri builtUri = Uri.parse(baseUrl).buildUpon().appendQueryParameter("api_key", key).build();

            return MovieDatabaseUtility.openConnectionToMovieDB(builtUri).toString();
        }

        @Override
        protected void onPostExecute(String jsonString) {
            if(jsonString.length() == 0) {
                Toast.makeText(getActivity(), ERROR_STRING, Toast.LENGTH_LONG).show();
                return;
            }
            Iterator<VideoModel> itr = MovieDatabaseUtility.extractVideoModels(jsonString).iterator();
            while (itr.hasNext()) {
                movieTrailerAdapter.addTrailerToView(itr.next());
            }
        }
    }

    class GetMovieDetailsTask extends AsyncTask<String, Void, String> {

        private final OnTaskCompleted taskCompleteListener;

        public GetMovieDetailsTask(OnTaskCompleted taskCompleteListener) {
            this.taskCompleteListener = taskCompleteListener;
        }

        @Override
        protected String doInBackground(String... params) {
            String baseUrl = params[0];
            String key = params[1];

            Uri builtUri = Uri.parse(baseUrl).buildUpon().appendQueryParameter("api_key", key).build();

            return MovieDatabaseUtility.openConnectionToMovieDB(builtUri).toString();
        }

        @Override
        protected void onPostExecute(String jsonString) {
            if(jsonString.length() == 0) {
                Toast.makeText(getActivity(), ERROR_STRING, Toast.LENGTH_LONG).show();
                return;
            }
            MovieItemModel movie = MovieDatabaseUtility.extractMovieDetailModel(jsonString);
            taskCompleteListener.showMovieInfo(movie);
        }
    }
}

