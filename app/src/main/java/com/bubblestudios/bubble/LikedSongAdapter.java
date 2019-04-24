package com.bubblestudios.bubble;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class LikedSongAdapter extends RecyclerView.Adapter<LikedSongHolder> implements Filterable {

    private List<DocumentSnapshot> snapshotList;
    private List<DocumentSnapshot> filteredSnapshotList;
    private StorageReference albumArtRef;

    public LikedSongAdapter(List<DocumentSnapshot> snapshotList, StorageReference albumArtRef) {
        this.snapshotList = snapshotList;
        this.albumArtRef = albumArtRef;
    }

    @NonNull
    @Override
    public LikedSongHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new LikedSongHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.liked_song_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LikedSongHolder holder, int i) {
        Snippet snippet = filteredSnapshotList.get(i).toObject(Snippet.class);

        holder.songTitle.setText(snippet.getTitle());
        holder.artistName.setText(snippet.getArtist());
        Glide.with(holder.albumArt).load(albumArtRef.child(snippet.getAlbumArt())).into(holder.albumArt);
    }

    @Override
    public int getItemCount() {
        if(filteredSnapshotList == null) {
            return 0;
        } else {
            return filteredSnapshotList.size();
        }
    }

    public Snippet getItem(int pos) {
        return filteredSnapshotList.get(pos).toObject(Snippet.class);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchString = constraint.toString().toLowerCase();
                if(searchString.isEmpty()) {
                    filteredSnapshotList = snapshotList;
                } else {
                    List<DocumentSnapshot> tempSnapList = new ArrayList<>();
                    for(DocumentSnapshot snapshot: snapshotList) {
                        Snippet snippet = snapshot.toObject(Snippet.class);
                        String songTitle = snippet.getTitle();
                        String artistName = snippet.getArtist();
                        Log.d("filterquery", "performFiltering: " + songTitle + " " + artistName);
                        if (songTitle.toLowerCase().contains(searchString) || artistName.toLowerCase().contains(searchString)) {
                            Log.d("filterquery2", "performFiltering: " + songTitle);
                            tempSnapList.add(snapshot);
                        }
                    }
                    filteredSnapshotList = tempSnapList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredSnapshotList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredSnapshotList = (List<DocumentSnapshot>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
