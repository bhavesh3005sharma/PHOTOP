package com.example.bestphotocollections.Fragments.MyUploadsFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bestphotocollections.Adapter.UploadFragAdapter;
import com.example.bestphotocollections.AddPhoto_Activity;
import com.example.bestphotocollections.R;
import com.example.bestphotocollections.Model.Upload;

import java.util.ArrayList;
import java.util.Collections;

public class MyUploadsFragment extends Fragment implements Contract.view, UploadFragAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView textView;
    UploadFragAdapter adapter;
    Contract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  =  inflater.inflate(R.layout.myuploads_fragment,container,false);
        presenter = new Presenter(MyUploadsFragment.this);
        getActivity().setTitle("Your Uploaded Photos");

        textView = view.findViewById(R.id.hiddenText);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView_uploads);
        presenter.getItemGroupList();
        return view;
    }

    @Override
    public void showToast(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setRecyclerView(ArrayList<Upload> list_group) {
        Collections.reverse(list_group);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.hasFixedSize();
         adapter = new UploadFragAdapter(getContext(),list_group);
        adapter.setOnItemClickListener(MyUploadsFragment.this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void progressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    @Override
    public void setText(String s) {
        textView.setVisibility(View.VISIBLE);
        textView.setText(s);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddPhoto_Activity.class));
            }
        });
    }

    @Override
    public void ShowDetails(int position) {
        Toast.makeText(getContext(), "Show Details "+position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroyExcessValueEventListener();
    }

    @Override
    public void onDeleteClick(int position) {
        presenter.deletePhoto(position);
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(getContext(), "Item Clicked "+position, Toast.LENGTH_SHORT).show();
    }
}
