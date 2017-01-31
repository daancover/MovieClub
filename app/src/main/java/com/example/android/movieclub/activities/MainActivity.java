package com.example.android.movieclub.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.movieclub.R;
import com.example.android.movieclub.database.MoviesContract;
import com.example.android.movieclub.movie.MovieAdapter;
import com.example.android.movieclub.movie.MovieData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener
{
    // Tag for debugging purposes
    private static final String TAG = MainActivity.class.getSimpleName();

    // Request url parts
    private static final String urlBegin = "http://www.omdbapi.com/?t=";
    private static final String urlEnd = "&y=&plot=short&r=json";

    // Movie list sorting order
    private String sortBy;

    // Visible info
    private MovieAdapter mAdapter;

    @BindView(R.id.pb_progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.rv_movies)  RecyclerView mRecyclerView;
    @BindView(R.id.tv_no_movies) TextView mNoMoviesInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Load saved Settings
        setupSharedPreferences();

        super.onCreate(savedInstanceState);

        // Show app logo in Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Button to add a new movie
        mFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addMovie();
            }
        });

        // Set columns accordingly to the device size and orientation
        int columnsQtt = 3;

        if(findViewById(R.id.tablet_view) != null)
        {
            columnsQtt += 2;
        }

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            columnsQtt += 2;
        }

        // Setup movie list
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columnsQtt, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MovieAdapter(MainActivity.this, this);
        mRecyclerView.setAdapter(mAdapter);

        // Load saved movies form local db
        loadMovieData(false);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Reload movie list
        mAdapter.setMovieData(null);
        loadMovieData(false);
    }

    // Add items to the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Handle action bar item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.action_settings)
        {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Create a dialog for the user to search for a movie
    void addMovie()
    {
        final View view = LayoutInflater.from(this).inflate(R.layout.activity_overlay, null);

        new AlertDialog.Builder(this).setTitle(R.string.movie_name_question).setView(view).setPositiveButton(R.string.action_confirm, new DialogInterface.OnClickListener()
        {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        String movieName = ((EditText) view.findViewById(R.id.et_movie_title)).getText().toString();

                        if(movieName.isEmpty())
                        {
                            Toast.makeText(MainActivity.this, getString(R.string.movie_name_empty), Toast.LENGTH_SHORT).show();
                        }

                        else
                        {
                            requestMovie(movieName);
                        }
                    }
        }).setNegativeButton(R.string.action_cancel, null).show();
    }

    // Load movies from database
    void loadMovieData(boolean justInsertedItem)
    {
        // User Feedback
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        List<MovieData> movieData = MoviesContract.MovieEntry.loadMovies(this, mProgressBar);
        MovieData insertedMovieData = movieData.size() > 0 ? movieData.get(movieData.size() - 1) : null;

        // Check sorting order
        if(sortBy.equals(getString(R.string.pref_sort_by_value_alphabetic)))
        {
            alphabeticalInsertion(movieData);
        }

        else if(sortBy.equals(getString(R.string.pref_sort_by_value_release)))
        {
            releaseDateInsertion(movieData);
        }

        mAdapter.setMovieData(movieData);

        // No movies in the list
        if(movieData.size() == 0)
        {
            mNoMoviesInfo.setVisibility(View.VISIBLE);
        }

        else
        {
            mNoMoviesInfo.setVisibility(View.GONE);
        }

        // Show recently added item
        if(justInsertedItem)
        {
            int positionToShow = movieData.indexOf(insertedMovieData);
            mRecyclerView.scrollToPosition(positionToShow);
        }
    }

    // Insertion sort by name
    private void alphabeticalInsertion(List<MovieData> movieData)
    {
        for (int i = 1; i < movieData.size(); i++)
        {
            MovieData tmp = movieData.get(i);
            int j = i - 1;

            while ((j >= 0) && (movieData.get(j).getTitle().compareTo(tmp.getTitle()) > 0))
            {
                movieData.set(j + 1, movieData.get(j));
                j--;
            }

            movieData.set(j + 1, tmp);
        }
    }

    // Insertion sort by release date
    private void releaseDateInsertion(List<MovieData> movieData)
    {
        List<MovieData> removedItems = new ArrayList<>();

        for(int i = 0; i < movieData.size(); i++)
        {
            if(getDate(movieData.get(i)) == null)
            {
                removedItems.add(movieData.remove(i));
                i--;
            }
        }

        for (int i = 1; i < movieData.size(); i++)
        {
            MovieData tmp = movieData.get(i);
            int j = i - 1;

            while ((j >= 0) && (getDate(movieData.get(j)).compareTo(getDate(tmp)) > 0))
            {
                movieData.set(j + 1, movieData.get(j));
                j--;
            }

            movieData.set(j + 1, tmp);
        }

        for(int i = 0; i < removedItems.size(); i++)
        {
            movieData.add(removedItems.get(i));
        }
    }

    // Get Date object that corresponds to the movie release date
    private Date getDate(MovieData movieData)
    {
        String stringDate = movieData.getReleased();
        int month = 0;

        if(stringDate.contains("Jan"))
        {
            month = 1;
        }

        else if(stringDate.contains("Feb"))
        {
            month = 2;
        }

        else if(stringDate.contains("Mar"))
        {
            month = 3;
        }

        else if(stringDate.contains("Apr"))
        {
            month = 4;
        }

        else if(stringDate.contains("May"))
        {
            month = 5;
        }

        else if(stringDate.contains("Jun"))
        {
            month = 6;
        }

        else if(stringDate.contains("Jul"))
        {
            month = 7;
        }

        else if(stringDate.contains("Aug"))
        {
            month = 8;
        }

        else if(stringDate.contains("Sep"))
        {
            month = 9;
        }

        else if(stringDate.contains("Oct"))
        {
            month = 10;
        }

        else if(stringDate.contains("Nov"))
        {
            month = 11;
        }

        else if(stringDate.contains("Dec"))
        {
            month = 12;
        }

        else
        {
            return null;
        }

        int day = Integer.parseInt(stringDate.substring(0, 2));
        int year = Integer.parseInt(stringDate.substring(stringDate.length() - 4, stringDate.length()));

        return new Date(year, month, day);
    }

    // Rest request to the api
    public MovieData requestMovie(final String movieName)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final JsonObjectRequest jsonRequest =  new JsonObjectRequest(Request.Method.GET, buildUrl(movieName), new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                try
                {
                    // Store all movie data
                    final MovieData movieData = new MovieData(
                            response.getString(MoviesContract.MovieEntry.COLUMN_TITLE),
                            response.getString(MoviesContract.MovieEntry.COLUMN_ACTORS),
                            response.getString(MoviesContract.MovieEntry.COLUMN_DIRECTOR),
                            response.getString(MoviesContract.MovieEntry.COLUMN_RUNTIME),
                            response.getString(MoviesContract.MovieEntry.COLUMN_GENRE),
                            response.getString(MoviesContract.MovieEntry.COLUMN_POSTER),
                            response.getString(MoviesContract.MovieEntry.COLUMN_PLOT),
                            response.getString(MoviesContract.MovieEntry.COLUMN_RELEASED),
                            response.getString(MoviesContract.MovieEntry.COLUMN_METASCORE),
                            response.getString(MoviesContract.MovieEntry.COLUMN_IMBD_RATING),
                            "");

                    // Try to save movie
                    if(MoviesContract.MovieEntry.saveMovie(MainActivity.this, movieData))
                    {
                        Toast.makeText(MainActivity.this, getString(R.string.added_movie), Toast.LENGTH_SHORT).show();
                        mAdapter.setMovieData(null);
                        loadMovieData(true);
                    }

                    else
                    {
                        Toast.makeText(MainActivity.this, getString(R.string.repeated_movie), Toast.LENGTH_SHORT).show();
                    }
                }

                catch(JSONException e)
                {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, getString(R.string.json_exception), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(MainActivity.this, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonRequest);

        return null;
    }

    // Build url using the movie name given by the user
    public String buildUrl(String movieName)
    {
        String url = urlBegin;

        if(movieName.contains(" "))
        {
            String[] movieNameParts = movieName.split(" ");

            for(int i = 0; i < movieNameParts.length - 1; i++)
            {
                url += movieNameParts[i] + "+";
            }

            url += movieNameParts[movieNameParts.length - 1];
        }

        else
        {
            url += movieName;
        }

        url += urlEnd;

        return url;
    }

    // Handle movie click
    @Override
    public void onMovieItemClick(int itemIndex)
    {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(MovieData.EXTRA_MOVIE_DATA, mAdapter.getMovieData(itemIndex));
        startActivity(intent);
    }

    // Setup user preferences
    private void setupSharedPreferences()
    {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        loadAppColorFormPreferences(sharedPreferences);
        loadSortOrderFormPreferences(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if(key.equals(getString(R.string.pref_app_color_key)) || key.equals(getString(R.string.pref_sort_by_key)))
        {
            recreate();
        }
    }

    // Load color set by the user
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

    // Load sort order set by the user
    private void loadSortOrderFormPreferences(SharedPreferences sharedPreferences)
    {
        sortBy = sharedPreferences.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_label_insertion));
    }

    // Overridden to unregister Shared Preferences Listener
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
}
