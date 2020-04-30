package com.example.bestphotocollections.Fragments.HomeFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bestphotocollections.Adapter.ItemGroupAdapter;
import com.example.bestphotocollections.AddPhoto_Activity;
import com.example.bestphotocollections.Model.ItemGroup;
import com.example.bestphotocollections.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements Contract.view{

    RecyclerView recyclerView;
    ProgressBar progressBar;
    FloatingActionButton fb;
    ItemGroupAdapter adpter;
    Contract.Presenter presenter;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment,container,false);
        getActivity().setTitle("Home");
        recyclerView = (RecyclerView)  view.findViewById(R.id.recycler_view_group);
        fb = view.findViewById(R.id.Add_Photo);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser!=null) {
                    if (currentUser.getDisplayName().equals(""))
                        showToast("Please Update Your Profile First.");
                    else
                        startActivity(new Intent(getContext(), AddPhoto_Activity.class));
                }

            }
        });

        presenter = new Presenter(HomeFragment.this);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        presenter.getItemGroupList();

        return view;
    }

    @Override
    public void showToast(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setRecyclerView(ArrayList<ItemGroup> list_group) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adpter = new ItemGroupAdapter(getContext(), list_group);
        recyclerView.setAdapter(adpter);
    }

    @Override
    public void progressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    @Override
    public void updateAdapter() {
        adpter.notifyDataSetChanged();
    }
}
