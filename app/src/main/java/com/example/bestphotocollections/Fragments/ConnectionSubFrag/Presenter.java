package com.example.bestphotocollections.Fragments.ConnectionSubFrag;

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
import java.util.Collections;

public class Presenter implements Contract.Presenter {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("uploads/");
    Contract.View mainView;
    ArrayList<ModelConnection> list;

    public Presenter(Contract.View mainView) {
        this.mainView = mainView;
    }


    @Override
    public void loadDataFromConnections() {
        list= new ArrayList<>();
        mainView.setRecyclerView(list);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot uploads_dataSnapshot) {
                list.clear();
                String myUid = FirebaseAuth.getInstance().getUid();
                DataSnapshot myFollowings_dataSnapshot = uploads_dataSnapshot.child(myUid+"/Followings/");
                for (DataSnapshot connection : myFollowings_dataSnapshot.getChildren()){
                    String uid = connection.getKey();
                    DataSnapshot dataSnapshot = uploads_dataSnapshot.child(uid);
                    Log.d("list", list.toString());
                    Log.d("currentUid", uid);

                    String profilePicUri = null, name = null;
                    if (dataSnapshot.child("/Name").getValue() != null)
                        name = (dataSnapshot.child("/Name").getValue().toString());
                    if (dataSnapshot.child("/ProfilePicUri").getValue() != null)
                        profilePicUri = (dataSnapshot.child("/ProfilePicUri").getValue().toString());

                    for (DataSnapshot groupSnapshot : dataSnapshot.child("/item_list").getChildren()) {
                        ModelConnection model = new ModelConnection();
                        model.setTitle(groupSnapshot.child("mtitle").getValue().toString());
                        model.setMetadata(groupSnapshot.child("mMatadata").getValue().toString());
                        model.setUri(groupSnapshot.child("mUri").getValue().toString());
                        model.setName(name);
                        model.setUid(uid);
                        model.setProfileImageUri(profilePicUri);
                        model.setKey(groupSnapshot.getKey());
                        list.add(model);

                        Log.d("ssss", name + profilePicUri + list);
                    }
                    Log.d("list5", list.toString());
                    Collections.shuffle(list);
                    mainView.NotifyAdapter(list);

                }

                DataSnapshot myChatAvailableFriends_dataSnapshot = uploads_dataSnapshot.child(myUid+"/ChatsAvailable/");
                for (DataSnapshot connection : myChatAvailableFriends_dataSnapshot.getChildren()){
                    String uid = connection.getKey();
                    if(!myFollowings_dataSnapshot.child(uid).exists()) {
                        DataSnapshot dataSnapshot = uploads_dataSnapshot.child(uid);
                        Log.d("list", list.toString());
                        Log.d("currentUid", uid);

                        String profilePicUri = null, name = null;
                        if (dataSnapshot.child("/Name").getValue() != null)
                            name = (dataSnapshot.child("/Name").getValue().toString());
                        if (dataSnapshot.child("/ProfilePicUri").getValue() != null)
                            profilePicUri = (dataSnapshot.child("/ProfilePicUri").getValue().toString());

                        for (DataSnapshot groupSnapshot : dataSnapshot.child("/item_list").getChildren()) {
                            ModelConnection model = new ModelConnection();
                            model.setTitle(groupSnapshot.child("mtitle").getValue().toString());
                            model.setMetadata(groupSnapshot.child("mMatadata").getValue().toString());
                            model.setUri(groupSnapshot.child("mUri").getValue().toString());
                            model.setName(name);
                            model.setUid(uid);
                            model.setProfileImageUri(profilePicUri);
                            model.setKey(groupSnapshot.getKey());
                            if (groupSnapshot.child("/Likes/"+myUid).exists())
                                model.setLiked(true);
                            else model.setLiked(false);
                            list.add(model);
                        }
                        Collections.shuffle(list);
                        mainView.NotifyAdapter(list);
                    }

                }

                mainView.NotifyAdapter(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mainView.showToast(databaseError.getMessage());
            }
        });
    }

    @Override
    public void setPicLiked(String uid, String key) {
        databaseReference.child(uid+"/item_list/"+key+"/Likes").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String myUid = FirebaseAuth.getInstance().getUid();
                        if (dataSnapshot.child(myUid).exists())
                            databaseReference.child(uid+"/item_list/"+key+"/Likes/"+myUid).removeValue();
                        else
                            databaseReference.child(uid+"/item_list/"+key+"/Likes/"+myUid).setValue(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        mainView.showToast(databaseError.getMessage());
                    }
                });
    }
}
