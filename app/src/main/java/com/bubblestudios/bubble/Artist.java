package com.bubblestudios.bubble;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Artist {

    private String artistName, artistArt;
    @ServerTimestamp
    private Date timeStamp;


public Artist(String artistName, String artistArt) {
    this.artistName = artistName;
    this.artistArt = artistArt;
}

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistArt() {
        return artistArt;
    }

    public void setArtistArt(String artistArt) {
        this.artistArt = artistArt;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }


}



