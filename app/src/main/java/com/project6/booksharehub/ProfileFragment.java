package com.project6.booksharehub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class ProfileFragment extends Fragment {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private StorageReference storageReference;
    private TextView uid, bio, name, age, rbc, dbc, gender, email, mobile, pincode, city;
    ImageView userProfileImage;

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //start progress dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Gathering Profile Information...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        // Initialize TextViews
        uid = view.findViewById(R.id.user_id_tv);
        bio = view.findViewById(R.id.user_bio_tv);
        name = view.findViewById(R.id.user_name_tv);
        age = view.findViewById(R.id.user_age_tv);
        rbc = view.findViewById(R.id.user_received_books_count_tv);
        dbc = view.findViewById(R.id.user_donated_books_count_tv);
        gender = view.findViewById(R.id.user_gender_tv);
        email = view.findViewById(R.id.user_email_tv);
        mobile = view.findViewById(R.id.user_mobile_tv);
        pincode = view.findViewById(R.id.user_pin_code_tv);
        city = view.findViewById(R.id.user_city_tv);
        userProfileImage = view.findViewById(R.id.user_profile_image);

        // Load user information
        loadUserInfo();

        // Redirect user to update profile screen
        Button updateProfileBtn = view.findViewById(R.id.btn_update_profile);
        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UpdateProfileActivity.class));
            }
        });

        // Log out user
        Button logOutBtn = view.findViewById(R.id.btn_log_out);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        return view;
    }

    private void loadUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Get user information
                            String userProfilePic = "" + snapshot.child("profileImage").getValue();
                            String UserUid = "" + snapshot.child("uid").getValue();
                            String UserBio = "" + snapshot.child("bio").getValue();
                            String UserName = "" + snapshot.child("name").getValue();
                            String UserAge = "" + snapshot.child("age").getValue();
                            String UserRbc = "" + snapshot.child("rbc").getValue();
                            String UserDbc = "" + snapshot.child("dbc").getValue();
                            String UserGender = "" + snapshot.child("gender").getValue();
                            String UserMail = "" + snapshot.child("email").getValue();
                            String UserMobile = "" + snapshot.child("mobile").getValue();
                            String UserPincode = "" + snapshot.child("pincode").getValue();
                            String UserCity = "" + snapshot.child("city").getValue();

                            // Update TextViews with user info
                            uid.setText(UserUid);
                            bio.setText(UserBio);
                            name.setText(UserName);
                            age.setText(UserAge);
                            rbc.setText(UserRbc);
                            dbc.setText(UserDbc);
                            gender.setText(UserGender);
                            email.setText(UserMail);
                            mobile.setText(UserMobile);
                            pincode.setText(UserPincode);
                            city.setText(UserCity);

                            //show profile picture
                            storageReference = FirebaseStorage.getInstance().getReference("ProfileImages").child(UserUid);
                            try {
                                File localFile = File.createTempFile("images", "jpg");

                                storageReference.getFile(localFile)
                                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                // Local temp file has been created
                                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                                userProfileImage.setImageBitmap(bitmap);
                                                progressDialog.dismiss();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Handle any errors
                                            }
                                        });
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle onCancelled
                    }
                });
    }
}
