package com.bubblestudios.bubble;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CardsFragment extends Fragment implements CardStackListener {

    private OnFragmentInteractionListener mListener;
    //private CardStackAdapter adapter;
    private CardStackAdapter2 adapter;
    private CardStackLayoutManager layoutManager;
    private SimpleExoPlayer exoPlayer;
    private DataSource.Factory dataSourceFactory;
    private PlayerView playerView;
    private CardStackView cardStackView;
    private DocumentReference snippetRef;
    private Snippet currentSnippet;
    private String userID;
    private FirebaseFirestore db;
    private int lastPosition = 0;

    public CardsFragment() {
    }

    public static CardsFragment newInstance() {
        CardsFragment fragment = new CardsFragment();
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
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_cards, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        db = FirebaseFirestore.getInstance();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference albumArtRef = storageRef.child("AlbumArt");
        final StorageReference snippetRef = storageRef.child("Snippets");

        playerView = view.findViewById(R.id.player_view);
        exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext());
        dataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), "Bubble"));
        playerView.setPlayer(exoPlayer);

        cardStackView = view.findViewById(R.id.card_view);

        Query query = FirebaseFirestore.getInstance().collection("snippets").orderBy("timeStamp");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<DocumentSnapshot> snapshotList = task.getResult().getDocuments();

                    adapter = new CardStackAdapter2(snapshotList, albumArtRef, snippetRef, exoPlayer, dataSourceFactory, CardsFragment.this);
                    cardStackView.setAdapter(adapter);
                    adapter.getFilter().filter(userID);
                }
            }
        });
        layoutManager = new CardStackLayoutManager(getContext(), this);
        cardStackView.setLayoutManager(layoutManager);

        final Button pausePlayButton = view.findViewById(R.id.pause_play_button);
        pausePlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exoPlayer.setPlayWhenReady(!exoPlayer.getPlayWhenReady());
            }
        });

        final SwipeAnimationSetting likeSetting = new SwipeAnimationSetting.Builder().setDirection(Direction.Right).setDuration(Duration.Normal.duration).build();
        final SwipeAnimationSetting dislikeSetting = new SwipeAnimationSetting.Builder().setDirection(Direction.Left).setDuration(Duration.Normal.duration).build();

        Button likeButton = view.findViewById(R.id.like_button);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutManager.setSwipeAnimationSetting(likeSetting);
                cardStackView.swipe();
            }
        });
        Button dislikeButton = view.findViewById(R.id.dislike_button);
        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutManager.setSwipeAnimationSetting(dislikeSetting);
                cardStackView.swipe();
            }
        });

        //adapter.startListening();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        lastPosition = layoutManager.getTopPosition();
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

    @Override
    public void onStart() {
        super.onStart();
        //adapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(lastPosition != 0){
            cardStackView.scrollToPosition(lastPosition);
        }
    }

    public void firstPlay() {
        CardViewHolder viewHolder = (CardViewHolder) cardStackView.findViewHolderForAdapterPosition(layoutManager.getTopPosition());
        exoPlayer.prepare(viewHolder.audioSource);
        playerView.setPlayer(exoPlayer);
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        //adapter.stopListening();
        exoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //adapter.stopListening();
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {
        if(direction == Direction.Right) {
            db.collection("snippets").document(snippetRef.getId()).update("liked_users", FieldValue.arrayUnion(userID));
        } else if(direction == Direction.Left) {
            db.collection("snippets").document(snippetRef.getId()).update("disliked_users", FieldValue.arrayUnion(userID));
        }
    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {
        CardViewHolder viewHolder = (CardViewHolder) cardStackView.findViewHolderForAdapterPosition(position);
        if(position != 0) {
            exoPlayer.prepare(viewHolder.audioSource);
            playerView.setPlayer(exoPlayer);
            exoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void onCardDisappeared(View view, int position) {
        exoPlayer.setPlayWhenReady(false);
        CardViewHolder viewHolder = (CardViewHolder) cardStackView.findViewHolderForAdapterPosition(position);
        snippetRef = viewHolder.snippetRef;
        currentSnippet = adapter.getItem(position);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}