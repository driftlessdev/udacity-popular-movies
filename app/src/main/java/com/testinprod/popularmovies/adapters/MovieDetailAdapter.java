package com.testinprod.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.testinprod.popularmovies.R;
import com.testinprod.popularmovies.api.TheMovieDBConsts;
import com.testinprod.popularmovies.models.MovieModel;
import com.testinprod.popularmovies.models.ReviewModel;
import com.testinprod.popularmovies.models.VideoModel;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by Tim on 8/22/2015.
 */
public class MovieDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private MovieModel mMovie;
    private ArrayList<VideoModel> mVideos;
    private ArrayList<ReviewModel> mReviews;
    private Context mContext;

    private static final int BASIC_INFO = 0;
    private static final int REVIEW = 1;
    private static final int VIDEO = 2;

    public MovieDetailAdapter(Context context, MovieModel movie, ArrayList<VideoModel> videos, ArrayList<ReviewModel> reviewModels)
    {
        Timber.tag(MovieDetailAdapter.class.getSimpleName());
        mContext = context;
        mMovie = movie;
        mVideos = videos;
        mReviews = reviewModels;
    }

    public void setVideos(ArrayList<VideoModel> videos)
    {
        mVideos = videos;
        notifyDataSetChanged();
    }

    public void setReviews(ArrayList<ReviewModel> reviews)
    {
        mReviews = reviews;
        notifyDataSetChanged();
    }

    public void setMovie(MovieModel movie)
    {
        mMovie = movie;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType)
        {
            case BASIC_INFO:
                View basicView = inflater.inflate(R.layout.movie_detail, parent, false);
                viewHolder = new BasicHolder(basicView);
                break;
            case VIDEO:
                View videoView = inflater.inflate(R.layout.video_item, parent, false);
                viewHolder = new VideoHolder(videoView);
                break;
            default:
                throw new InvalidParameterException("Invalid view type: " + viewType);
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {

        int size = 1;
        if(position == 0)
        {
            return BASIC_INFO;
        }

        if(mVideos != null)
        {
            size += mVideos.size();
            if(position < size)
            {
                return VIDEO;
            }
        }

        if(mReviews != null)
        {
            size += mReviews.size();
            if(position < size)
            {
                return REVIEW;
            }
        }

        return -1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType())
        {
            case BASIC_INFO:
                bindBasicInformation((BasicHolder) holder);
                break;

            case VIDEO:
                bindVideo((VideoHolder) holder, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if(mMovie != null)
        {
            count += 1;
        }
        if(mVideos != null)
        {
            count += mVideos.size();
        }
        if(mReviews != null)
        {
            count += mReviews.size();
        }
        return count;
    }

    private void bindVideo(VideoHolder holder, int position)
    {
        VideoModel videoModel = mVideos.get(position - 1);
        holder.videoType.setText(videoModel.getType());
        holder.videoName.setText(videoModel.getName());
    }

    private void bindBasicInformation(BasicHolder holder)
    {
        if(mMovie == null)
        {
            return;
        }
        holder.overview.setText(mMovie.getOverview());

        String path = mMovie.getPosterPath();
        if(path != null && !path.isEmpty())
        {
            Picasso.with(mContext)
                    .load(TheMovieDBConsts.POSTER_BASE_URL + path)
                    .into(holder.movieHeader) ; //, new ImageLoadedCallback());
        }


        holder.rating.setText(mMovie.getVoteAverage() + "/10");


        Date releaseDate = mMovie.getReleaseDateClass();

        String dateText = "Unknown";
        if(releaseDate != null)
        {
            dateText = SimpleDateFormat.getDateInstance().format(releaseDate);
        }
        holder.releaseDate.setText(dateText);
    }

    public class BasicHolder extends RecyclerView.ViewHolder
    {
        public final ImageView movieHeader;
        public final TextView releaseDate;
        public final TextView rating;
        public final TextView overview;

        public BasicHolder(View view)
        {
            super(view);
            movieHeader = (ImageView) view.findViewById(R.id.ivMovieHeader);
            releaseDate = (TextView) view.findViewById(R.id.tvReleaseDate);
            rating = (TextView) view.findViewById(R.id.tvRating);
            overview = (TextView) view.findViewById(R.id.tvOverview);
        }
    }

    public class VideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public final TextView videoName;
        public final TextView videoType;

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition() - 1;
            VideoModel videoModel = mVideos.get(position);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://www.youtube.com/v/").buildUpon().appendPath(videoModel.getKey()).build());
            mContext.startActivity(intent);

        }

        public VideoHolder(View view)
        {
            super(view);
            videoName = (TextView) view.findViewById(R.id.tvVideoName);
            videoType = (TextView) view.findViewById(R.id.tvVideoType);
            view.setOnClickListener(this);
        }


    }
}
