package com.rsatyavolu.nanodegree.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.rsatyavolu.nanodegree.popularmovies.model.MovieItemModel;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {

    private boolean mTwoPane;
    public static final String SELECTED_MOVIE_ID = "selected_movie_id";
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.moviecam_icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        setContentView(R.layout.activity_main);
        if(findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if(savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().
                        add(R.id.movie_detail_container, new MovieDetailActivityFragment())
                        .commit();
            }

        } else {
            mTwoPane = false;
        }

        //MovieDetailActivityFragment detailFragment =  ((MovieDetailActivityFragment)getSupportFragmentManager()
                //.findFragmentById(R.id.movie_detail_container));
        //detailFragment
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(MovieItemModel selectedMovie) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putString(SELECTED_MOVIE_ID, String.valueOf(selectedMovie.getId()));

            MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent explicitIntent = new Intent(this, MovieDetailActivity.class);

            Bundle b = new Bundle();
            b.putString(SELECTED_MOVIE_ID, String.valueOf(selectedMovie.getId()));

            explicitIntent.putExtras(b);
            startActivity(explicitIntent);
        }

    }
}
