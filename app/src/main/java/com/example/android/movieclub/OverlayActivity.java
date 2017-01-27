package com.example.android.movieclub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class OverlayActivity extends Activity
{
    private static final String MOVIE_NAME_TAG = "movie_name";

    EditText mMovieName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlay);

        mMovieName = (EditText) findViewById(R.id.et_movie_title);
    }

    public void onSearchClicked(View view)
    {
        String movieName = mMovieName.getText().toString();

        if(movieName.isEmpty())
        {
            Toast.makeText(this, getString(R.string.movie_name_empty), Toast.LENGTH_SHORT).show();
        }

        else
        {
            Intent intent = new Intent();
            intent.putExtra(MOVIE_NAME_TAG, movieName);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void onCancelClicked(View view)
    {
        onBackPressed();
    }
}
