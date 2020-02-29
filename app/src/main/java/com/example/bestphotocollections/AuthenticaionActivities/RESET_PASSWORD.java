package com.example.bestphotocollections.AuthenticaionActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bestphotocollections.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RESET_PASSWORD extends AppCompatActivity {

    private EditText textEmail;
    private Button btnResetPassword;
    private ProgressBar Progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset__password);

        textEmail = findViewById(R.id.editText3);
        btnResetPassword = findViewById(R.id.button);
        Progressbar = findViewById(R.id.progressBar2);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = textEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    textEmail.setError("Email is Required.");
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    textEmail.setError("Please Enter The Valid Email.");
                    return;
                }

                ResetetPasswordEmailSend(email);
            }
        });
    }

    private void ResetetPasswordEmailSend(String email){
        Progressbar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Progressbar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            Toast.makeText(RESET_PASSWORD.this,"Check Your Email",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(RESET_PASSWORD.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
