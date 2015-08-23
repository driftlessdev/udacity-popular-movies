package com.testinprod.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.testinprod.popularmovies.R;
import com.testinprod.popularmovies.api.TheMovieDBConsts;
import com.testinprod.popularmovies.fragments.MovieGridFragment;

/**
 * Created by Tim on 7/8/2015.
 */
public class MovieAdapter extends CursorAdapter {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.titleView.setVisibility(View.VISIBLE);
        viewHolder.titleView.setText(cursor.getString(MovieGridFragment.COL_TITLE));
        String posterPath = cursor.getString(MovieGridFragment.COL_POSTER_PATH);
        if(posterPath != null && !posterPath.isEmpty())
        {
            Picasso.with(context)
                    .load(TheMovieDBConsts.POSTER_BASE_URL + posterPath)
                    .placeholder(R.drawable.movie_board)
                    .into(viewHolder.posterView, viewHolder);
        }
    }

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder implements Callback {
        public final ImageView posterView;
        public final TextView titleView;

        @Override
        public void onSuccess() {
            titleView.setVisibility(View.GONE);
        }

        @Override
        public void onError() {

        }

        public ViewHolder(View view)
        {
            posterView = (ImageView) view.findViewById(R.id.ivMoviePoster);
            titleView = (TextView) view.findViewById(R.id.tvTitlePlaceholder);
        }
    }

}
