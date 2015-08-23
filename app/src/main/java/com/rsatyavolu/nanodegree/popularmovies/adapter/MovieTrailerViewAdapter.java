package com.rsatyavolu.nanodegree.popularmovies.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rsatyavolu.nanodegree.popularmovies.R;
import com.rsatyavolu.nanodegree.popularmovies.model.VideoModel;

/**
 * Created by rsatyavolu on 8/18/15.
 */
public class MovieTrailerViewAdapter {

    private LinearLayout trailerList;
    private Context context;

    public MovieTrailerViewAdapter(Context context, LinearLayout trailerList) {
        this.context = context;
        this.trailerList = trailerList;
    }

    public void addTrailerToView(VideoModel trailer) {
        if(trailer.getType().equals(VideoModel.TRAILER_VIDEO_TYPE)) {
            View view = LayoutInflater.from(context).inflate(R.layout.trailer_list_item, null);
            ImageView videoIcon = (ImageView) view.findViewById(R.id.trailer_play_icon);
            TextView trailerName = (TextView) view.findViewById(R.id.trailer_name);

            videoIcon.setOnClickListener(new PlayVideoListener(trailer.getKey()));
            trailerName.setText(trailer.getName());

            trailerList.addView(view);
        }
    }

    class PlayVideoListener implements View.OnClickListener {

        public static final String YOUTUBE_APP_URI = "vnd.youtube:";
        public static final String YOUTUBE_PLAY_URL = "http://www.youtube.com/watch?v=";
        String key;
        PlayVideoListener(String key) {
            this.key = key;
        }

        @Override
        public void onClick(View v) {
            try{
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_APP_URI + key));
                context.startActivity(intent);
            }catch (ActivityNotFoundException ex){
                Intent intent=new Intent(Intent.ACTION_VIEW,
                        Uri.parse(YOUTUBE_PLAY_URL + key));
                context.startActivity(intent);
            }
        }
    }

}
