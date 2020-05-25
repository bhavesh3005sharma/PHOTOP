package com.example.bestphotocollections.Fragments.HomeSubFragment;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.bestphotocollections.Model.ItemData;
import com.example.bestphotocollections.Model.ItemGroup;
import com.example.bestphotocollections.Model.Upload;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class Presenter implements Contract.Presenter {
    DatabaseReference databaseReference;
    ArrayList<ItemData> list_item;
    ArrayList<ItemGroup> list_group;
    Contract.view mainView;

    public Presenter(Contract.view mainView ){
        this.mainView =mainView;
    }

    @Override
    public ArrayList<Upload> getItemGroupList() {
        list_group = new ArrayList<>();
        mainView.setRecyclerView(list_group);
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list_group.clear();
                for(DataSnapshot groupsnapshot : dataSnapshot.getChildren()){
                    ItemGroup itemGroup = new ItemGroup();
                    if (groupsnapshot.child("Name").getValue()!=null)
                    itemGroup.setName(groupsnapshot.child("Name").getValue().toString());
                    if (groupsnapshot.child("ProfilePicUri").getValue()!=null)
                    itemGroup.setUri(groupsnapshot.child("ProfilePicUri").getValue().toString());
                    if (groupsnapshot.child("Metadata").getValue()!=null)
                    itemGroup.setMetadata(groupsnapshot.child("Metadata").getValue().toString());
                    itemGroup.setUid(groupsnapshot.getKey());

                    list_item = new ArrayList<>();
                    for(DataSnapshot listSnapshot : groupsnapshot.child("item_list").getChildren()){
                        ItemData itemData = new ItemData();
                        itemData.setMtitle(listSnapshot.child("mtitle").getValue().toString());
                        itemData.setmMatadata(listSnapshot.child("mMatadata").getValue().toString());
                        itemData.setmUri(listSnapshot.child("mUri").getValue().toString());
                        itemData.setKey(listSnapshot.getKey().toString());
                        list_item.add(itemData);
                    }
                    Collections.reverse(list_item);
                    itemGroup.setItem_list(list_item);

                    if(list_item.size()==0)
                        continue;

                    list_group.add(itemGroup);
                }
                mainView.updateAdapter();
                mainView.progressBarVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mainView.progressBarVisibility(View.GONE);
                mainView.showToast(databaseError.getMessage());
            }
        });
        return null;
    }

}
