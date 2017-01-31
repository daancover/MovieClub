package com.example.android.movieclub.details;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movieclub.R;
import com.example.android.movieclub.database.MoviesContract;
import com.example.android.movieclub.movie.MovieData;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Daniel on 31/01/2017.
 */

public class DetailsFragment extends Fragment
{
    public static final String ARG_MOVIE = "movie";

    private MovieData mMovieData;

    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.iv_movie_poster) ImageView mPoster;
    @BindView(R.id.tv_movie_title) TextView mTitle;
    @BindView(R.id.tv_movie_actors) TextView mActors;
    @BindView(R.id.tv_movie_director) TextView mDirector;
    @BindView(R.id.tv_movie_runtime) TextView mRuntime;
    @BindView(R.id.tv_movie_genre) TextView mGenre;
    @BindView(R.id.tv_movie_plot) TextView mPlot;
    @BindView(R.id.tv_movie_released) TextView mReleased;
    @BindView(R.id.tv_movie_metascore) TextView mMetascore;
    @BindView(R.id.tv_movie_imdb_rating) TextView mImdbRating;

    public static DetailsFragment newInstance(MovieData movieData)
    {
        DetailsFragment detailsFragment = new DetailsFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, movieData);
        detailsFragment.setArguments(args);

        return detailsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.details_fragment, container, false);
        Bundle args = getArguments();

        if(args.containsKey(ARG_MOVIE))
        {
            ButterKnife.bind(this, rootView);

            // Get movie data from previous activity
            mMovieData = args.getParcelable(ARG_MOVIE);

            // Load movie image
            Picasso.with(getContext()).load(mMovieData.getPoster()).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder).into(mPoster);

            // Fill in all text views
            mTitle.setText(mMovieData.getTitle());
            mActors.setText(mMovieData.getActors());
            mDirector.setText(mMovieData.getDirector());
            mRuntime.setText(mMovieData.getRuntime());
            mGenre.setText(mMovieData.getGenre());
            mPlot.setText(mMovieData.getPlot());
            mReleased.setText(mMovieData.getReleased());
            mMetascore.setText(mMovieData.getMetascore());
            mImdbRating.setText(mMovieData.getImdbRating());

            mFab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    deleteMovie();
                }
            });
        }

        return rootView;
    }

    void deleteMovie()
    {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.remove_dialog, null);

        new AlertDialog.Builder(getContext()).setMessage(R.string.delete_movie_question).setView(view).setPositiveButton(R.string.action_confirm, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                MoviesContract.MovieEntry.deleteMovie(getContext(), mMovieData);
                Toast.makeText(getContext(), getString(R.string.removed_movie), Toast.LENGTH_SHORT).show();
                NavUtils.navigateUpFromSameTask(getActivity());
            }
        }).setNegativeButton(R.string.action_cancel, null).show();
    }
}
