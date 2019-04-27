package com.bubblestudios.bubble;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LikedSongHolder extends RecyclerView.ViewHolder {
    //ViewHolder extended for Liked Song properties and click listener

    public TextView artistName;
    public TextView songTitle;
    public ImageView albumArt;

    public LikedSongHolder(@NonNull View itemView) {
        super(itemView);
        artistName = itemView.findViewById(R.id.liked_artist_name);
        songTitle = itemView.findViewById(R.id.liked_song_title);
        albumArt = itemView.findViewById(R.id.liked_album_art);
    }

}
