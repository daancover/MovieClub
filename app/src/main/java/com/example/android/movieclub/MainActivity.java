package com.example.android.movieclub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.movieclub.database.MoviesContract;
import com.example.android.movieclub.movie.MovieAdapter;
import com.example.android.movieclub.movie.MovieData;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener
{
    // Request url parts
    private static final String urlBegin = "http://www.omdbapi.com/?t=";
    private static final String urlEnd = "&y=&plot=short&r=json";

    // Tag for debugging purposes
    private static final String TAG = MainActivity.class.getSimpleName();

    // Constants to get results from OverlayActivity
    private static final int REQUEST_CODE_OVERLAY_ACTIVITY = 1;
    private static final String MOVIE_NAME_TAG = "movie_name";

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Load saved Settings
        setupSharedPreferences();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        // Show app logo in Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayUseLogoEnabled(true);

        // Button to add a new movie
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, OverlayActivity.class);
                startActivityForResult(intent, REQUEST_CODE_OVERLAY_ACTIVITY);
            }
        });

        // Setup movie list and user feedback
        mProgressBar = (ProgressBar) findViewById(R.id.pb_progress_bar);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MovieAdapter(MainActivity.this, this);
        mRecyclerView.setAdapter(mAdapter);

        // Load saved movies form local db
        loadMovieData();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Reload movie list
        mAdapter.setMovieData(null);
        loadMovieData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_OVERLAY_ACTIVITY)
        {
            if(resultCode == RESULT_OK)
            {
                String movieName = data.getStringExtra(MOVIE_NAME_TAG);
                Log.e(TAG, movieName);

                requestMovie(movieName);
            }
        }
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

    void loadMovieData()
    {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter.setMovieData(MoviesContract.MovieEntry.loadMovies(this, mProgressBar));
    }

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
                            response.getString(MoviesContract.MovieEntry.COLUMN_IMBD_RATING));

                    Log.d(TAG, movieData.getPoster());

                    if(MoviesContract.MovieEntry.saveMovie(MainActivity.this, movieData))
                    {
                        Toast.makeText(MainActivity.this, getString(R.string.added_movie), Toast.LENGTH_SHORT).show();
                    }

                    else
                    {
                        Toast.makeText(MainActivity.this, getString(R.string.repeated_movie), Toast.LENGTH_SHORT).show();
                    }

                    mProgressBar.setVisibility(View.INVISIBLE);
                }

                catch(JSONException e)
                {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, getString(R.string.json_exception), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                mProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonRequest);

        return null;
    }

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

    @Override
    public void onMovieItemClick(int itemIndex)
    {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(MovieData.EXTRA_MOVIE_DATA, mAdapter.getMovieData(itemIndex));
        startActivity(intent);
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
        Log.e(TAG, "onSharedPreferenceChanged <<<<<<<<<<<<<<<<<<<<<<<");

        if(key.equals(getString(R.string.pref_app_color_key)))
        {
            loadAppColorFormPreferences(sharedPreferences);
        }

        else if(key.equals(getString(R.string.pref_sort_by_key)))
        {

        }
    }

    private void loadAppColorFormPreferences(SharedPreferences sharedPreferences)
    {
        Log.e(TAG, "loadAppColorFormPreferences <<<<<<<<<<<<<<<<<<<<<<<");
        String appColorPreference = sharedPreferences.getString(getString(R.string.pref_app_color_key), getString(R.string.pref_app_color_value_dark));

        Log.e(TAG, appColorPreference);

        if(appColorPreference.equals(getString(R.string.pref_app_color_value_dark)))
        {
            setTheme(R.style.AppTheme);
            Log.e(TAG, "DARK");
        }

        else if(appColorPreference.equals(getString(R.string.pref_app_color_value_light)))
        {
            setTheme(R.style.AppThemeLight);
            Log.e(TAG, "LIGHT");

        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
}
