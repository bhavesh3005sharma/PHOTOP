package com.example.bestphotocollections.Fragments.HomeSubFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bestphotocollections.Adapter.ItemGroupAdapter;
import com.example.bestphotocollections.Fragments.MainHomeFragment;
import com.example.bestphotocollections.Model.ItemGroup;
import com.example.bestphotocollections.R;

import java.util.ArrayList;

public class HomeSubFragment extends Fragment implements Contract.view,MainHomeFragment.OnItemClickListener{

    RecyclerView recyclerView;
    ProgressBar progressBar;
    ItemGroupAdapter adapter;
    Contract.Presenter presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_sub_fragment,container,false);
        getActivity().setTitle("PHOTOP");
        recyclerView =  view.findViewById(R.id.recycler_view_group);
        MainHomeFragment mainHomeFragment = new MainHomeFragment();
        mainHomeFragment.setOnItemClickListener(HomeSubFragment.this);

        presenter = new Presenter(HomeSubFragment.this);
        progressBar =  view.findViewById(R.id.progressBar);
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
        adapter = new ItemGroupAdapter(getContext(), list_group);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void progressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    @Override
    public void updateAdapter() {
        adapter.notifyDataSetChanged();
    }


    @Override
    public void searchHome(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

}
