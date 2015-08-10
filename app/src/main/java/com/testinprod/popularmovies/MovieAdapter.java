package com.testinprod.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.testinprod.popularmovies.api.TheMovieDBConsts;
import com.testinprod.popularmovies.fragments.MovieGridFragment;
import com.testinprod.popularmovies.models.MovieModel;

import java.util.ArrayList;

/**
 * Created by Tim on 7/8/2015.
 */
public class MovieAdapter extends CursorAdapter {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<MovieModel> mMovieModels;

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

        String posterPath = cursor.getString(MovieGridFragment.COL_POSTER_PATH);
        if(posterPath != null && !posterPath.isEmpty())
        {
            viewHolder.noPosterView.setVisibility(View.GONE);
            viewHolder.posterView.setVisibility(View.VISIBLE);
            Picasso.with(mContext)
                    .load(TheMovieDBConsts.POSTER_BASE_URL + posterPath)
                    .placeholder(R.drawable.movie_board)
                    .into(viewHolder.posterView);
        }
        else
        {
            viewHolder.noPosterView.setVisibility(View.VISIBLE);
            viewHolder.posterView.setVisibility(View.GONE);
            viewHolder.titleView.setText(cursor.getString(MovieGridFragment.COL_TITLE));
        }
    }

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        public final ImageView posterView;
        public final TextView titleView;
        public final View noPosterView;

        public ViewHolder(View view)
        {
            posterView = (ImageView) view.findViewById(R.id.ivMoviePoster);
            titleView = (TextView) view.findViewById(R.id.tvTitlePlaceholder);
            noPosterView = view.findViewById(R.id.rlNoPoster);
        }
    }
}
