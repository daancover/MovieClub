package com.example.android.movieclub.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.movieclub.movie.MovieData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 26/01/2017.
 */

public class MoviesContract
{
    public static final String CONTENT_AUTHORITY = "com.example.android.movieclub";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_TITLE = "Title";
        public static final String COLUMN_ACTORS = "Actors";
        public static final String COLUMN_DIRECTOR = "Director";
        public static final String COLUMN_RUNTIME = "Runtime";
        public static final String COLUMN_GENRE = "Genre";
        public static final String COLUMN_POSTER = "Poster";
        public static final String COLUMN_PLOT = "Plot";
        public static final String COLUMN_RELEASED = "Released";
        public static final String COLUMN_METASCORE = "Metascore";
        public static final String COLUMN_IMBD_RATING = "imdbRating";

        public static final String TYPE_TEXT = " TEXT";

        public static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID             + " INTEGER PRIMARY KEY," +
                        COLUMN_TITLE        + TYPE_TEXT + "," +
                        COLUMN_ACTORS       + TYPE_TEXT + "," +
                        COLUMN_DIRECTOR     + TYPE_TEXT + "," +
                        COLUMN_RUNTIME      + TYPE_TEXT + "," +
                        COLUMN_GENRE        + TYPE_TEXT + "," +
                        COLUMN_POSTER       + TYPE_TEXT + "," +
                        COLUMN_PLOT         + TYPE_TEXT + "," +
                        COLUMN_RELEASED     + TYPE_TEXT + "," +
                        COLUMN_METASCORE    + TYPE_TEXT + "," +
                        COLUMN_IMBD_RATING  + TYPE_TEXT + " )";

        public static final String DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public MovieEntry()
        {
        }

        // Save movie in database
        public static void saveMovie(Context context, MovieData movieData)
        {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, movieData.getTitle());
            values.put(COLUMN_ACTORS, movieData.getActors());
            values.put(COLUMN_DIRECTOR, movieData.getDirector());
            values.put(COLUMN_RUNTIME, movieData.getRuntime());
            values.put(COLUMN_GENRE, movieData.getGenre());
            values.put(COLUMN_POSTER, movieData.getPoster());
            values.put(COLUMN_PLOT, movieData.getPlot());
            values.put(COLUMN_RELEASED, movieData.getReleased());
            values.put(COLUMN_METASCORE, movieData.getMetascore());
            values.put(COLUMN_IMBD_RATING, movieData.getImdbRating());

            ContentResolver contentResolver = context.getContentResolver();
            contentResolver.bulkInsert(CONTENT_URI, new ContentValues[] {values});
        }

        // Delete specific movie from database
        public static void deleteMovie(Context context, MovieData movieData)
        {
            String selection = COLUMN_TITLE + " = ?";

            String[] selectionArgs = {movieData.getTitle()};

            ContentResolver contentResolver = context.getContentResolver();

            contentResolver.delete(CONTENT_URI, selection, selectionArgs);
        }

        // Load all movies from database
        public static List<MovieData> loadMovies(Context context, ProgressBar progressBar)
        {
            List<MovieData> returnList = new ArrayList<>();

            String[] columns = { COLUMN_TITLE, COLUMN_ACTORS, COLUMN_DIRECTOR, COLUMN_RUNTIME, COLUMN_GENRE, COLUMN_POSTER, COLUMN_PLOT, COLUMN_RELEASED, COLUMN_METASCORE, COLUMN_IMBD_RATING };

            ContentResolver contentResolver = context.getContentResolver();

            Cursor cursor = contentResolver.query(CONTENT_URI, columns, null, null, null);

            while(cursor.moveToNext())
            {
                MovieData movie = new MovieData(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_ACTORS)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DIRECTOR)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_RUNTIME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_GENRE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_POSTER)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_PLOT)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_RELEASED)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_METASCORE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_IMBD_RATING)));

                returnList.add(movie);
            }

            cursor.close();

            progressBar.setVisibility(View.INVISIBLE);

            return returnList;
        }
    }
}
