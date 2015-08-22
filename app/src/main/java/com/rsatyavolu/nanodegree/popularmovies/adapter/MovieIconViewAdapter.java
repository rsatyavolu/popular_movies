package com.rsatyavolu.nanodegree.popularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.rsatyavolu.nanodegree.popularmovies.R;
import com.rsatyavolu.nanodegree.popularmovies.model.MovieItemModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by rsatyavolu on 7/18/15.
 */
public class MovieIconViewAdapter extends BaseAdapter {

    private Context context;
    private List<MovieItemModel> movieModels;

    public MovieIconViewAdapter(Context context, List<MovieItemModel> movieModels) {
        this.context = context;
        this.movieModels = movieModels;
    }

    @Override
    public int getCount() {
        if(movieModels == null) return 0;
        return movieModels.size();
    }

    public void addMovie(MovieItemModel movie) {
        movieModels.add(movie);
        this.notifyDataSetChanged();
    }

    @Override
    public MovieItemModel getItem(int position) {
        return movieModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /*
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
        } else {
            imageView = (ImageView) convertView;
        }
        String iconUrl = movieModels.get(position).getIconUrl();
        Picasso.with(context).load(iconUrl).into(imageView);

        return imageView;
    }
    */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi=convertView;
        GridView grid;
        if(convertView==null) {
            vi = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        }

        vi.setFocusable(false);
        vi.setFocusableInTouchMode(false);
        ImageView poster = (ImageView) vi.findViewById(R.id.poster);
        poster.setFocusable(false);
        poster.setFocusableInTouchMode(false);

        String iconUrl = movieModels.get(position).getMediumIconUrl();

        Picasso.with(context).load(iconUrl).into(poster);

        return vi;
    }

    public void clearContent() {
        movieModels.clear();
        this.notifyDataSetChanged();
    }
}
