package com.bubblestudios.bubble;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadArtistActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;
    private Uri artistArtFilePath;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseFirestore db;
    private Button chooseArtistArtButton, uploadArtistArtButton, submitArtistButton;
    private EditText artistNameEditText, artistBlurbEditText;
    private String artistArtFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_artist);

        Toolbar toolbar = findViewById(R.id.upload__artist_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setTitle(R.string.upload_artist);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();

        chooseArtistArtButton = findViewById(R.id.choose_artist_art_button);
        chooseArtistArtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseArtistArt();
            }
        });
        uploadArtistArtButton = findViewById(R.id.upload_artist_art_button);
        uploadArtistArtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAlbumArt();
            }
        });
        submitArtistButton = findViewById(R.id.submit_artist_button);
        artistNameEditText = findViewById(R.id.artist_upload_name_editText);
        artistBlurbEditText = findViewById(R.id.artist_blurb_editText);

        submitArtistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String artistName = artistNameEditText.getText().toString();
                String artistBlurb = artistBlurbEditText.getText().toString();
                Artist artist = new Artist(artistName, artistBlurb, artistArtFileName);

                db.collection("artists").add(artist).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        chooseArtistArtButton.setTextColor(Color.BLACK);
                        uploadArtistArtButton.setTextColor(Color.BLACK);
                        chooseArtistArtButton.setText(R.string.choose_artist_art);
                        uploadArtistArtButton.setText(R.string.upload);
                        uploadArtistArtButton.setEnabled(false);
                        artistNameEditText.setText("");
                        artistBlurbEditText.setText("");
                        Toast.makeText(getApplicationContext(), "Submitted Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null ) {
            artistArtFilePath = data.getData();
            artistArtFileName = getFileName(artistArtFilePath);
            chooseArtistArtButton.setTextColor(Color.GREEN);
            uploadArtistArtButton.setEnabled(true);
        }
    }

    private void chooseArtistArt() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadAlbumArt() {
        if(artistArtFilePath != null) {
            uploadArtistArtButton.setText(R.string.uploading);
            StorageReference ref = storageReference.child("AlbumArt/" + artistArtFileName);
            ref.putFile(artistArtFilePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadArtistArtButton.setText(R.string.upload_successful);
                    uploadArtistArtButton.setTextColor(Color.GREEN);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    uploadArtistArtButton.setText(R.string.upload_failed);
                    uploadArtistArtButton.setTextColor(Color.RED);
                }
            });
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
