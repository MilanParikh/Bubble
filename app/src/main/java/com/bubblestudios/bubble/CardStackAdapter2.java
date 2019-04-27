package com.bubblestudios.bubble;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CardStackAdapter2 extends RecyclerView.Adapter<CardViewHolder> implements Filterable {

    private List<DocumentSnapshot> snapshotList;
    private List<DocumentSnapshot> filteredSnapshotList;
    private StorageReference albumArtRef;
    private StorageReference snippetRef;
    private StorageReference artistArtRef;
    private SimpleExoPlayer exoPlayer;
    private DataSource.Factory dataSourceFactory;
    private CardsFragment cardsFragment;
    private int firstLoad = 0;

    public CardStackAdapter2(List<DocumentSnapshot> snapshotList, StorageReference albumArtRef, StorageReference snippetRef, SimpleExoPlayer exoPlayer, DataSource.Factory dataSourceFactory, CardsFragment cardsFragment) {
        this.snapshotList = snapshotList;
        this.albumArtRef = albumArtRef;
        this.snippetRef = snippetRef;
        this.exoPlayer = exoPlayer;
        this.dataSourceFactory = dataSourceFactory;
        this.cardsFragment = cardsFragment;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CardViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final CardViewHolder holder, int i) {
        Snippet snippet = filteredSnapshotList.get(i).toObject(Snippet.class);

        holder.artistName.setText(snippet.getArtist());
        holder.songTitle.setText(snippet.getTitle());
        Glide.with(holder.albumArt).load(albumArtRef.child(snippet.getAlbumArt())).into(holder.albumArt);
        holder.snippetRef = filteredSnapshotList.get(i).getReference();
        snippetRef.child(snippet.getSnippet()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                MediaSource audioSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
                holder.audioSource = audioSource;

                if((firstLoad == 0) && (holder.getAdapterPosition() == 0)) {
                    cardsFragment.firstPlay();
                    firstLoad = 1;
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exoPlayer.setPlayWhenReady(!exoPlayer.getPlayWhenReady());
            }
        });
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
                String userID = constraint.toString();
                if(userID.isEmpty()) {
                    filteredSnapshotList = snapshotList;
                } else {
                    List<DocumentSnapshot> tempSnapList = new ArrayList<>();
                    for(DocumentSnapshot snapshot: snapshotList) {
                        List<String> likedUsersList = snapshot.toObject(Snippet.class).getLiked_users();
                        List<String> dislikedUsersList = snapshot.toObject(Snippet.class).getDisliked_users();
                        if(!likedUsersList.contains(userID) && !dislikedUsersList.contains(userID)){
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
