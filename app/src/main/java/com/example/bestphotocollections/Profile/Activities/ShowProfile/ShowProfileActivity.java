package com.example.bestphotocollections.Profile.Activities.ShowProfile;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bestphotocollections.Adapter.AdapterConnections;
import com.example.bestphotocollections.Adapter.AdapterMessageRequests;
import com.example.bestphotocollections.Profile.Activities.EditProfileActivity.EditProfileActivity;
import com.example.bestphotocollections.MessagingActivities.DirectMessage.DirectMessageActivity;
import com.example.bestphotocollections.Model.ModelConnection;
import com.example.bestphotocollections.R;
import com.example.bestphotocollections.Utilities.FollowersHandler;
import com.example.bestphotocollections.Utilities.MessageRequestHandler;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ShowProfileActivity extends AppCompatActivity implements Contract.View, View.OnClickListener {

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.followers) TextView followers;
    @BindView((R.id.followings)) TextView followings;
    @BindView((R.id.uploads)) TextView uploads;
    @BindView((R.id.editProfile)) TextView editProfile;
    @BindView((R.id.textPosts)) TextView textPosts;
    @BindView((R.id.metadata)) TextView metadata;
    @BindView((R.id.textMetadata)) TextView textMetadata;
    @BindView((R.id.name)) TextView text_name;
    @BindView(R.id.followBtn) Button followBtn;
    @BindView(R.id.messageRequestBtn) Button messageRequestBtn;
    @BindView(R.id.profile_image) CircularImageView profileImg;
    @BindView(R.id.constraintLayoutDetails) ConstraintLayout constraintLayoutDetails;
    @BindView(R.id.shimmer_view_container) ShimmerFrameLayout shimmerFrameLayout;
    Contract.Presenter presenter;
    Unbinder unbinder;
    String uid,name,strUri;
    AdapterConnections postAdapter;
    AdapterMessageRequests msgRequestAdapter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_profile_activity);

        unbinder=ButterKnife.bind(this);
        presenter = new Presenter(ShowProfileActivity.this);

        uid=getIntent().getStringExtra("uid");
        name=getIntent().getStringExtra("name");
        strUri=getIntent().getStringExtra("uri");
        if(strUri!=null)
            Picasso.get().load(Uri.parse(strUri)).placeholder(R.drawable.ic_profile).into(profileImg);
        text_name.setText(name);
        setTitle(name);

        messageRequestBtn.setOnClickListener(this);
        editProfile.setOnClickListener(this);
        followBtn.setOnClickListener(this);

        shimmerFrameLayout.startShimmer();
        presenter.loadProfileData(uid);
        if (uid.equals(FirebaseAuth.getInstance().getUid()))
            presenter.loadMessageRequests(FirebaseAuth.getInstance().getUid());
        else {
            presenter.loadPosts(uid, name, strUri);
            editProfile.setVisibility(View.GONE);
        }

        messageRequestBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(messageRequestBtn.getText().equals("REQUEST SENT") && FirebaseAuth.getInstance().getUid()!=null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowProfileActivity.this);

                    builder.setPositiveButton("CANCEL REQUEST", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MessageRequestHandler.cancelRequest(uid,FirebaseAuth.getInstance().getUid());
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setTitle("Cancel Request !");
                    alertDialog.show();
                }
                return true;
            }
        });
    }

    @Override
    public void setProfileData(long followers1, long followings1, long uploads1, String metadata1) {
        if(followers!=null)
        followers.setText(""+followers1);
        if(followings!=null)
        followings.setText(""+followings1);
        if(uploads!=null)
        uploads.setText(""+uploads1);
        if(metadata!=null)
        metadata.setText(metadata1);
        if (metadata1==null)
            textMetadata.setVisibility(View.GONE);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setRecyclerView(ArrayList<ModelConnection> list) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.hasFixedSize();
        postAdapter = new AdapterConnections(list,this);
        recyclerView.setAdapter(postAdapter);
    }

    @Override
    public void notifyAdapter() {
        postAdapter.notifyDataSetChanged();
        if (shimmerFrameLayout!=null) {
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
        }
        constraintLayoutDetails.setVisibility(View.VISIBLE);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void setFollowBtn(boolean b) {
        if (b==true && followBtn!=null) {
            followBtn.setBackgroundResource(R.drawable.button_background2);
            followBtn.setText("UNFOLLOW");
            followBtn.setTextColor(Color.BLACK);
        }
        else if (followBtn!=null){
            followBtn.setBackgroundResource(R.drawable.background);
            followBtn.setText("FOLLOW");
            followBtn.setTextColor(Color.WHITE);
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void setMessageBtn(int conditionForMessage) {
        if(messageRequestBtn!=null) {
            if (conditionForMessage == 0) {
                messageRequestBtn.setBackgroundResource(R.drawable.button_background2);
                messageRequestBtn.setText("MESSAGE REQUEST");
                messageRequestBtn.setTextColor(Color.BLACK);
            } else if (conditionForMessage == 1) {
                messageRequestBtn.setBackgroundResource(R.drawable.button_background2);
                messageRequestBtn.setText("REQUEST SENT");
                messageRequestBtn.setTextColor(Color.BLACK);
            } else if (conditionForMessage == 2) {
                messageRequestBtn.setBackgroundResource(R.drawable.background);
                messageRequestBtn.setText("MESSAGE");
                messageRequestBtn.setTextColor(Color.WHITE);
            }
            else if (conditionForMessage == 3) {
                messageRequestBtn.setBackgroundResource(R.drawable.background);
                messageRequestBtn.setText("ACCEPT REQUEST");
                messageRequestBtn.setTextColor(Color.WHITE);
            }
        }
    }

    @Override
    public void setMsgRequestRecyclerView(ArrayList<ModelConnection> listMessageRequest) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.hasFixedSize();
        msgRequestAdapter = new AdapterMessageRequests(listMessageRequest,this);
        recyclerView.setAdapter(msgRequestAdapter);
    }

    @Override
    public void notifyMsgRequestAdapter() {
        if(msgRequestAdapter!=null && shimmerFrameLayout!=null && constraintLayoutDetails!=null
        && followBtn!=null && messageRequestBtn!=null) {
            msgRequestAdapter.notifyDataSetChanged();
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            constraintLayoutDetails.setVisibility(View.VISIBLE);
            followBtn.setVisibility(View.GONE);
            messageRequestBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void setPostsText(boolean isPosts, int size) {
        if(isPosts && textPosts!=null)
            textPosts.setText("POSTS ("+size+")");
        else if (textPosts!=null)
            textPosts.setText("MESSAGE REQUESTS ("+size+")");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.followBtn:
                if(followBtn!=null && followBtn.getText().equals("UNFOLLOW")) {
                    FollowersHandler.unFollow(FirebaseAuth.getInstance().getUid(), uid);
                }
                else if(followBtn!=null && followBtn.getText().equals("FOLLOW")){
                    FollowersHandler.addFollower(FirebaseAuth.getInstance().getUid(), uid);
                }
                break;
            case R.id.editProfile:
                if (editProfile!=null)
                startActivity(new Intent(ShowProfileActivity.this, EditProfileActivity.class));
                break;
            case R.id.messageRequestBtn :
                if (messageRequestBtn!=null && messageRequestBtn.getText().equals("MESSAGE REQUEST")) {
                    MessageRequestHandler.sendMessageRequest(FirebaseAuth.getInstance().getUid(), uid);
                }
                else if (messageRequestBtn!=null && messageRequestBtn.getText().equals("ACCEPT REQUEST")) {
                    MessageRequestHandler.acceptRequest(FirebaseAuth.getInstance().getUid(), uid);
                }
                else if (messageRequestBtn!=null && messageRequestBtn.getText().equals("MESSAGE")) {
                    Intent intent = new Intent(ShowProfileActivity.this, DirectMessageActivity.class);
                    intent.putExtra("uid",uid);
                    intent.putExtra("profilePicUri",strUri);
                    intent.putExtra("name",name);
                    startActivity(intent);
                }
                break;
        }
    }
}
