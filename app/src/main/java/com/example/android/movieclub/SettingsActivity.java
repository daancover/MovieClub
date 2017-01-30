package com.example.android.movieclub;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    // Tag for debugging purposes
    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Load saved Settings
        setupSharedPreferences();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null)
        {
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        }
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
}