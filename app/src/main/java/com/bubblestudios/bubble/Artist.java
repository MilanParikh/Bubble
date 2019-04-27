package com.bubblestudios.bubble;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Artist {

    private String artistName;
    private String artistArt;
    private String artistBlurb;
    @ServerTimestamp
    private Date timeStamp;


    public Artist() {
    }

    public Artist(String artistName, String artistBlurb, String artistArt) {
    this.artistName = artistName;
    this.artistBlurb = artistBlurb;
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

    public String getArtistBlurb() {
        return artistBlurb;
    }

    public void setArtistBlurb(String artistBlurb) {
        this.artistBlurb = artistBlurb;
    }

}



