package com.example.android.movieclub.Movie;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movieclub.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Daniel on 27/01/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>
{
    MovieItemClickListener mOnClickListener;
    private List<MovieData> mMovieData;
    Context context;

    public MovieAdapter(Context context, MovieItemClickListener movieItemClickListener)
    {
        this.context = context;
        mOnClickListener = movieItemClickListener;
    }

    MovieData getMovieData(int position)
    {
        if(mMovieData != null && mMovieData.size() > position)
        {
            return mMovieData.get(position);
        }

        return null;
    }

    public void setMovieData(List<MovieData> movieData)
    {
        mMovieData = movieData;
        notifyDataSetChanged();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.movie_list_item, parent, false);

        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;
    }

    // Update movie poster
    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position)
    {
        holder.mMovieTitle.setText(mMovieData.get(position).getTitle());
        String url = mMovieData.get(position).getPoster();
        Picasso.with(context).load(url).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder).into(holder.mMoviePoster);
    }

    @Override
    public int getItemCount()
    {
        if(mMovieData != null)
        {
            return mMovieData.size();
        }

        return 0;
    }

    public interface MovieItemClickListener
    {
        void onMovieItemClick(int itemIndex);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        ImageView mMoviePoster;
        TextView mMovieTitle;

        public MovieViewHolder(View itemView)
        {
            super(itemView);

            mMoviePoster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            mMovieTitle = (TextView) itemView.findViewById(R.id.tv_movie_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onMovieItemClick(clickedPosition);
        }
    }
}
