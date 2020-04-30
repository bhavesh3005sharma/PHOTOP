package com.example.bestphotocollections.Fragments.ProfileFragment;

import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bestphotocollections.AddPhoto_Activity;
import com.example.bestphotocollections.Model.Upload;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class Presenter implements Contract.presenter{
    private Contract.view mainView;
    Uri downloadableUri;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads/"+ FirebaseAuth.getInstance().getUid()+"/");;
    private StorageTask mUploadTask;
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("ProfilePics/"+FirebaseAuth.getInstance().getUid());

    public Presenter(Contract.view mainView) {
        this.mainView = mainView;
    }

    @Override
    public void setProfileName(String name , String bio) {
            if (name.isEmpty() || bio.isEmpty())
                mainView.Toast("Name & Bio are Required.");
            else {
                if (currentUser != null) {
                    mainView.setProgressbar(View.VISIBLE);

                    UserProfileChangeRequest.Builder profileUpdates = new UserProfileChangeRequest.Builder();
                    profileUpdates.setDisplayName(name);

                    currentUser.updateProfile(profileUpdates.build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mainView.setProgressbar(View.GONE);
                            mainView.Toast("Profile Updated Successfully");
                        }
                    });

                    mDatabaseRef.child("Name").setValue(name);
                    mDatabaseRef.child("Metadata").setValue(bio);
                }
            }
    }

    @Override
    public void loadDataOfUser() {
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long followers = dataSnapshot.child("Followers").getChildrenCount();
                long followings = dataSnapshot.child("Followings").getChildrenCount();
                long total_uploads = dataSnapshot.child("item_list").getChildrenCount();
                String metadata = ""+dataSnapshot.child("Metadata").getValue();
                mainView.setProfileData(followers,followings,total_uploads,metadata);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mainView.Toast(""+databaseError.getMessage());
            }
        });
    }

    @Override
    public void setProfilePic(Uri mImageUri) {
        if (mUploadTask != null && mUploadTask.isInProgress())
            mainView.Toast("Profile Update is in Progress");
        else {
            mainView.setProgressbar(View.VISIBLE);
            StorageReference fileReference = mStorageRef.child("ProfilePic." + mainView.getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d("uploadProfilePic", "success");
                                    UserProfileChangeRequest.Builder profileUpdates = new UserProfileChangeRequest.Builder();
                                    profileUpdates.setPhotoUri(uri);

                                    currentUser.updateProfile(profileUpdates.build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            mainView.setProgressbar(View.GONE);
                                            mainView.Toast("Profile Photo Changed Successfully");
                                        }
                                    });

                                    String url = uri.toString();
                                    mDatabaseRef.child("ProfilePicUri").setValue(url);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mainView.Toast(e.getMessage());
                        }
                    });
        }
    }
}
