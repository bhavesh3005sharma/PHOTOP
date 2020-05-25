package com.example.bestphotocollections.Utilities;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FollowersHandler {

    public static void addFollower(String uidFollower, String uidFollowing) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("uploads/");
        databaseReference.child(uidFollower+"/Followings/"+uidFollowing).setValue(true);
        databaseReference.child(uidFollowing+"/Followers/"+uidFollower).setValue(true);
    }

    public static void unFollow(String uidFollower, String uidFollowing) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("uploads/");
        databaseReference.child(uidFollower+"/Followings/"+uidFollowing).removeValue();
        databaseReference.child(uidFollowing+"/Followers/"+uidFollower).removeValue();
    }
}
