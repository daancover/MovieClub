package com.example.android.movieclub;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movieclub.database.MoviesContract;
import com.example.android.movieclub.movie.MovieData;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    // Tag for debugging purposes
    private static final String TAG = DetailsActivity.class.getSimpleName();

    MovieData mMovieData;

    @BindView(R.id.pb_progress_bar) ProgressBar mProgressBar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Load saved Settings
        setupSharedPreferences();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        ActionBar actionBar = this.getSupportActionBar();

        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent().hasExtra(MovieData.EXTRA_MOVIE_DATA))
        {
            // Get movie data from previous activity
            mMovieData = getIntent().getParcelableExtra(MovieData.EXTRA_MOVIE_DATA);

            // Load movie image
            Picasso.with(this).load(mMovieData.getPoster()).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder).into(mPoster);

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
                    /*MoviesContract.MovieEntry.deleteMovie(DetailsActivity.this, mMovieData);
                    Toast.makeText(DetailsActivity.this, getString(R.string.removed_movie), Toast.LENGTH_SHORT).show();
                    onBackPressed();*/
                    deleteMovie();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupSharedPreferences()
    {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        loadAppColorFormPreferences(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if(key.equals(getString(R.string.pref_app_color_key)))
        {
            recreate();
        }
    }

    private void loadAppColorFormPreferences(SharedPreferences sharedPreferences)
    {
        String appColorPreference = sharedPreferences.getString(getString(R.string.pref_app_color_key), getString(R.string.pref_app_color_value_dark));

        if(appColorPreference.equals(getString(R.string.pref_app_color_value_dark)))
        {
            setTheme(R.style.AppTheme);
        }

        else if(appColorPreference.equals(getString(R.string.pref_app_color_value_light)))
        {
            setTheme(R.style.AppThemeLight);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    void deleteMovie()
    {
        final View view = LayoutInflater.from(this).inflate(R.layout.remove_dialog, null);

        new AlertDialog.Builder(this).setMessage(R.string.delete_movie_question).setView(view).setPositiveButton(R.string.action_confirm, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                MoviesContract.MovieEntry.deleteMovie(DetailsActivity.this, mMovieData);
                Toast.makeText(DetailsActivity.this, getString(R.string.removed_movie), Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }).setNegativeButton(R.string.action_cancel, null).show();
    }
}
