package com.bubblestudios.bubble;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Snippet {
    private String title;
    private String artist;
    private String blurb;
    private String snippet;
    private String albumArt;
    private String artistBlurb;
    private String songBlurb;
    private DocumentReference artistRef;
    private List<String> liked_users;
    private List<String> disliked_users;
    @ServerTimestamp private Date timeStamp;

    public Snippet(String title, String artist, String songBlurb, String artistBlurb, String snippet, String albumArt, DocumentReference artistRef) {
        this.title = title;
        this.artist = artist;
        this.songBlurb = songBlurb;
        this.artistBlurb = artistBlurb;
        this.snippet = snippet;
        this.albumArt = albumArt;
        this.artistRef = artistRef;
        }

    public Snippet() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtistBlurb(){
        return artistBlurb;
    }

    public void setArtistBlurb(String artistBlurb){
        this.artistBlurb = artistBlurb;
    }

    public String getSongBlurb(){
        return songBlurb;
    }

    public void setSongBlurb(String songBlurb){
        this.songBlurb = songBlurb;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<String> getLiked_users() {
        return liked_users;
    }

    public void setLiked_users(List<String> liked_users) {
        this.liked_users = liked_users;
    }

    public List<String> getDisliked_users() {
        return disliked_users;
    }

    public void setDisliked_users(List<String> disliked_users) {
        this.disliked_users = disliked_users;
    }

    public DocumentReference getArtistRef() {
        return artistRef;
    }

    public void setArtistRef(DocumentReference artistRef) {
        this.artistRef = artistRef;
    }

}
