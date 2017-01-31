package com.example.android.movieclub.details;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movieclub.R;
import com.example.android.movieclub.movie.MovieData;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Daniel on 31/01/2017.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder>
{
    public static String THUMBNAIL_BASE_URL = "http://img.youtube.com/vi/";
    public static String THUMBNAIL_END_URL = "/hqdefault.jpg";

    TrailersAdapter.TrailerItemClickListener mOnClickListener;
    private MovieData mMovieData;
    private List<String> mUrls;
    private List<String> mNames;
    Context context;

    public TrailersAdapter(Context context, TrailersAdapter.TrailerItemClickListener trailerItemClickListener)
    {
        this.context = context;
        mOnClickListener = trailerItemClickListener;
    }

    public void setTrailerData(List<String> urls, List<String> names)
    {
        mUrls = urls;
        mNames = names;
        notifyDataSetChanged();
    }

    @Override
    public TrailersAdapter.TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.trailer_item, parent, false);

        TrailersAdapter.TrailerViewHolder viewHolder = new TrailersAdapter.TrailerViewHolder(view);

        return viewHolder;
    }

    // Update movie poster
    @Override
    public void onBindViewHolder(TrailersAdapter.TrailerViewHolder holder, int position)
    {
        String url = THUMBNAIL_BASE_URL + mUrls.get(position) + THUMBNAIL_END_URL;
        Picasso.with(context).load(url).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder).into(holder.mThumb);
        holder.mTitle.setText(mNames.get(position));
    }

    @Override
    public int getItemCount()
    {
        if(mNames != null)
        {
            return mNames.size();
        }

        return 0;
    }

    public interface TrailerItemClickListener
    {
        void onTrailerItemClick(int itemIndex);
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        @BindView(R.id.iv_thumbnail) ImageView mThumb;
        @BindView(R.id.tv_trailer_title) TextView mTitle;

        public TrailerViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onTrailerItemClick(clickedPosition);
        }
    }
}