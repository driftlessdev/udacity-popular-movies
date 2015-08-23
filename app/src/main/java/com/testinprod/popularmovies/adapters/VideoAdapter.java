package com.testinprod.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.testinprod.popularmovies.R;
import com.testinprod.popularmovies.fragments.MovieDetailFragment;

/**
 * Created by Tim on 8/22/2015.
 */
public class VideoAdapter extends CursorAdapter {

    public VideoAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.video_item, parent, false);
        VideoViewHolder holder = new VideoViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        VideoViewHolder holder = (VideoViewHolder) view.getTag();

        holder.videoName.setText(cursor.getString(MovieDetailFragment.COL_VIDEO_NAME));
        holder.videoType.setText(cursor.getString(MovieDetailFragment.COL_VIDEO_TYPE));

    }

    public static class VideoViewHolder {
        public final TextView videoName;
        public final TextView videoType;

        public VideoViewHolder(View root)
        {
            videoName = (TextView) root.findViewById(R.id.tvVideoName);
            videoType = (TextView) root.findViewById(R.id.tvVideoType);
        }
    }
}
