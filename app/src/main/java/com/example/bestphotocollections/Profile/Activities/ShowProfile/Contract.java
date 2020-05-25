package com.example.bestphotocollections.Profile.Activities.ShowProfile;

import com.example.bestphotocollections.Model.ModelConnection;

import java.util.ArrayList;

public class Contract {
    interface View{

        void setProfileData(long followers, long followings, long uploads, String metadata);

        void showToast(String message);

        void setRecyclerView(ArrayList<ModelConnection> list);

        void notifyAdapter();

        void setFollowBtn(boolean b);

        void setMessageBtn(int conditionForMessage);

        void setMsgRequestRecyclerView(ArrayList<ModelConnection> listMessageRequest);

        void notifyMsgRequestAdapter();

        void setPostsText(boolean b, int size);
    }
    interface  Presenter{

        void loadPosts(String uid,String name,String ProfilePicUri);

        void loadProfileData(String uid);

        void loadMessageRequests(String uid);
    }
}
