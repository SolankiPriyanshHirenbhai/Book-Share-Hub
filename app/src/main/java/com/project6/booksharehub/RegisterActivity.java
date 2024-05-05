package com.project6.booksharehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    Button rsignUp;
    ImageButton rbtnBack;
    EditText redtEmail, redtPassword, redtCPassword;
    TextView rtvSignIn;
    private FirebaseAuth mAuth;
    private String password = "";
    private String cPassword = "";
    private String email = "";

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(RegisterActivity.this, MainActivity2.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        redtEmail = findViewById(R.id.redtEmail);
        redtPassword = findViewById(R.id.redtPassword);
        redtCPassword = findViewById(R.id.redtCPassword);
        mAuth = FirebaseAuth.getInstance();
        rbtnBack = findViewById(R.id.rbtnBack);
        rsignUp = findViewById(R.id.rbtnSignUp);
        rtvSignIn = findViewById(R.id.rtvSignIn);

        rbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rsignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        rtvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void validateData() {
        email = redtEmail.getText().toString();
        password = redtPassword.getText().toString();
        cPassword = redtCPassword.getText().toString();

        if (!isValidEmail(email)) {
            Toast.makeText(RegisterActivity.this, "Your email doesn't look like an email address", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(cPassword)) {
            Toast.makeText(RegisterActivity.this, "Password doesn't match! please try again.", Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            updateUserInfo();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT);
                        }
                    });
        }
    }

    private void updateUserInfo() {

        long timeStamp = System.currentTimeMillis();

        //get current user uid
        String uid = mAuth.getUid();

        //setup data to add in db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("name", "");
        hashMap.put("userType", "user");//will be changed manually by changing its valu in firebase
        hashMap.put("profileImage", "");
        hashMap.put("bio", "");
        hashMap.put("age", "");
        hashMap.put("rbc", "");
        hashMap.put("dbc", "");
        hashMap.put("gender", "");
        hashMap.put("mobile", "");
        hashMap.put("pincode", "");
        hashMap.put("city", "");
        hashMap.put("timeStamp", timeStamp);

        //set Data to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(RegisterActivity.this, "User Created Successfully", Toast.LENGTH_SHORT);
                        //direct user to home page
                        startActivity(new Intent(RegisterActivity.this, MainActivity2.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT);
                    }
                });
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

}

