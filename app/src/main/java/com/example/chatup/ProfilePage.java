package com.example.chatup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ProfilePage extends AppCompatActivity {
    //DECLARING THE VARIABLES
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    //FIREBASE AUTHENTICATION VARIABLES
    public FirebaseAuth mAuth;
    //TAG
    String TAG = "";
   // ImageView imageView;
    EditText user_name, user_bio;
    String userName, userBio;
    Button save_details;
    ImageView user_image_view;
    Uri pickedImgUri;
    String uid;
    //Firebase Realtime Db vars
    DatabaseReference parentReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        //Hidden status bar
        getSupportActionBar().hide();

        URL url = null;
        try {
            url = new URL("https://avatars2.githubusercontent.com/u/37215508?v=4");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch(NetworkOnMainThreadException n)
        {n.printStackTrace();}
        try {
            user_image_view.setImageBitmap(bmp); //ispe error hai..should we try catch this? I think pehle layout pe jaake dekh if imageview even exists
        }
        catch(NullPointerException ne) {
            ne.printStackTrace();

        }
        try {
            uid = getIntent().getStringExtra("uid");
        } catch (Exception e) {
            Log.d(TAG, " uid get intent" + e);
        }
        //  Log.d(TAG, "uid "  +uid) ;

        parentReference = FirebaseDatabase.getInstance().getReference("users"); //i am googling onmy end

        //INITIALISING THE VARIABLES TO THE XML IDs
        mAuth = FirebaseAuth.getInstance();
        user_image_view = findViewById(R.id.UserImage);
        user_name = findViewById(R.id.UserName);
        user_bio = findViewById(R.id.UserBio);
        save_details = findViewById(R.id.SaveDetails);

        save_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                save_details.setVisibility(View.INVISIBLE);
                //    progressBar.setVisibility(View.INVISIBLE);
                userName = user_name.getText().toString();
                userBio = user_bio.getText().toString();
                UserDetails.setUsername(userName);
                UserDetails.setBio(userBio);


                if (userName.isEmpty() || userBio.isEmpty()) {
                    ShowMessage("Please check your details");
                    save_details.setVisibility(View.VISIBLE);

                } else {
                    CreateUserAccount(userName, userBio);
                    startActivity(new Intent(ProfilePage.this, UsersAndChatsActivity.class));
                }

            }
        });


        user_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }
        });
    }


    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(this, "Please accept for required permission", Toast.LENGTH_SHORT).show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        } else
            openGallery();

    }

    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData();
            user_image_view.setImageURI(pickedImgUri);


        }
    }

    public void ShowMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void CreateUserAccount(final String name, String bio) {
//        mDatabase.createUserWithEmailAndPassword(name,bio)//kuch mila?
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // user account created successfully
//                            ShowMessage("Account created");
//                            // after we created user account we need to update his profile picture and name
//                            updateUserInfo(name,pickedImgUri,mDatabase.getCurrentUser());
//                        }
//                        else
//                        {
////                            Log.d(TAG, "onComplete: " + task.getResult().getUser());
//                            //addUserNameToUser(task.getResult().getUser());
//                            //createNewUser(task.getResult().getUser());
//
//                            // account creation failed
//                            ShowMessage("account creation failed" + task.getException().getMessage());
//                            save_details.setVisibility(View.VISIBLE);
//                        }
//                    }
//                });

//            BioModel model = new BioModel(name, bio);
        try {
            parentReference.child(userName).child("name").setValue(userName);
            parentReference.child(userName).child("bio").setValue(userBio);
            parentReference.child(userName).child("phone").setValue(UserDetails.getPhoneNum());
        } catch (NullPointerException e) {
            Log.d(TAG, " null pointer" + e);
        }


    }

    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {

        // first we need to upload user photo to firebase storage and get url

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // image uploaded succesfully
                // now we can get our image url

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        // uri contain user image url


                        UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();


                        currentUser.updateProfile(profleUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            // user info updated successfully
                                            ShowMessage("Register Complete");
                                            updateUI();
                                        }

                                    }
                                });

                    }
                });


            }
        });
    }

    private void updateUI() {

        Intent homeActivity = new Intent(getApplicationContext(), Chats.class);
        startActivity(homeActivity);
        finish();
    }


    private void addUserNameToUser(FirebaseUser user) {
        String username = "";
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                // .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
    }
}

