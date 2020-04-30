package com.example.bestphotocollections.Fragments.MyUploadsFragment;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.bestphotocollections.Model.Upload;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Presenter implements com.example.bestphotocollections.Fragments.MyUploadsFragment.Contract.Presenter {
    DatabaseReference databaseReference;
    ArrayList<Upload> list_item;
    Contract.view mainView;
    FirebaseStorage mstorage = FirebaseStorage.getInstance();
    ValueEventListener mDBListener;

    public Presenter(com.example.bestphotocollections.Fragments.MyUploadsFragment.Contract.view mainView ){
        this.mainView =mainView;
    }

    @Override
    public void getItemGroupList() {
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads/"+ FirebaseAuth.getInstance().getUid()+"/item_list");
        mDBListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               list_item =new ArrayList<>();
                for(DataSnapshot listsnapshot : dataSnapshot.getChildren()){
                    Upload itemData = new Upload();
                    itemData.setMtitle(listsnapshot.child("mtitle").getValue().toString());
                    itemData.setmMatadata(listsnapshot.child("mMatadata").getValue().toString());
                    itemData.setmUri(listsnapshot.child("mUri").getValue().toString());
                    itemData.setmKey(listsnapshot.getKey());
                    list_item.add(itemData);
                }
                mainView.progressBarVisibility(View.GONE);
                if(list_item.isEmpty())
                    mainView.setText("You haven't Uploaded anything yet \n Click here for your first Upload.");
                mainView.setRecyclerView(list_item);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mainView.progressBarVisibility(View.GONE);
                mainView.showToast(databaseError.getMessage());
            }
        });
    }

    @Override
    public void deletePhoto(int position) {
        Log.d("uploadPresenetr","delete Clicked");
        Upload selectedItem = list_item.get(position);
        final String selectedKey = selectedItem.getmKey();

        StorageReference imageRef = mstorage.getReferenceFromUrl(selectedItem.getmUri());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child(selectedKey).removeValue();
                mainView.showToast("Photo Deleted");
            }
        });
    }

    @Override
    public void destroyExcessValueEventListener() {
        databaseReference.removeEventListener(mDBListener);
    }

}
