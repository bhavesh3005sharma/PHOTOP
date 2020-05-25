package com.example.bestphotocollections.Fragments.ConnectionSubFrag;

import com.example.bestphotocollections.Model.ModelConnection;

import java.util.ArrayList;

public class Contract {
    interface  View{

        void showToast(String message);

        void setRecyclerView(ArrayList<ModelConnection> list);

        void NotifyAdapter(ArrayList<ModelConnection> list);
    }

    interface Presenter{

        void loadDataFromConnections();

        void setPicLiked(String uid, String key);
    }
}
