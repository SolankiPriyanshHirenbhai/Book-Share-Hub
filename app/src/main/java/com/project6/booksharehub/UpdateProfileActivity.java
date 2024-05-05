package com.project6.booksharehub;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class UpdateProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    ImageButton userProfileBtn;
    private Uri imageUri = null;
    private ProgressDialog progressDialog;;
    EditText userBio, userName, userAge, userGender, userEmail, userMobile, userPincode, userCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        //get instances
        userBio = findViewById(R.id.user_bio_et);
        userName = findViewById(R.id.user_name_et);
        userAge = findViewById(R.id.user_age_et);
        userGender = findViewById(R.id.user_gender_et);
        userEmail = findViewById(R.id.user_email_et);
        userMobile = findViewById(R.id.user_mobile_et);
        userPincode = findViewById(R.id.user_pin_code_et);
        userCity = findViewById(R.id.user_city_et);
        Button saveButton = findViewById(R.id.save_button);
        userProfileBtn = findViewById(R.id.user_profile_btn);

        firebaseAuth = FirebaseAuth.getInstance();

        // Set click listeners
        userProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageAttachMenu();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private void validateData() {
        //gate data
        String bio = userBio.getText().toString().trim();
        String name = userName.getText().toString().trim();
        String age = userAge.getText().toString().trim();
        String gender = userGender.getText().toString().trim();
        String email = userEmail.getText().toString().trim();
        String mobile = userMobile.getText().toString().trim();
        String pincode = userPincode.getText().toString().trim();
        String city = userCity.getText().toString().trim();

        //validate data
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Enter Name...", Toast.LENGTH_SHORT).show();
        } else{
            if(imageUri == null){
                updateProfile("");
            }else {
                uploadImage();
            }
        }

    }

    private void updateProfile(String imageUri){
        progressDialog.setMessage("Updating User Profile...");
        progressDialog.show();

        //gate data
        String bio = userBio.getText().toString();
        String name = userName.getText().toString();
        String age = userAge.getText().toString();
        String email = userEmail.getText().toString();
        String gender = userGender.getText().toString();
        String mobile = userMobile.getText().toString();
        String pincode = userPincode.getText().toString();
        String city = userCity.getText().toString();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("email", email);
        hashMap.put("name", name);
        if(imageUri != null) {
            hashMap.put("profileImage", imageUri);
        }
        hashMap.put("bio", bio);
        hashMap.put("age", age);
        hashMap.put("gender", gender);
        hashMap.put("mobile", mobile);
        hashMap.put("pincode", pincode);
        hashMap.put("city", city);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(firebaseAuth.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(UpdateProfileActivity.this, "Profile Updated Successfully.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UpdateProfileActivity.this, HomeFragment.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateProfileActivity.this, "Failed to update in db: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadImage(){
        progressDialog.setMessage("Updating Profile Image...");
        progressDialog.show();

        String filePathAndName = "ProfileImages/"+firebaseAuth.getUid();
        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName);
        reference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        String uploadedImageUri = ""+uriTask.getResult();
                        updateProfile(uploadedImageUri);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(UpdateProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void showImageAttachMenu() {
        //setup popup menu
        PopupMenu popupMenu = new PopupMenu(this, userProfileBtn);
        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Camera");
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Gallery");

        popupMenu.show();

        //handle menu item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int which = item.getItemId();
                if(which==0){
                    //camera clicked
                    pickImageCamera();
                } else if(which == 1){
                    //gallery cicked
                    pickImageGallery();
                }

                return false;
            }
        });
    }

    private void pickImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private void pickImageCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent((MediaStore.ACTION_IMAGE_CAPTURE));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        userProfileBtn.setImageURI(imageUri);
                    }

                }
            }
    );

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        imageUri = data.getData();
                        userProfileBtn.setImageURI(imageUri);
                    }
                }
            }
    );
}