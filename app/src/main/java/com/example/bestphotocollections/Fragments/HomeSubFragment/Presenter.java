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
                    itemGroup.setName(groupsnapshot.child("Name").getValue().toString());
                    if (groupsnapshot.child("ProfilePicUri").getValue()!=null)
                    itemGroup.setUri(groupsnapshot.child("ProfilePicUri").getValue().toString());
                    if (groupsnapshot.child("Metadata").getValue()!=null)
                    itemGroup.setMetadata(groupsnapshot.child("Metadata").getValue().toString());
                    itemGroup.setUid(groupsnapshot.getKey());
                    Log.d("bnmscjblisdu","uidSet * "+groupsnapshot.getKey());
                    //GenericTypeIndicator<ArrayList<ItemData>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<ItemData>>() {};
                    //GenericTypeIndicator<ArrayList<ItemData>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<ItemData>>(){};
                    list_item = new ArrayList<>();
                    for(DataSnapshot listsnapshot : groupsnapshot.child("item_list").getChildren()){
                        ItemData itemData = new ItemData();
                        itemData.setMtitle(listsnapshot.child("mtitle").getValue().toString());
                        itemData.setmMatadata(listsnapshot.child("mMatadata").getValue().toString());
                        itemData.setmUri(listsnapshot.child("mUri").getValue().toString());
                        list_item.add(itemData);
                    }
                    Collections.reverse(list_item);
                    itemGroup.setItem_list(list_item);

                    for (DataSnapshot groupSnapshot : dataSnapshot.child(FirebaseAuth.getInstance().getUid()+"/Followers").getChildren()) {
                        if (groupSnapshot.getValue().equals(itemGroup.getUid())) {
                            itemGroup.setBtnFollowCondition(1);
                            Log.d("FollowersAdapter", "entered");
                            break;
                        }
                    }
                    for (DataSnapshot groupSnapshot : dataSnapshot.child(FirebaseAuth.getInstance().getUid()+"/Followings").getChildren()) {
                        if (groupSnapshot.getValue().equals(itemGroup.getUid())) {
                            itemGroup.setBtnFollowCondition(2);
                            Log.d("FollowingssAdapter", "entered");
                            break;
                        }
                    }
                    if (FirebaseAuth.getInstance().getUid().equals(itemGroup.getUid())) {
                        Log.d("mhihu", "entered");
                        itemGroup.setBtnFollowCondition(3);
                    }

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
