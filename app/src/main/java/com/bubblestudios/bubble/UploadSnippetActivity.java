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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class UploadSnippetActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;
    private int PICK_AUDIO_REQUEST = 2;
    private Uri albumArtFilePath;
    private Uri snippetFilePath;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseFirestore db;
    private Button uploadAlbumArtButton, uploadSnippetButton, chooseAlbumArtButton, chooseSnippetButton;
    private EditText songTitleEditText;
    private String albumArtFileName, snippetFileName, songBlurb="test", artistBlurb = "test", artistName;
    private Spinner artistSpinner;
    private ArrayAdapter<String> spinnerAdapter;
    private List<String> artistNames, liked_users = new ArrayList<>(), disliked_users = new ArrayList<>();
    private List<DocumentReference> artistRefs;
    private DocumentReference artistRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_snippet);

        Toolbar toolbar = findViewById(R.id.upload_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setTitle(R.string.upload_snippet);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();

        songTitleEditText = findViewById(R.id.song_title_editText);
        chooseAlbumArtButton = findViewById(R.id.choose_album_art_button);
        chooseAlbumArtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseAlbumArt();
            }
        });
        uploadAlbumArtButton = findViewById(R.id.upload_album_art_button);
        uploadAlbumArtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAlbumArt();
            }
        });

        chooseSnippetButton = findViewById(R.id.choose_snippet_button);
        chooseSnippetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseSnippet();
            }
        });
        uploadSnippetButton = findViewById(R.id.upload_snippet_button);
        uploadSnippetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadSnippet();
            }
        });
        final Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String songTitle = songTitleEditText.getText().toString();
                Snippet snippet = new Snippet(songTitle, artistName, songBlurb , snippetFileName, albumArtFileName, liked_users, disliked_users, artistRef);

                db.collection("snippets").add(snippet).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        chooseAlbumArtButton.setTextColor(Color.BLACK);
                        chooseSnippetButton.setTextColor(Color.BLACK);
                        uploadAlbumArtButton.setTextColor(Color.BLACK);
                        uploadSnippetButton.setTextColor(Color.BLACK);
                        chooseAlbumArtButton.setText(R.string.choose_album_art);
                        chooseSnippetButton.setText(R.string.choose_snippet);
                        uploadAlbumArtButton.setText(R.string.upload);
                        uploadSnippetButton.setText(R.string.upload);
                        uploadAlbumArtButton.setEnabled(false);
                        uploadSnippetButton.setEnabled(false);
                        songTitleEditText.setText("");
                        Toast.makeText(getApplicationContext(), "Submitted Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        artistSpinner = findViewById(R.id.artist_spinner);
        CollectionReference artistsRef = db.collection("artists");
        artistNames = new ArrayList<>();
        artistRefs = new ArrayList<>();
        spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, artistNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        artistSpinner.setAdapter(spinnerAdapter);
        artistsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    List<DocumentSnapshot> snapshots = task.getResult().getDocuments();
                    for(DocumentSnapshot snapshot: snapshots) {
                        Artist artist = snapshot.toObject(Artist.class);
                        artistNames.add(artist.getArtistName());
                        artistRefs.add(snapshot.getReference());
                    }
                    spinnerAdapter.notifyDataSetChanged();
                    artistName = artistNames.get(0);
                    artistRef = artistRefs.get(0);
                }
            }
        });
        artistSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                artistRef = artistRefs.get(i);
                artistName = artistNames.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null ) {
            albumArtFilePath = data.getData();
            albumArtFileName = getFileName(albumArtFilePath);
            chooseAlbumArtButton.setTextColor(Color.GREEN);
            uploadAlbumArtButton.setEnabled(true);
        } else if (requestCode == PICK_AUDIO_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            snippetFilePath = data.getData();
            snippetFileName = getFileName(snippetFilePath);
            chooseSnippetButton.setTextColor(Color.GREEN);
            uploadSnippetButton.setEnabled(true);
        }
    }

    private void chooseAlbumArt() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void chooseSnippet() {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Song Snippet"), PICK_AUDIO_REQUEST);
    }

    private void uploadAlbumArt() {
        if(albumArtFilePath != null) {
            uploadAlbumArtButton.setText(R.string.uploading);
            StorageReference ref = storageReference.child("AlbumArt/" + albumArtFileName);
            ref.putFile(albumArtFilePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadAlbumArtButton.setText(R.string.upload_successful);
                    uploadAlbumArtButton.setTextColor(Color.GREEN);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    uploadAlbumArtButton.setText(R.string.upload_failed);
                    uploadAlbumArtButton.setTextColor(Color.RED);
                }
            });
        }
    }

    private void uploadSnippet() {
        if(snippetFilePath != null) {
            uploadSnippetButton.setText(R.string.uploading);
            StorageReference ref = storageReference.child("Snippets/" + snippetFileName);
            ref.putFile(snippetFilePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadSnippetButton.setText(R.string.upload_successful);
                    uploadSnippetButton.setTextColor(Color.GREEN);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    uploadSnippetButton.setText(R.string.upload_failed);
                    uploadSnippetButton.setTextColor(Color.RED);
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
