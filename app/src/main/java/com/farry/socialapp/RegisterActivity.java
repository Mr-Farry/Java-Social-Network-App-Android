package com.farry.socialapp;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText password,email;
    Button register,signUpButton;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        progressDialog=new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Registering User ..");
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Register New User ..");

        // initializing the mAuth inctance

        mAuth = FirebaseAuth.getInstance();
        register=(Button)findViewById(R.id.register_button);
        password=(EditText)findViewById(R.id.password_text);
        email=(EditText)findViewById(R.id.email_text);
        password.setVisibility(View.INVISIBLE);
        email.setVisibility(View.INVISIBLE);
        signUpButton=(Button)findViewById(R.id.sign_up_button);


        register.setVisibility(View.INVISIBLE);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String Email=email.getText().toString().trim();
                String Password=password.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches())
                {
                    // if email is typed incorrectly ..
                    email.setError("Email is incorrect");
                    email.setFocusable(true);
                }

                else if(Password.length()<8)
                {

                    email.setError("Password is too Short");
                    email.setFocusable(true);
                }

                else {
                    RegisterUser(Email,Password);
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.setVisibility(View.VISIBLE);
                password.setVisibility(View.VISIBLE);
                register.setVisibility(View.VISIBLE);
                signUpButton.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void RegisterUser(String email,String password) {
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            //  Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            String email=user.getEmail();
                            String uid=user.getUid();
                            HashMap<Object,String> hashMap=new HashMap<>();
                            hashMap.put("email",email);
                            hashMap.put("uid",uid);
                            hashMap.put("name","");
                            hashMap.put("phone","");
                            hashMap.put("profile","");
                            hashMap.put("cover","");
                            FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference=firebaseDatabase.getReference("AppUsers");
                            databaseReference.child(uid).setValue(hashMap);

                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();


            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(RegisterActivity.this, "Registered ..", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }



}
