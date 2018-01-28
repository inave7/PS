package com.bellaku.naveenprakash.musicmaterial;

/**
 * Created by naveenprakash on 26/01/18.
 */

public class Song {
    private long id;
    private String title, path, artist;


    public Song(long songID, String songTitle, String songPath, String songArtist) {
        id=songID;
        title=songTitle;
        path=songPath;
        artist=songArtist;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
}
