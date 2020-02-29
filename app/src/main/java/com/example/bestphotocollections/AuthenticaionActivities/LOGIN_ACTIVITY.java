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

import com.example.bestphotocollections.MainActivity;
import com.example.bestphotocollections.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LOGIN_ACTIVITY extends AppCompatActivity {

    private TextView textViewSignup,textViewResetPassword;
    private EditText textEmail,textPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private ProgressBar Progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__activity);

        mAuth = FirebaseAuth.getInstance();

        Progressbar= findViewById(R.id.progressBar);
        textViewSignup = findViewById(R.id.textView2);
        textEmail = findViewById(R.id.editText);
        textPassword = findViewById(R.id.editText2);
        btnLogin = findViewById(R.id.button2);
        textViewResetPassword = findViewById(R.id.textView3);

        textViewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SIGNUP_ACTIVITY.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = textEmail.getText().toString().trim();
                String password = textPassword.getText().toString().trim();
                if (email.isEmpty()) {
                    textEmail.setError("Email is Required.");
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    textEmail.setError("Please Enter The Valid Email.");
                    return;
                }

                if (password.isEmpty() || password.length() < 6) {
                    textPassword.setError("At least 6 Character Password is Required.");
                    return;
                }

                loginUser(email, password);
            }
        });

        textViewResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LOGIN_ACTIVITY.this, RESET_PASSWORD.class);
                startActivity(intent);
            }
        });


    }

    private void loginUser(String email, String password) {
        Progressbar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Progressbar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    if(mAuth.getCurrentUser().isEmailVerified()){
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(LOGIN_ACTIVITY.this,"User not Verified\n  check Email",Toast.LENGTH_SHORT).show();
                    }
                }else{
                        Toast.makeText(LOGIN_ACTIVITY.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null && mAuth.getCurrentUser().isEmailVerified()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

}
