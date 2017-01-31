package com.example.android.movieclub.details;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.movieclub.R;
import com.example.android.movieclub.movie.MovieData;

/**
 * Created by Daniel on 31/01/2017.
 */

public class DetailsAdapter extends FragmentPagerAdapter
{
    private static final int NUM_ITEMS = 2;

    private static final int DETAILS = 0;
    private static final int TRAILERS = 1;

    Context mContext;
    MovieData mMovieData;

    public DetailsAdapter(FragmentManager fm, Context context, MovieData movieData)
    {
        super(fm);
        mContext = context;
        mMovieData = movieData;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch(position)
        {
            case DETAILS:
            {
                return DetailsFragment.newInstance(mMovieData);
            }

            case TRAILERS:
            {
                return TrailersFragment.newInstance(mMovieData);
            }

            default:
            {
                return null;
            }
        }
    }

    @Override
    public int getCount()
    {
        return NUM_ITEMS;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return position == 0 ? mContext.getString(R.string.details_tab) : mContext.getString(R.string.trailers_tab);
    }
}
