package com.eyzindskye.med_manager.Utils;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eyzindskye.med_manager.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    private LinearLayout mEditPic;
    private static final int GALLERY_PICK = 1;
    private CircleImageView mProfile;
    private TextView mName, mEmail;

    GoogleSignInClient mGoogleSignInClient;
    String personName, personEmail, personPhoto;
    private StorageReference mStorageRef;

    UserSharedPreference userPreference;
    AVLoadingIndicatorView avLoadingIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userPreference = new UserSharedPreference(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        avLoadingIndicatorView = findViewById(R.id.avi);

        mEditPic = findViewById(R.id.editPic);
        mEmail = findViewById(R.id.email);
        mName = findViewById(R.id.name);
        mProfile = findViewById(R.id.circleImage);
        mEditPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Profile Picture"), GALLERY_PICK);
            }
        });

//        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
//        if (acct != null) {
//            if (acct.getPhotoUrl() != null){
//                personPhoto = acct.getPhotoUrl().toString();
//                Glide.with(this).load(personPhoto).into(mProfile);
//            }
//        }
        if (userPreference != null){
            personName = userPreference.getName();
            personEmail = userPreference.getEmail();

            mName.setText(personName);
            mEmail.setText(personEmail);

            mStorageRef.child("profile_image").child(personEmail + ".jpg")
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    final String imageUri = uri.toString();
                    Picasso.with(getApplicationContext()).load(imageUri).placeholder(R.drawable.user_avatar1)
                            .into(mProfile);
                }
            });


            File localFile = null;
            try {
                localFile = File.createTempFile("images", "jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            mStorageRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            // ...
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    // ...
                }
            });
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit_name){
            startActivity(new Intent(getApplicationContext(), EditName.class));
        }else if (id == R.id.edit_email){
            startActivity(new Intent(getApplicationContext(), EditEmail.class));
        }else if (id == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(1, 1).start(Profile.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                avLoadingIndicatorView.setVisibility(View.VISIBLE);
                avLoadingIndicatorView.show();

                StyleableToast.makeText(this, "Uploading image, please wait...", R.style.success).show();



                Uri resultUri = result.getUri();
                StorageReference filepath = mStorageRef.child("profile_image").child(personEmail + ".jpg");

                filepath.putFile(resultUri)
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

//                Uri resultUri = result.getUri();
//                //Getting the file
//                File thumb_filePath = new File(resultUri.getPath());
////                String currentUserID = mCurrentUser.getUid();
//
//                //Compressing the image
//                Bitmap thumb_bitmap = new Compressor(this)
//                        .setMaxWidth(400)
//                        .setMaxHeight(350)
//                        .setQuality(75)
//                        .compressToBitmap(thumb_filePath);
//
//                //To store byte array into firebase
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                final byte[] thumb_byte = baos.toByteArray();
//
//                StorageReference filepath = mStorageRef.child("profile_image").child(personEmail
//                        + ".jpg");
//                final StorageReference thumb_filepath = mStorageRef.child("profile_image")
//                        .child("thumb").child(personEmail + ".jpg");
//
//                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        //The task gets the values that are provided in firebase
//
//                        //we get the download url and store it the the database
//                        final String download_url = taskSnapshot.getDownloadUrl().toString();
//
//                        UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
//                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                            @RequiresApi(api = Build.VERSION_CODES.M)
//                            @Override
//                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
//
//                                String thumb_downloaadUrl = thumb_task.getResult().getDownloadUrl().toString();
//                                if (thumb_task.isSuccessful()) {
//
////                                        Map<String, Object> update_map = new HashMap();
//                                    Map update_map = new HashMap();
//                                    update_map.put("image", download_url);
//                                    update_map.put("thumb_image", thumb_downloaadUrl);
//                                    StyleableToast.makeText(getApplicationContext(), "Image uplaoded", R.style.success).show();
//
//                                } else {
//                                    // dialog.dismiss();
//                                    StyleableToast.makeText(getApplicationContext(), "Error occurred", R.style.error).show();
//
//                                }
//                            }
//                        });
//                    }
//                })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                StyleableToast.makeText(getApplicationContext(), e.getMessage(), R.style.error).show();
//
//                            }
//                        });

                avLoadingIndicatorView.hide();
                avLoadingIndicatorView.setVisibility(View.INVISIBLE);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
