package com.bubblestudios.bubble;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import static com.google.firebase.firestore.FieldValue.arrayRemove;
import static com.google.firebase.firestore.FieldValue.arrayUnion;

public class UserProfileFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private LikedSongAdapter adapter;
    private RecyclerView likedSongRecyclerView;
    private List<DocumentSnapshot> snapshotList;
    private SwipeRefreshLayout swipeRefresh;
    private FirebaseUser user;
    private FrameLayout logo;

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
        //inflate layout
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser(); //get current user from Firebase

        FirebaseStorage storage = FirebaseStorage.getInstance(); //getting instance from Firebase
        StorageReference storageRef = storage.getReference();
        final StorageReference albumArtRef = storageRef.child("AlbumArt");

        //instantiate recyclerview for liked songs list
        likedSongRecyclerView = view.findViewById(R.id.user_profile_recyclerView);
        //perform query and get data from firebase to get list of liked songs for current user
        Query query = FirebaseFirestore.getInstance().collection("snippets").whereArrayContains("liked_users", user.getUid());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    //if successful, instantiate and set adapter, and pass data to it
                    snapshotList = task.getResult().getDocuments();
                    adapter = new LikedSongAdapter(snapshotList, albumArtRef, getContext());
                    likedSongRecyclerView.setAdapter(adapter);
                    //set empty search filter, custom adapter uses filtered list, so this will essentially show an unfiltered list
                    adapter.getFilter().filter("");
                }
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        likedSongRecyclerView.setLayoutManager(layoutManager);

        //set swipe down to refresh view from UI and and set listener to use refresh data function
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });


        // ItemTouchHelper is used to implement swipe function for RecyclerView
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            //This is used to drag items on RecyclerView, as it is not needed we set it to false
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            //Swipe to delete item from liked songs list
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder target, int i) {

                int position = target.getAdapterPosition(); //get adapter position
                Snippet snippet = adapter.getItem(position); //get snippet at that position
                DocumentReference documentReference = adapter.getItemReference(position); // generate document reference to Firebase for specific snippet
                String userID = user.getUid(); //get current user

                documentReference.update("liked_users", arrayRemove(userID)); //remove the current user from the liked_users array for this snippet
                documentReference.update("disliked_users", arrayUnion(userID));//add the user to disliked_users for the song

                adapter.removeItem(position); //remove item from Recyler View
                adapter.notifyDataSetChanged();//notify the adapter of the change


            }
        });


        helper.attachToRecyclerView(likedSongRecyclerView);//attach this ItemTouchHelper to likedSongRecyclerview

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //essentially tells fragment to look at the menu created in the main activity
        setHasOptionsMenu(true);
    }

    public void refreshData() {
        //gets data from firebase again, replaces adapter data, notifies adapter of update, and disables refresh animation
        Query query = FirebaseFirestore.getInstance().collection("snippets").whereArrayContains("liked_users", user.getUid());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    snapshotList.clear();
                    List<DocumentSnapshot> tempSnapList = task.getResult().getDocuments();
                    snapshotList.addAll(tempSnapList);
                    adapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //get a handle on the search button in main activity toolbar
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        Context context = (AppCompatActivity) getActivity();
        //make search button turn into search bar
        SearchView sv = new SearchView(((AppCompatActivity) context).getSupportActionBar().getThemedContext());
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menuItem.setActionView(sv);
        //set listener for search bar input
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //when search is submitted, filter the adapter
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //as search is typed in, filter the adapter
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }



}
