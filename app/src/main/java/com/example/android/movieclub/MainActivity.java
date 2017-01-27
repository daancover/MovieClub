package com.example.android.movieclub;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieItemClickListener
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        mProgressBar = (ProgressBar) findViewById(R.id.pb_progress_bar);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MovieAdapter(MainActivity.this, this);

        mRecyclerView.setAdapter(mAdapter);
        loadMovieData();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
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

                    MoviesContract.MovieEntry.saveMovie(MainActivity.this, movieData);
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
}
