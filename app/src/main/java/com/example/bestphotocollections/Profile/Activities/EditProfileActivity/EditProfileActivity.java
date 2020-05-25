package com.example.bestphotocollections.Profile.Activities.EditProfileActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.bestphotocollections.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileActivity extends AppCompatActivity implements Contract.view, View.OnClickListener {

    @BindView(R.id.profile_image) ImageView profileImage;
    @BindView(R.id.text_name) EditText editTextName;
    @BindView(R.id.text_email) TextView textEmail;
    @BindView(R.id.text_password) TextView textPassword;
    @BindView(R.id.text_bio) EditText editTextBio;
    @BindView(R.id.btn_save) Button btn_save;
    @BindView(R.id.progressBar)ProgressBar progressBar;
    TextView gallery,camera,cancel;
    AlertDialog alertDialog;
    Contract.presenter presenter;
    Uri imageUri;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);
        setTitle("Edit Profile");
        ButterKnife.bind(this);
        presenter = new Presenter(EditProfileActivity.this);

        editTextName.setText(currentUser.getDisplayName());
        textEmail.setText(currentUser.getEmail());
        if(currentUser.getPhotoUrl()!=null)
            Picasso.get().load(currentUser.getPhotoUrl()).placeholder(R.drawable.ic_profile).into(profileImage);
        presenter.loadDataOfUser();
        btn_save.setOnClickListener(this);
        profileImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save :
                presenter.setProfileName(editTextName.getText().toString().trim(),editTextBio.getText().toString().trim());
                break;
            case R.id.profile_image:
                if(checkForReadPermission() && checkForWritePermission())
                    openAlertDialogue();
                break;
            case R.id.gallery:
                alertDialog.dismiss();
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                cameraIntent.setType("image/*");
                startActivityForResult(cameraIntent,1);
                break;
            case R.id.camera:
                alertDialog.dismiss();
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(EditProfileActivity.this.getPackageManager()) != null)
                    startActivityForResult(takePictureIntent, 2);
                break;
            case R.id.cancel:
                alertDialog.dismiss();
                break;
        }
    }

    private boolean checkForWritePermission() {
        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(EditProfileActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {   requestPermissions(
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                2000);
            Toast.makeText(EditProfileActivity.this, "Give Permissions and try again.", Toast.LENGTH_SHORT).show();
            //Permission automatically granted for Api<23 on installation
        }
        else
            return true;

        return false;
    }

    private boolean checkForReadPermission() {
        if(Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(EditProfileActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        { requestPermissions(
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                2000);
            //Permission automatically granted for Api<23 on installation
        }
        else
            return true;

        return false;
    }


    @Override
    public void setProgressbar(int v) {
        progressBar.setVisibility(v);
    }

    @Override
    public void Toast(String s) {
        Toast.makeText(EditProfileActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        alertDialog.dismiss();
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1 && data.getData() != null) {
                imageUri = data.getData();
                Picasso.get().load(imageUri).placeholder(R.drawable.ic_profile).into(profileImage);
                openAlertDialogueToShowPic();
            } else if (requestCode == 2 && data.getExtras() != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Log.d("***imageBitmap", imageBitmap + "");
                imageUri = getImageUri(EditProfileActivity.this, imageBitmap);
                openAlertDialogueToShowPic();
                profileImage.setImageBitmap(imageBitmap);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (alertDialog!=null)
        alertDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertDialog!=null)
            alertDialog.dismiss();
    }

    private void openAlertDialogueToShowPic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.layout_show_profile_image,null));

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ImageView imageView = alertDialog.findViewById(R.id.profile_imageToShow);
        TextView confirm = alertDialog.findViewById(R.id.confirm);
        TextView cancel = alertDialog.findViewById(R.id.cancel);

        Picasso.get().load(imageUri).placeholder(R.drawable.ic_profile).into(imageView);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.setProfilePic(imageUri);
                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void openAlertDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialogue_choose,null));
        alertDialog = builder.create();
        alertDialog.show();

        gallery = alertDialog.findViewById(R.id.gallery);
        camera =  alertDialog.findViewById(R.id.camera);
        cancel = alertDialog.findViewById(R.id.cancel);

        gallery.setOnClickListener(this);
        camera.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public String getFileExtension(Uri uri) {
        ContentResolver cR = EditProfileActivity.this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    @Override
    public void setProfileData(String metadata) {
        if(metadata.equals("null")) {
            metadata = "Add some bio about you. It will help your friends to recognise you.";
            editTextBio.setHint(metadata);
            Log.d("if-setprofileData-hint",metadata);
        }
        else {
            Log.d("else-setprfleData-text",metadata);
            editTextBio.setText(metadata);
        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
