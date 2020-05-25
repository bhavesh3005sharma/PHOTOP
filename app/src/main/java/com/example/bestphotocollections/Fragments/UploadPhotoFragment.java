package com.example.bestphotocollections.Fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bestphotocollections.Model.Upload;
import com.example.bestphotocollections.R;
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

public class UploadPhotoFragment extends Fragment implements View.OnClickListener{
    private static final int PICK_IMAGE_REQUEST =1;

    private EditText editTextTitle,editTextMetabata;
    private Button btnChoose,btnUpload;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Uri mImageUri;
    private StorageTask mUploadTask;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_photo_,container,false);
        getActivity().setTitle("Add Photo");

        editTextTitle = view.findViewById(R.id.text_title);
        editTextMetabata = view.findViewById(R.id.text_metadata);
        imageView = view.findViewById(R.id.imageView);
        btnChoose = view.findViewById(R.id.choose);
        btnUpload = view.findViewById(R.id.upload);
        progressBar = view.findViewById(R.id.progressBar);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads/"+ FirebaseAuth.getInstance().getUid());
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads/"+ FirebaseAuth.getInstance().getUid());

        btnChoose.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        return view;
    }

//    private void saveUploadTasks(StorageTask mUploadTask) {
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sihsac",getActivity().MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        Gson gson = new Gson();
//        //String json = gson.tojson(mUploadTask,StorageTask);
//        //editor.putString("list", json);
//        editor.apply();
//    }
//
//    private  void loadUploadTask(){
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sihsac", getActivity().MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = sharedPreferences.getString("list", null);
//        Type type = new TypeToken<StorageTask>() {}.getType();
//        mUploadTask = gson.fromJson(json, type);
//    }

    private void FileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(imageView);
            Log.d("UploadActUri",""+mImageUri);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage() {
        if(editTextTitle.getText().toString().trim().equals(""))
            Toast.makeText(getContext(), "Title is mandatory", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    Upload upload = new Upload(editTextMetabata.getText().toString().trim(),
                                            url,editTextTitle.getText().toString().trim());
                                    String uploadId = mDatabaseRef.push().getKey();

                                    mDatabaseRef.child("item_list/"+uploadId).setValue(upload);
                                    if (FirebaseAuth.getInstance().getCurrentUser()!=null)
                                    mDatabaseRef.child("Name").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.choose:
                FileChooser();
                break;
            case R.id.upload :
                if (mUploadTask != null && mUploadTask.isInProgress())
                    Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                else
                    uploadImage();
                break;
        }
    }
}
