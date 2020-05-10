package com.example.bestphotocollections.MessagingActivities.ChatActivity;

import com.example.bestphotocollections.Model.ModelMessagingPersons;

import java.util.ArrayList;

public class Contracter {
    interface mainView{

        void showToast(String message);

        void setRecyclerView(ArrayList<ModelMessagingPersons> list);

        void saveToShredPref(ArrayList<ModelMessagingPersons> list);
    }
    interface presenter{

        void loadContactList();
    }
}
