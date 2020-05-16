package com.example.bestphotocollections.Fragments.ProfileFragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.bestphotocollections.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment implements Contract.view, View.OnClickListener {

    @BindView(R.id.textView_uploads)
    TextView textUploads;
    @BindView(R.id.textView_followers)
    TextView textFollowers;
    @BindView(R.id.textView_following)
     TextView textFollowings;
    @BindView(R.id.profile_image)
     ImageView profileImage;
    @BindView(R.id.text_name)
     EditText editTextName;
    @BindView(R.id.text_email)
     TextView textEmail;
    @BindView(R.id.text_password)
     TextView textPassword;
    @BindView(R.id.text_bio)
     EditText editTextBio;
    @BindView(R.id.btn_save)
     Button btn_save;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    TextView gallery;
    TextView camera;
    TextView cancel;
    AlertDialog alertDialog;
     Contract.presenter presenter;
    Uri imageUri;
    SharedPreferences sharedPreferences;
     FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment,container,false);

        getActivity().setTitle("Profile");
        sharedPreferences = getActivity().getSharedPreferences("shared_pref_profile_data",Context.MODE_PRIVATE);

        ButterKnife.bind(this,view);
        presenter = new Presenter(ProfileFragment.this);

        loadDataFormSharedPref();
        editTextName.setText(currentUser.getDisplayName());
        textEmail.setText(currentUser.getEmail());
        if(currentUser.getPhotoUrl()!=null)
            Picasso.get().load(currentUser.getPhotoUrl()).placeholder(R.drawable.ic_profile).into(profileImage);
        presenter.loadDataOfUser();
        btn_save.setOnClickListener(this);
        profileImage.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save :
                presenter.setProfileName(editTextName.getText().toString().trim(),editTextBio.getText().toString().trim());
                break;
            case R.id.profile_image:
                Log.d("profile","CLicked");
                if(ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                { requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            2000);
                    Log.d("profilePermission","taken");
                }
                else if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {   requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        2000);
                    Toast.makeText(getContext(), "Give Permissions and try again.", Toast.LENGTH_SHORT).show();
                }
                else
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
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null)
                    startActivityForResult(takePictureIntent, 2);
                break;
            case R.id.cancel:
                alertDialog.dismiss();
                break;
        }
    }


    @Override
    public void setProgressbar(int v) {
        progressBar.setVisibility(v);
    }

    @Override
    public void Toast(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        alertDialog.dismiss();
        Log.d("ActivityResult","run");
        if (resultCode == getActivity().RESULT_OK && data != null) {
            if(requestCode == 1 && data.getData()!=null) {
                imageUri = data.getData();
                Picasso.get().load(imageUri).placeholder(R.drawable.ic_profile).into(profileImage);
                openAlertDialogueToShowPic();
            }else if(requestCode == 2 && data.getExtras()!=null){
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Log.d("***imageBitmap",imageBitmap+"");
                imageUri = getImageUri(getContext(),imageBitmap);
                openAlertDialogueToShowPic();
                profileImage.setImageBitmap(imageBitmap);
            }
            Log.d("imageUri",""+imageUri);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (alertDialog!=null)
        alertDialog.dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (alertDialog!=null)
        alertDialog.dismiss();
    }

    private void openAlertDialogueToShowPic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    @Override
    public void setProfileData(long followers, long followings, long total_uploads, String metadata) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        textFollowers.setText(""+followers);
        textFollowings.setText(""+followings);
        textUploads.setText(""+total_uploads);
        if(metadata.equals("null")) {
            metadata = "Add some bio about you. It will help your friends to recognise you.";
            editTextBio.setHint(metadata);
            Log.d("if-setprofileData-hint",metadata);
        }
        else {
            Log.d("else-setprfleData-text",metadata);
            editTextBio.setText(metadata);
            editor.putString("metadata",metadata);
        }
        editor.putLong("followers",followers);
        editor.putLong("followings",followings);
        editor.putLong("total_uploads",total_uploads);

        if(currentUser.getPhotoUrl()!=null)
            editor.putString("uri",currentUser.getPhotoUrl().toString());
        editor.apply();
    }

    private void loadDataFormSharedPref() {
        textFollowers.setText(""+sharedPreferences.getLong("followers",0));
        textFollowings.setText(""+sharedPreferences.getLong("followings",0));
        textUploads.setText(""+sharedPreferences.getLong("total_uploads",0));
        if(sharedPreferences.getString("uri",null)!=null)
            Picasso.get().load(sharedPreferences.getString("uri",null)).placeholder(R.drawable.ic_profile).into(profileImage);
        String metadata = sharedPreferences.getString("metadata",null);
        if(metadata==null) {
            metadata = "Add some bio about you. It will help your friends to recognise you.";
            editTextBio.setHint(metadata);
            Log.d("if-hint",metadata);
        }
        else {
            Log.d("else-text",metadata);
            editTextBio.setText(metadata);
        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        Log.d("imImage",""+inImage);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        Log.d("path",""+path);
        return Uri.parse(path);
    }
}
