package com.example.bestphotocollections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bestphotocollections.Model.ItemData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class showSelectedImg extends AppCompatActivity {
    @BindView(R.id.selectedImg)
    ImageView img;
    @BindView(R.id.likes)
    TextView number_Likes;
    @BindView(R.id.dislike)
    TextView number_Dislikes;
    @BindView(R.id.views)
    TextView number_Views;
    @BindView(R.id.imgLike)
    ImageView like;
    @BindView(R.id.ImgDislike)
    ImageView dislike;
    @BindView(R.id.download)
    Button download;
    @BindView(R.id.textPrice)
    TextView price;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.metadata)
    TextView metadata;
    Unbinder unbinder;
    String uri=null;
    String str_title;
    String str_metadata;
    ArrayList<ItemData> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_selected_img);
        setTitle("Photop");
        unbinder = ButterKnife.bind(this);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
        );

        Intent intent =getIntent();
        uri = intent.getStringExtra("uri");
        Picasso.get().load(Uri.parse(uri)).placeholder(R.mipmap.ic_launcher).into(img);
        str_title=intent.getStringExtra("title");
        str_metadata = intent.getStringExtra("metadata");
        title.setText(str_title);
        metadata.setText(str_metadata);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.download)
    public void download(View view){
        downloadFile();
    }

    private void downloadFile() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(uri);
        //StorageReference islandRef = storageRef.child("file.txt");

        File rootPath = new File(Environment.getExternalStorageDirectory(), "Photop Downloads");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath, Calendar.getInstance().getTimeInMillis()+".jpg");

        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ", ";local tem file created  created " + localFile.toString());
                loadFromDownloadsSharedPref();
                list.add(new ItemData(str_metadata,uri,str_title));
                saveToDownloadsSharedPref();
                Toast.makeText(showSelectedImg.this, "Download Successful\n Check in Storage/Photop Downloads", Toast.LENGTH_LONG).show();
                //  updateDb(timestamp,localFile.toString(),position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                Toast.makeText(showSelectedImg.this, "Download UnSuccessful\n"+exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveToDownloadsSharedPref() {
        SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(R.string.sharedPrefDownloads),MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("list",json);
        editor.apply();
        Log.d("selectedImgSave",list.toString());
    }

    private  void loadFromDownloadsSharedPref(){
        SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(R.string.sharedPrefDownloads),MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("list", null);
        Type type = new TypeToken<ArrayList<ItemData>>() {}.getType();
        list = gson.fromJson(json, type);

        if(list==null)
            list = new ArrayList<>();

        Log.d("selectedImgLoad",list.toString());
    }
}
