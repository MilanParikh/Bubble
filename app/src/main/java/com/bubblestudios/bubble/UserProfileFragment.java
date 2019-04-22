package com.bubblestudios.bubble;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserProfileFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private FirestoreRecyclerAdapter adapter;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    public static UserProfileFragment newInstance() {
        //Androidy construction method that returns the fragment to another
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Initializes and restores the state of the Fragment from its Bundle
        //This is a non-graphical initialization
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //All graphical initializations are here. Returns a View to the Activity that called it,
        //with View being the actual GUI

        //Defines the view to be returned from the corresponding layout
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        //Firebase stuff
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference albumArtRef = storageRef.child("AlbumArt");

        Query query = FirebaseFirestore.getInstance().collection("users").document(user.getUid()).collection("liked").orderBy("timeStamp");
        Query query2 = FirebaseFirestore.getInstance().collection("snippets").whereArrayContains("liked_users", user.getUid());

        //RecyclerView is a user interface that provides a scrolling list of elements based on calls
        //to database which return indefinite numbers of things. Used here to present liked songs
        FirestoreRecyclerOptions<Snippet> options = new FirestoreRecyclerOptions.Builder<Snippet>().setQuery(query2, Snippet.class).build();

        adapter = new FirestoreRecyclerAdapter<Snippet, LikedSongHolder>(options) {
            //The Adapter creates ViewHolders and Binds them to their data. Holders contain each song's data and layout
            @Override
            protected void onBindViewHolder(@NonNull LikedSongHolder holder, int position, @NonNull Snippet snippet) {
                holder.songTitle.setText(snippet.getTitle());
                holder.artistName.setText(snippet.getArtist());
                Glide.with(holder.albumArt).load(albumArtRef.child(snippet.getAlbumArt())).into(holder.albumArt);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Listens for click on each entry in recyclerView entries
                        //Opens new SongDetailsDialog for the clicked entry
                        SongDetailsDialog dialog = new SongDetailsDialog();
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        dialog.show(ft, SongDetailsDialog.TAG);
                    }
                });
            }
            @NonNull
            @Override
            public LikedSongHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                //Creates the ViewHolder object containing the right stuff
                return new LikedSongHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.liked_song_layout, viewGroup, false));
            }
        };

        //Sets a Linear layout setup for the RecyclerView. Other options include grid/tile manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        //There has to be a RecyclerView declaration on the layout/xml file. Rest of setup here
        RecyclerView recyclerView = view.findViewById(R.id.user_profile_recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
