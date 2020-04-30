package com.example.bestphotocollections.MessagingActivities.DirectMessage;

import com.example.bestphotocollections.Model.ModelChatMessages;

import java.util.ArrayList;

public class Contract {
    interface View{

        void showToast(String s);

        void notifyAdapterChange(ArrayList<ModelChatMessages> list);

        void loadRecyclerView(ArrayList<ModelChatMessages> list);

        void userHaveNoMessages();
    }
    interface Presenter{

        void sendMessage(String message,String oppositePersonUid);

        void loadMessages(String oppositePersonUid, String name, String profilePicUri, boolean b);
    }
}
