package com.bubblestudios.bubble;

import android.app.Dialog;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bubblestudios.bubble.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ArtistProfileDialog extends DialogFragment {

    public static String TAG = "SongDetailsDialog";
    TextView artistName;
    TextView artistBlurb;
    DocumentReference artistRef;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        Bundle d_snips = getArguments();
        View view = inflater.inflate(R.layout.artist_profile, container, false);
        artistBlurb = view.findViewById(R.id.description);
        artistName = view.findViewById(R.id.name);

        artistName.setText(d_snips.getString("artistName"));
        //artistBlurb.setText(d_snips.getString("artistBlurb")); // moved this to the artist object (get it below from artistRef)

        //retrieve the reference path from the bundle
        String artistRefPath = d_snips.getString("artistRefPath");
        //check if it exists
        if(artistRefPath!=null){
            //create a document reference from the path
            //you can get the artist object from here now and then the artistArt from that
            artistRef = FirebaseFirestore.getInstance().document(artistRefPath);
        }

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