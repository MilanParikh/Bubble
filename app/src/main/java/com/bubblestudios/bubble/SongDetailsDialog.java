package com.bubblestudios.bubble;

import android.app.Dialog;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bubblestudios.bubble.R;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SongDetailsDialog extends DialogFragment {

    public static String TAG = "SongDetailsDialog";
    //public TextView songBlurb;
    TextView artistName;
    TextView songTitle;
    ImageView albumArt;
    Snippet snippet;

    public void attachSnippet(Snippet snippet) {

        this.snippet = snippet;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference albumArtRef = storageRef.child("AlbumArt");
        Glide.with(albumArt).load(albumArtRef.child(snippet.getAlbumArt())).into(albumArt);
        artistName.setText(snippet.getArtist());
        songTitle.setText(snippet.getTitle());
        //songBlurb.setText(snippet.getBlurb());

        View view = inflater.inflate(R.layout.song_details_dialog, container, false);
        artistName = view.findViewById(R.id.details_artist_name);
        songTitle = view.findViewById(R.id.details_song_title);
        albumArt = view.findViewById(R.id.details_album_art);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}