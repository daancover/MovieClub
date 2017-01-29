package com.example.android.movieclub;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class OverlayActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final String MOVIE_NAME_TAG = "movie_name";

    EditText mMovieName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setupSharedPreferences();

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
            loadAppColorFormPreferences(sharedPreferences);
        }
    }

    private void loadAppColorFormPreferences(SharedPreferences sharedPreferences)
    {
        String appColorPreference = sharedPreferences.getString(getString(R.string.pref_app_color_key), getString(R.string.pref_app_color_value_dark));

        if(appColorPreference.equals(getString(R.string.pref_app_color_value_dark)))
        {
            setTheme(android.R.style.Theme_Holo_Dialog);
        }

        else if(appColorPreference.equals(getString(R.string.pref_app_color_value_light)))
        {
            setTheme(android.R.style.Theme_Holo_Light_Dialog);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
}
