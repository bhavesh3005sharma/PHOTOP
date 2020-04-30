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
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list =new ArrayList<>();
                for (DataSnapshot groupSnapshot :dataSnapshot.getChildren()){
                    for (int i=0;i<listUidForChats.size();i++){
                        if (groupSnapshot.getKey().equals(listUidForChats.get(i))){
                            ModelMessagingPersons person = new ModelMessagingPersons();
                            person.setName(groupSnapshot.child("Name").getValue().toString());
                            if (groupSnapshot.hasChild("ProfilePicUri"))
                            person.setProfileUrl(groupSnapshot.child("ProfilePicUri").getValue().toString());
                            person.setUid(listUidForChats.get(i));
                            list.add(person);
                            break;
                        }
                    }
                }
                Collections.reverse(list);
                mainView.setRecyclerView(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mainView.showToast(databaseError.getMessage());
            }
        });
    }
}
