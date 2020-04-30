package com.example.bestphotocollections.Fragments.ProfileFragment;

import android.net.Uri;

public class Contract {
    public interface view{

        void setProgressbar(int visible);

        void Toast(String s);

        String getFileExtension(Uri mImageUri);

        void setProfileData(long followers, long followings, long total_uploads, String metadata);
    }
    public interface presenter{

        void setProfileName(String trim , String toString);

        void loadDataOfUser();

        void setProfilePic(Uri imageUri);
    }
}
