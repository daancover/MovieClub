package com.example.android.movieclub.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Daniel on 26/01/2017.
 */

public class MoviesHelper extends SQLiteOpenHelper
    {
        private static final int DB_VERSION = 4;
        private static final String DB_NAME = "movies.db";

        public MoviesHelper(Context context)
        {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(MoviesContract.MovieEntry.CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL(MoviesContract.MovieEntry.DELETE_ENTRIES);
            onCreate(db);
        }
}
