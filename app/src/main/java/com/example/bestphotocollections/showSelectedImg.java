package com.example.bestphotocollections;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_selected_img);
        setTitle("PhotopShop");
        ButterKnife.bind(this);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
        );

        Intent intent =getIntent();
        String uri = intent.getStringExtra("uri");
        Picasso.get().load(Uri.parse(uri)).placeholder(R.mipmap.ic_launcher).into(img);
        title.setText(intent.getStringExtra("title"));
        metadata.setText(intent.getStringExtra("metadata"));
    }
}
