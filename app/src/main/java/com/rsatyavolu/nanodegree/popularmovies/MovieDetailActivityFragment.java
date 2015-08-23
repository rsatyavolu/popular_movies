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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.rsatyavolu.nanodegree.popularmovies.adapter.MovieTrailerViewAdapter;
import com.rsatyavolu.nanodegree.popularmovies.db.MovieDbHelper;
import com.rsatyavolu.nanodegree.popularmovies.model.MovieItemModel;
import com.rsatyavolu.nanodegree.popularmovies.model.VideoModel;
import com.squareup.picasso.Picasso;

import java.util.Iterator;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment implements OnTaskCompleted {

    private static final String MOVIE_DETAILS_URL = "http://api.themoviedb.org/3/movie/";
    private static final String MOVIE_VIDEOS_URL_PREFIX = "http://api.themoviedb.org/3/movie/";
    private static final String MOVIE_VIDEOS_URL_POSTFIX = "/videos";

    private static final String ERROR_STRING = "Unable to connect and retrive movie information.";

    private static final String SELECTED_MOVIE_ID = "selected_movie_id";
    private static final String SELECTED_MOVIE = "selected_movie";

    TextView title;
    ImageView poster;
    TextView overview;
    TextView year;
    TextView length;
    TextView date;
    LinearLayout trailerList;
    View horizontalSeperator;
    TextView trailersTitle;

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

        title = (TextView) rootView.findViewById(R.id.movie_title);
        poster = (ImageView) rootView.findViewById(R.id.movie_poster);
        overview = (TextView) rootView.findViewById(R.id.movie_overview);
        year = (TextView) rootView.findViewById(R.id.movie_year);
        length = (TextView) rootView.findViewById(R.id.movie_length);
        date = (TextView) rootView.findViewById(R.id.movie_release_date);

        favoriteSwitch = (Switch)rootView.findViewById(R.id.favorite);

        horizontalSeperator = rootView.findViewById(R.id.horizontal_seperator);
        trailersTitle = (TextView)rootView.findViewById(R.id.trailers_title);
        trailerList = (LinearLayout) rootView.findViewById(R.id.movie_trailers);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_settings, false);

        apiToken = prefs.getString(getString(R.string.pref_api_token_key), "");

        String selectedMovieId = null;

        movieTrailerAdapter = new MovieTrailerViewAdapter(getActivity(), trailerList);
        dbHelper = new MovieDbHelper(getActivity());

        favoriteSwitch.setVisibility(View.INVISIBLE);
        horizontalSeperator.setVisibility(View.INVISIBLE);
        trailersTitle.setVisibility(View.INVISIBLE);
        favoriteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dbHelper.markMovieFavorite(selectedMovie, isChecked);
            }
        });

        final Bundle arguments = getArguments();
        if (arguments != null) {
            selectedMovieId = arguments.getString(SELECTED_MOVIE_ID);
        }

        final Intent intent = getActivity().getIntent();
        if(intent != null) {
            Bundle b = intent.getExtras();
            if(b != null) {
                selectedMovieId = b.getString(SELECTED_MOVIE_ID);
            }
        }

        if(savedInstanceState != null) {
            selectedMovie = (MovieItemModel)savedInstanceState.getSerializable(SELECTED_MOVIE);
            showMovieDetails();
        } else if(selectedMovie == null && selectedMovieId != null) {
            GetMovieDetailsTask movieDetailsTask = new GetMovieDetailsTask(this);
            movieDetailsTask.execute(MOVIE_DETAILS_URL + selectedMovieId, apiToken);
        } else {
            title.setText("Click on a movie to view details.");
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(selectedMovie != null) {
            outState.putSerializable(SELECTED_MOVIE, selectedMovie);
        }
    }

    @Override
    public void showMovieInfo(Object obj) {
        selectedMovie = (MovieItemModel)obj;
        showMovieDetails();
    }

    private void showMovieDetails() {
        favoriteSwitch.setVisibility(View.VISIBLE);
        horizontalSeperator.setVisibility(View.VISIBLE);
        trailersTitle.setVisibility(View.VISIBLE);

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

        if(selectedMovie.getTrailers() == null || selectedMovie.getTrailers().size() == 0) {
            GetMovieTrailersTask trailersTask = new GetMovieTrailersTask();
            trailersTask.execute(MOVIE_VIDEOS_URL_PREFIX + String.valueOf(selectedMovie.getId()) + MOVIE_VIDEOS_URL_POSTFIX, apiToken);
        } else {
            Iterator<VideoModel> itr = selectedMovie.getTrailers().iterator();
            while (itr.hasNext()) {
                movieTrailerAdapter.addTrailerToView(itr.next());
            }

        }
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
            List<VideoModel> trailers = MovieDatabaseUtility.extractVideoModels(jsonString);
            selectedMovie.setTrailers(trailers);
            Iterator<VideoModel> itr = trailers.iterator();
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

