package com.example.bestphotocollections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bestphotocollections.Model.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddPhoto_Activity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST =1;

    private EditText editTextTitle,editTextMetabata;
    private Button btnChoose,btnUpload;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Uri mImageUri;
    private StorageTask mUploadTask;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo_);
        setTitle("Add Photo");

        editTextTitle = findViewById(R.id.text_title);
        editTextMetabata = findViewById(R.id.text_metadata);
        imageView = findViewById(R.id.imageView);
        btnChoose = findViewById(R.id.choose);
        btnUpload = findViewById(R.id.upload);
        progressBar = findViewById(R.id.progressBar);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads/"+ FirebaseAuth.getInstance().getUid());
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads/"+ FirebaseAuth.getInstance().getUid());

        btnChoose.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
    }

    private void FileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(imageView);
            Log.d("UploadActUri",""+mImageUri);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage() {
        if(editTextTitle.getText().toString().trim().equals(""))
            Toast.makeText(AddPhoto_Activity.this, "Title is mandatory", Toast.LENGTH_SHORT).show();
        else if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 500);
                            Toast.makeText(AddPhoto_Activity.this, "Upload successful", Toast.LENGTH_LONG).show();

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    Upload upload = new Upload(editTextMetabata.getText().toString().trim(),
                                            url,editTextTitle.getText().toString().trim());
                                    String uploadId = mDatabaseRef.push().getKey();

                                    mDatabaseRef.child("item_list/"+uploadId).setValue(upload);
                                    mDatabaseRef.child("Name").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddPhoto_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });
        }
        else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(AddPhoto_Activity.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.choose:
                FileChooser();
                break;
            case R.id.upload :
                if (mUploadTask != null && mUploadTask.isInProgress())
                    Toast.makeText(AddPhoto_Activity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                else
                    uploadImage();
                break;
        }
    }
}
