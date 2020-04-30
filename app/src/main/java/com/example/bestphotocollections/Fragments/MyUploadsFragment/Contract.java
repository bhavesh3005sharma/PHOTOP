package com.example.bestphotocollections.Fragments.MyUploadsFragment;

import com.example.bestphotocollections.Model.Upload;
import java.util.ArrayList;

public class Contract {
    public interface view{
         void showToast(String s);
         void setRecyclerView(ArrayList<Upload> list_group);
         void progressBarVisibility(int visibility);

        void setText(String s);
    }
    public interface Presenter{
         void getItemGroupList();

        void deletePhoto(int position);

        void destroyExcessValueEventListener();
    }
}
