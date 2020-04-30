package com.example.bestphotocollections.MessagingActivities.DirectMessage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bestphotocollections.Adapter.AdapterChatMessages;
import com.example.bestphotocollections.Model.ModelChatMessages;
import com.example.bestphotocollections.R;
import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DirectMessageActivity extends AppCompatActivity implements Contract.View {
    String oppositePersonUid , name , profilePicUri;
    @BindView(R.id.recyclerViewChats)
    RecyclerView recyclerView;
    @BindView(R.id.editTextMessage)
    EditText textMessage;
    @BindView(R.id.send)
    ImageView send;
    @BindView(R.id.progressBarChatsActivity)
    ProgressBar progressBar;
    @BindView(R.id.name)
    TextView toolbarName;
    @BindView(R.id.profileImage)
    CircularImageView toolbarProfileImage;
    @BindView(R.id.myProfilePic)
    CircularImageView myProfilePic;
    @BindView(R.id.oppositeProfilePic)
    CircularImageView oppositeProfilePic;
    @BindView(R.id.welcomeConstraintLayout)
    ConstraintLayout welcomeConstraintLayout;
    Contract.Presenter presenter;
    AdapterChatMessages adapter;
    ArrayList<ModelChatMessages> MessagesList;


    @Override
    protected void onStart() {
        super.onStart();
        oppositePersonUid = getIntent().getStringExtra("uid");
        name = getIntent().getStringExtra("name");
        profilePicUri = getIntent().getStringExtra("profilePicUri");
        toolbarName.setText(name);
        if(profilePicUri!=null)
        Picasso.get().load(Uri.parse(profilePicUri)).placeholder(R.drawable.ic_profile).into(toolbarProfileImage);
        Log.d("ahdvhcjsd",oppositePersonUid+name+profilePicUri);
        presenter.loadMessages(oppositePersonUid,name,profilePicUri,true);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_messages);
        ButterKnife.bind(this);
        oppositePersonUid = getIntent().getStringExtra("uid");
        name = getIntent().getStringExtra("name");
        profilePicUri = getIntent().getStringExtra("profilePicUri");
        presenter = new Presenter(DirectMessageActivity.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = textMessage.getText().toString().trim();
                Log.d("datachanged","message*"+message);
                if(message.isEmpty())
                    Toast.makeText(DirectMessageActivity.this, "Please Enter Some Message.", Toast.LENGTH_SHORT).show();
                else
                    presenter.sendMessage(message,oppositePersonUid);
                textMessage.setText(null);
            }
        });
    }

    @Override
    public void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void notifyAdapterChange(ArrayList<ModelChatMessages> list) {
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(list.size()-1);
        progressBar.setVisibility(View.GONE);
        welcomeConstraintLayout.setVisibility(View.GONE);
    }

    @Override
    public void loadRecyclerView(ArrayList<ModelChatMessages> list) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();
        adapter = new AdapterChatMessages(list, DirectMessageActivity.this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void userHaveNoMessages() {
        progressBar.setVisibility(View.GONE);
        welcomeConstraintLayout.setVisibility(View.VISIBLE);
        if (profilePicUri!=null)
        Picasso.get().load(Uri.parse(profilePicUri)).placeholder(R.drawable.ic_profile).into(oppositeProfilePic);
        if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()!=null)
            Picasso.get().load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).placeholder(R.drawable.ic_profile).into(myProfilePic);

    }
}
