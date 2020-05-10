package com.example.bestphotocollections.Fragments.ConnectionSubFrag;

import android.os.Handler;
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
import java.util.Random;

public class Presenter implements Contract.Presenter {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("uploads/");
    Contract.View mainView;
    ArrayList<String> listUidForConnections;
    ArrayList<ModelConnection> list;

    public Presenter(Contract.View mainView) {
        this.mainView = mainView;
    }

    @Override
    public void loadDataFromConnections() {
        databaseReference.child(FirebaseAuth.getInstance().getUid()+"/ChatsAvailable")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listUidForConnections =new ArrayList<>();
                        for (DataSnapshot groupSnapshot : dataSnapshot.getChildren())
                            listUidForConnections.add(""+groupSnapshot.getValue());
                        Log.d("listOfConnections1",listUidForConnections.toString());
                        loadDataForAllMembers(listUidForConnections);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        mainView.showToast(databaseError.getMessage());
                    }
                });
    }

    private void loadDataForAllMembers(ArrayList<String> listUidForConnections) {
        Log.d("listOfConnections",listUidForConnections.toString());
        list= new ArrayList<>();
        mainView.setRecyclerView(list);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot main_dataSnapshot) {
                list.clear();
                for (int i = 0; i < listUidForConnections.size(); i++) {
                    DataSnapshot dataSnapshot = main_dataSnapshot.child(listUidForConnections.get(i));
                    Log.d("list", list.toString());
                    Log.d("currentUid", listUidForConnections.get(i));

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
                        model.setUid(listUidForConnections.get(i));
                        model.setProfileImageUri(profilePicUri);
                        list.add(model);

                        Log.d("ssss", name + profilePicUri + list);
                    }
                    Log.d("list5", list.toString());
                    Collections.shuffle(list);
                    mainView.NotifyAdapter();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mainView.showToast(databaseError.getMessage());
            }
        });

            //mainView.NotifyAdapter();
    }
}
