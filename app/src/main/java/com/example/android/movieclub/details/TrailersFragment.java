package com.example.android.movieclub.details;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.movieclub.R;
import com.example.android.movieclub.database.MoviesContract;
import com.example.android.movieclub.movie.MovieData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Daniel on 31/01/2017.
 */

public class TrailersFragment extends Fragment implements TrailersAdapter.TrailerItemClickListener
{
    public static final String ARG_MOVIE = "movie";
    private static final String TRAILERS_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String MOVIES_BASE_URL = "https://api.themoviedb.org/3/search/movie?api_key=";
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    private static final String API_KEY = "your_api_key";
    private static final String QUERY = "&query=";
    private static final String SPACE = "%20";
    private static final String KEY = "key";
    private static final String NAME = "name";

    private MovieData mMovieData;
    private List<String> mUrls;
    private List<String> mNames;
    private TrailersAdapter mAdapter;

    @BindView(R.id.rv_trailers) RecyclerView mRecyclerView;

    public static Fragment newInstance(MovieData movieData)
    {
        TrailersFragment detailsFragment = new TrailersFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, movieData);
        detailsFragment.setArguments(args);

        return detailsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.trailers_fragment, container, false);
        Bundle args = getArguments();

        if(args.containsKey(ARG_MOVIE))
        {
            ButterKnife.bind(this, rootView);

            // Get movie data from previous activity
            mMovieData = args.getParcelable(ARG_MOVIE);
            mUrls = new ArrayList<>();
            mNames = new ArrayList<>();

            mAdapter = new TrailersAdapter(getContext(), this);
            LinearLayoutManager trailerLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(trailerLinearLayoutManager);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setAdapter(mAdapter);

            requestMovieId();
        }

        return rootView;
    }

    private void requestMovieId()
    {
        if(mMovieData.getId().isEmpty())
        {
            final RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            String url = buildUrl();

            if(url != null)
            {
                final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            // Split the movie attributes in an array
                            JSONArray jsonArray = response.getJSONArray("results");
                            boolean found = false;

                            for(int i = 0; i < jsonArray.length() && !found; i++)
                            {
                                JSONObject object = jsonArray.getJSONObject(i);

                                if(object.getString("original_title").equals(mMovieData.getTitle()) || object.getString("title").equals(mMovieData.getTitle()))
                                {
                                    mMovieData.setId(object.getString(MoviesContract.MovieEntry.COLUMN_ID));
                                    found = true;
                                }
                            }
                        }

                        catch(JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(getContext(), getString(R.string.no_trailers), Toast.LENGTH_SHORT).show();
                        }

                        requestMovieTrailers(requestQueue);
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(getContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                    }
                });

                requestQueue.add(jsonRequest);
            }

            else
            {
                Toast.makeText(getContext(), getString(R.string.no_trailers), Toast.LENGTH_SHORT).show();
            }
        }

        else
        {
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestMovieTrailers(requestQueue);
        }
    }

    private void requestMovieTrailers(RequestQueue requestQueue)
    {
        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, buildTrailerUrl(), new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                try
                {
                    // Split the movie attributes in an array
                    JSONArray jsonArray = response.getJSONArray("results");

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject object = jsonArray.getJSONObject(i);

                        // Store all movie data
                        mUrls.add(object.getString(KEY));
                        mNames.add(object.getString(NAME));
                    }

                    mAdapter.setTrailerData(mUrls, mNames);
                }

                catch(JSONException e)
                {
                    e.printStackTrace();
                    Toast.makeText(getContext(), getString(R.string.no_trailers), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonRequest);
    }

    private String parseTitle()
    {
        if(mMovieData.getTitle().contains(" "))
        {
            String[] titleParts = mMovieData.getTitle().replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
            String title = "";

            for(int i = 0; i < titleParts.length; i++)
            {
                title += (titleParts[i] + SPACE);
            }

            return title.substring(0, title.length() - 3);
        }

        else
        {
            return mMovieData.getTitle();
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

    public String buildUrl()
    {
        if(getDate(mMovieData) != null)
        {
            Uri builtUri = Uri.parse(MOVIES_BASE_URL + API_KEY + QUERY + parseTitle()).buildUpon().build();

            URL url = null;

            try
            {
                url = new URL(builtUri.toString());
            }

            catch(MalformedURLException e)
            {
                e.printStackTrace();
            }

            return url.toString();
        }

        return null;
    }

    public String buildTrailerUrl()
    {
        Uri builtUri = Uri.parse(TRAILERS_BASE_URL + mMovieData.getId() + "/videos?api_key=" + API_KEY).buildUpon().build();

        URL url = null;

        try
        {
            url = new URL(builtUri.toString());
        }

        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        return url.toString();
    }

    @Override
    public void onTrailerItemClick(int itemIndex)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_BASE_URL + mUrls.get(itemIndex)));

        if (intent.resolveActivity(getContext().getPackageManager()) != null)
        {
            startActivity(intent);
        }
    }
}
