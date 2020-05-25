package com.example.bestphotocollections.Profile.Activities.ShowProfile;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.bestphotocollections.Model.ModelConnection;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Presenter implements Contract.Presenter {

    Contract.View mainView;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("uploads/");
    ArrayList<ModelConnection> list;
    ArrayList<ModelConnection> listMessageRequest;

    public Presenter(Contract.View mainView) {
        this.mainView = mainView;
    }

    @Override
    public void loadProfileData(String uid) {
        String myUid = FirebaseAuth.getInstance().getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                DataSnapshot dataSnapshot = ds.child(uid);
                long followers = dataSnapshot.child("Followers").getChildrenCount();
                long followings = dataSnapshot.child("Followings").getChildrenCount();
                long uploads = dataSnapshot.child("item_list").getChildrenCount();
                String metadata = null;
                if (dataSnapshot.child("Metadata").getValue()!=null)
                 metadata = dataSnapshot.child("Metadata").getValue().toString();
                mainView.setProfileData(followers,followings,uploads,metadata);

                if(!uid.equals(myUid)) {
                    if (ds.child(myUid + "/Followings/" + uid).exists())
                        mainView.setFollowBtn(true);
                    else
                        mainView.setFollowBtn(false);

                    if (ds.child(uid + "/ChatsAvailable/" + myUid).exists())
                        mainView.setMessageBtn(2);
                    else if (ds.child(uid + "/MessageRequest/" + myUid).exists())
                        mainView.setMessageBtn(1);
                    else if (ds.child(myUid + "/MessageRequest/" + uid).exists())
                        mainView.setMessageBtn(3);
                    else
                        mainView.setMessageBtn(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mainView.showToast(databaseError.getMessage());
            }
        });
    }

    @Override
    public void loadMessageRequests(String uid) {
        listMessageRequest= new ArrayList<>();
        mainView.setMsgRequestRecyclerView(listMessageRequest);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                listMessageRequest.clear();
                DataSnapshot dataSnapshot = ds.child(uid+"/MessageRequest/");
                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()){
                    ModelConnection model = new ModelConnection();
                    String uidCheck = groupSnapshot.getKey();

                    DataSnapshot dataSnapshot1 = ds.child(uidCheck);
                    if (dataSnapshot1.child("Name").exists())
                    model.setName(dataSnapshot1.child("Name").getValue().toString());
                    if (dataSnapshot1.child("ProfilePicUri").exists())
                    model.setProfileImageUri(dataSnapshot1.child("ProfilePicUri").getValue().toString());
                    model.setUid(uidCheck);
                    listMessageRequest.add(model);
                    Log.d("list",listMessageRequest.toString());
                }
                mainView.setPostsText(false,listMessageRequest.size());
                mainView.notifyMsgRequestAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mainView.showToast(databaseError.getMessage());
            }
        });
    }

    @Override
    public void loadPosts(String uid,String name,String ProfilePicUri) {
        list= new ArrayList<>();
        mainView.setRecyclerView(list);
        databaseReference.child(uid+"/item_list/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
               for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()){
                   ModelConnection model = new ModelConnection();
                   model.setName(name);
                   model.setProfileImageUri(ProfilePicUri);
                   model.setTitle(groupSnapshot.child("mtitle").getValue().toString());
                   model.setMetadata(groupSnapshot.child("mMatadata").getValue().toString());
                   model.setUri(groupSnapshot.child("mUri").getValue().toString());
                   list.add(model);
               }
                mainView.setPostsText(true,list.size());
               mainView.notifyAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mainView.showToast(databaseError.getMessage());
            }
        });
    }
}
