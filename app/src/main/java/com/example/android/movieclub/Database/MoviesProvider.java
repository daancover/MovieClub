package com.example.android.movieclub.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Daniel on 26/01/2017.
 */

public class MoviesProvider extends ContentProvider
{
    public static final int CODE_MOVIES = 100;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesHelper mOpenHelper;

    @Override
    public boolean onCreate()
    {
        mOpenHelper = new MoviesHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        Cursor cursor;

        switch (sUriMatcher.match(uri))
        {
            case CODE_MOVIES:
            {
                cursor = mOpenHelper.getReadableDatabase().query(MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri))
        {
            case CODE_MOVIES:
                db.beginTransaction();
                int rowsInserted = 0;
                try
                {
                    for (ContentValues value : values)
                    {
                        long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, value);

                        if (_id != -1)
                        {
                            rowsInserted++;
                        }
                    }

                    db.setTransactionSuccessful();
                }

                finally
                {
                    db.endTransaction();
                }

                if (rowsInserted > 0)
                {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri)
    {
        throw new RuntimeException("Did not implement this");
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        throw new RuntimeException("Did not implement this. Used bulkInsert instead");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        int numRowsDeleted;

        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri))
        {
            case CODE_MOVIES:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsDeleted != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        throw new RuntimeException("Did not implement this");
    }

    public static UriMatcher buildUriMatcher()
    {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_MOVIES, CODE_MOVIES);

        return matcher;
    }
}
