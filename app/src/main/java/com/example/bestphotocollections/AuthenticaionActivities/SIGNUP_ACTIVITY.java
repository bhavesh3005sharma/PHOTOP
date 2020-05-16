package com.example.bestphotocollections.AuthenticaionActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bestphotocollections.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SIGNUP_ACTIVITY extends AppCompatActivity {

    private TextView textViewLogin;
    private EditText textEmail,textPassword;
    private Button btnSigup;
    private FirebaseAuth mAuth;
    private ProgressBar Progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup__activity);

        mAuth = FirebaseAuth.getInstance();

        Progressbar= findViewById(R.id.progressBar);
        textViewLogin = findViewById(R.id.textView2);
        textEmail = findViewById(R.id.editText);
        textPassword = findViewById(R.id.editText2);
        btnSigup = findViewById(R.id.button2);

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LOGIN_ACTIVITY.class);
                startActivity(intent);
            }
        });

        btnSigup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = textEmail.getText().toString().trim();
                String password = textPassword.getText().toString().trim();
                if (email.isEmpty()) {
                    textEmail.setError("Email is Required.");
                    textEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    textEmail.setError("Please Enter The Valid Email.");
                    textEmail.requestFocus();
                    return;
                }

                if (password.isEmpty() || password.length() < 6) {
                    textPassword.setError("At least 6 Character Password is Required.");
                    textPassword.requestFocus();
                    return;
                }

                registerUser(email, password);
            }
        });
    }

    private void registerUser(String email, String password){
        Progressbar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener( new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> it) {
                                    Progressbar.setVisibility(View.GONE);
                                    if (it.isSuccessful()) {
                                        Toast.makeText(SIGNUP_ACTIVITY.this , "Registered Successfully Verification Email Sent Please Verify First", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                    } else {
                                        Toast.makeText(SIGNUP_ACTIVITY.this , it.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Progressbar.setVisibility(View.GONE);
                            Toast.makeText(SIGNUP_ACTIVITY.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
