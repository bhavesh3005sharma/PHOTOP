package com.example.bestphotocollections.MessagingActivities.ChatActivity;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.bestphotocollections.Model.ModelMessagingPersons;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class Presenter implements Contracter.presenter {
    Contracter.mainView  mainView;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("uploads/");
    ArrayList<String> listUidForChats;
    ArrayList<ModelMessagingPersons> list;

    public Presenter(Contracter.mainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void loadContactList() {
        databaseReference.child(FirebaseAuth.getInstance().getUid()+"/ChatsAvailable")
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listUidForChats =new ArrayList<>();
                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren())
                listUidForChats.add(""+groupSnapshot.getValue());
                loadDataForAllMembers(listUidForChats);
                Log.d("ChatActivityLog","Success in loading list");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mainView.showToast(databaseError.getMessage());
                Log.d("ChatActivityLog",databaseError.getMessage());
            }
        });
    }

    private void loadDataForAllMembers(ArrayList<String> listUidForChats) {
        list =new ArrayList<>();
        mainView.setRecyclerView(list);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot mainSnapshot) {
                list.clear();
                DataSnapshot chats_snapshot = mainSnapshot.child(FirebaseAuth.getInstance().getUid()+"/ChatsAvailable");
                for (DataSnapshot chat :chats_snapshot.getChildren()){
                    String uid = chat.getKey();
                    DataSnapshot groupSnapshot = mainSnapshot.child(uid);
                        ModelMessagingPersons person = new ModelMessagingPersons();
                        person.setName(groupSnapshot.child("Name").getValue().toString());
                        if (groupSnapshot.child("ProfilePicUri").exists())
                            person.setProfileUrl(groupSnapshot.child("ProfilePicUri").getValue().toString());
                        person.setUid(uid);
                        list.add(person);
                }
                Collections.reverse(list);
                mainView.saveToShredPref(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mainView.showToast(databaseError.getMessage());
            }
        });
    }
}
