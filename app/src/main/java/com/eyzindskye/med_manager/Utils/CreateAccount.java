package com.eyzindskye.med_manager.Utils;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


import com.eyzindskye.med_manager.MainActivity;
import com.eyzindskye.med_manager.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class CreateAccount extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{
//public class CreateAccount extends AppCompatActivity{
    private static final int RC_SIGN_IN = 101;
    GoogleSignInClient mGoogleSignInClient;
    private String TAG =  "TAG";
    private SignInButton googleBtn;
    String personName, personEmail;
    Uri personPhoto;

    private StorageReference mStorageRef;

    UserSharedPreference userPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("CreateAccount");

        mStorageRef = FirebaseStorage.getInstance().getReference();

        userPreference = new UserSharedPreference(this);
        
        googleBtn = findViewById(R.id.sign_in_button);
        googleBtn.setSize(SignInButton.SIZE_STANDARD);

        googleBtn.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            // ...
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            personEmail = account.getEmail();
            personName = account.getDisplayName();
            personPhoto = account.getPhotoUrl();

            userPreference.setName(personName);
            userPreference.setEmail(personEmail);

            uploadFile(personEmail, personPhoto);

            updateUI(account);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void uploadFile(String emailKey, Uri photo){

        StorageReference filepath = mStorageRef.child("profile_image").child(emailKey + ".jpg");

        filepath.putFile(photo)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Map update_map = new HashMap();
                        update_map.put("image", downloadUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });

//        File thumb_filePath = new File(photo.getPath());
//
//        //Compressing the image
//        Bitmap thumb_bitmap = new Compressor(this)
//                .setMaxWidth(400)
//                .setMaxHeight(350)
//                .setQuality(75)
//                .compressToBitmap(thumb_filePath);
//
//        //To store byte array into firebase
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        final byte[] thumb_byte = baos.toByteArray();
//        StorageReference filepath = mStorageRef.child("profile_image").child(emailKey
//                + ".jpg");
//        final StorageReference thumb_filepath = mStorageRef.child("profile_image")
//                .child("thumb").child(emailKey + ".jpg");
//
//        filepath.putFile(photo).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                final String download_url = taskSnapshot.getDownloadUrl().toString();
//
//                UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
//                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @RequiresApi(api = Build.VERSION_CODES.M)
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
//
//                        String thumb_downloaadUrl = thumb_task.getResult().getDownloadUrl().toString();
//                        if (thumb_task.isSuccessful()) {
//
//                            Map update_map = new HashMap();
//                            update_map.put("image", download_url);
//                            update_map.put("thumb_image", thumb_downloaadUrl);
//
//                        } else {
//                        }
//                    }
//                });
//            }
//        })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
    }

}

