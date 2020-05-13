package com.example.bestphotocollections.Fragments.DownloadFragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bestphotocollections.Adapter.AdapterConnections;
import com.example.bestphotocollections.Adapter.DownloadFragAdapter;
import com.example.bestphotocollections.Model.ItemData;
import com.example.bestphotocollections.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DownloadsFragment extends Fragment implements Contract.view {
    ArrayList<ItemData> list;
    @BindView(R.id.recyclerViewDownloads)
    RecyclerView recyclerView;
    DownloadFragAdapter adapter;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.download_fragment,container,false);
        getActivity().setTitle("Your Complete Collections");
        unbinder = ButterKnife.bind(this,view);

        loadFromDownloadsSharedPref();
        setRecyclerView();

        return view;
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.hasFixedSize();
        adapter = new DownloadFragAdapter(list,getContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private  void loadFromDownloadsSharedPref(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(String.valueOf(R.string.sharedPrefDownloads),getActivity().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("list", null);
        Type type = new TypeToken<ArrayList<ItemData>>() {}.getType();
        list = gson.fromJson(json, type);

        if(list==null)
            list = new ArrayList<>();

        Log.d("DownloadFragLoad",list.toString());
    }
}
