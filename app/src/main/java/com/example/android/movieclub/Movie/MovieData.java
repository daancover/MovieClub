package com.example.android.movieclub.movie;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Daniel on 26/01/2017.
 */
public class MovieData implements Parcelable
{
    public static final String EXTRA_MOVIE_DATA = "com.example.android.movieclub.movie.MovieData";;
    private String title;
    private String actors;
    private String director;
    private String runtime;
    private String genre;
    private String poster;
    private String plot;
    private String released;
    private String metascore;
    private String imdbRating;
    private String id;

    public MovieData(String title, String actors, String director, String runtime,  String genre, String poster, String plot, String released,  String metascore, String imdbRating, String id)
    {
        this.title = title;
        this.actors = actors;
        this.director = director;
        this.runtime = runtime;
        this.genre = genre;
        this.poster = poster;
        this.plot = plot;
        this.released = released;
        this.metascore = metascore;
        this.imdbRating = imdbRating;
        this.id = id;
    }

    protected MovieData(Parcel in)
    {
        this.title = in.readString();
        this.actors = in.readString();
        this.director = in.readString();
        this.runtime = in.readString();
        this.genre = in.readString();
        this.poster = in.readString();
        this.plot = in.readString();
        this.released = in.readString();
        this.metascore = in.readString();
        this.imdbRating = in.readString();
        this.id = in.readString();
    }

    public String getTitle()
    {
        return title;
    }

    public String getActors()
    {
        return actors;
    }

    public String getDirector()
    {
        return director;
    }

    public String getRuntime()
    {
        return runtime;
    }

    public String getGenre()
    {
        return genre;
    }

    public String getPoster()
    {
        return poster;
    }

    public String getPlot()
    {
        return plot;
    }

    public String getReleased()
    {
        return released;
    }

    public String getMetascore()
    {
        return metascore;
    }

    public String getImdbRating()
    {
        return imdbRating;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public static final Creator<MovieData> CREATOR = new Creator<MovieData>()
    {
        @Override
        public MovieData createFromParcel(Parcel in)
        {
            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size)
        {
            return new MovieData[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(title);
        dest.writeString(actors);
        dest.writeString(director);
        dest.writeString(runtime);
        dest.writeString(genre);
        dest.writeString(poster);
        dest.writeString(plot);
        dest.writeString(released);
        dest.writeString(metascore);
        dest.writeString(imdbRating);
        dest.writeString(id);
    }
}
