package com.bubblestudios.bubble;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private FirestoreRecyclerAdapter adapter;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    public static UserProfileFragment newInstance() {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference albumArtRef = storageRef.child("AlbumArt");

        Query query = FirebaseFirestore.getInstance().collection("users").document(user.getUid()).collection("liked").orderBy("timeStamp");
        FirestoreRecyclerOptions<Snippet> options = new FirestoreRecyclerOptions.Builder<Snippet>().setQuery(query, Snippet.class).build();
        adapter = new FirestoreRecyclerAdapter<Snippet, LikedSongHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull LikedSongHolder holder, int position, @NonNull Snippet snippet) {
                holder.songTitle.setText(snippet.getTitle());
                holder.artistName.setText(snippet.getArtist());
                Glide.with(holder.albumArt).load(albumArtRef.child(snippet.getAlbumArt())).into(holder.albumArt);
            }

            @NonNull
            @Override
            public LikedSongHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new LikedSongHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.liked_song_layout, viewGroup, false));
            }
        };

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

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
