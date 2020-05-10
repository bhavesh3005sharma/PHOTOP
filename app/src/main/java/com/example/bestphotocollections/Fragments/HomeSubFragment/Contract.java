package com.example.bestphotocollections.Fragments.HomeSubFragment;

import com.example.bestphotocollections.Model.ItemGroup;
import com.example.bestphotocollections.Model.Upload;

import java.util.ArrayList;

public class Contract {
    public interface view{
        public void showToast(String s);
        public void setRecyclerView(ArrayList<ItemGroup> list_group);
        public void progressBarVisibility(int visibility);

        void updateAdapter();
    }
    public interface Presenter{
        public ArrayList<Upload> getItemGroupList();
    }
}
