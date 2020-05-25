package com.example.bestphotocollections.Fragments.ConnectionSubFrag;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bestphotocollections.Adapter.AdapterConnections;
import com.example.bestphotocollections.Model.ModelConnection;
import com.example.bestphotocollections.Profile.Activities.ShowProfile.ShowProfileActivity;
import com.example.bestphotocollections.R;
import com.example.bestphotocollections.showSelectedImg;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConnectionSubFragment extends Fragment implements Contract.View,AdapterConnections.onItemClickListener{

    @BindView(R.id.recyclerViewConnections)
    RecyclerView recyclerView;
    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout shimmerFrameLayout;
    AdapterConnections adapter;
    Contract.Presenter presenter;
    ArrayList<ModelConnection> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.connection_sub_frag,container,false);
        getActivity().setTitle("Your Connections");
        ButterKnife.bind(this,view);

        shimmerFrameLayout.startShimmer();
        presenter=new Presenter(ConnectionSubFragment.this);
        presenter.loadDataFromConnections();
        return  view;
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setRecyclerView(ArrayList<ModelConnection> list) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.hasFixedSize();
        adapter = new AdapterConnections(list,getContext());
        adapter.setItemClickListener(ConnectionSubFragment.this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void NotifyAdapter(ArrayList<ModelConnection> list_) {
        list=list_;
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void OnImgClick(int position) {
        Intent intent =  new Intent(getContext(), showSelectedImg.class);
        intent.putExtra("uri",list.get(position).getUri());
        intent.putExtra("title",list.get(position).getTitle());
        intent.putExtra("metadata",list.get(position).getMetadata());
        startActivity(intent);
    }

    @Override
    public void OnProfileImgClick(int position) {
        Intent intent = new Intent(getContext(), ShowProfileActivity.class);
        intent.putExtra("uid",list.get(position).getUid());
        intent.putExtra("name",list.get(position).getName());
        intent.putExtra("uri",list.get(position).getUri());
        startActivity(intent);
    }

    @Override
    public void OnLikeClick(int position, ImageButton btn) {
        presenter.setPicLiked(list.get(position).getUid(),list.get(position).getKey());
    }

    @Override
    public void OnDownloadClick(int position) {

    }

    @Override
    public void OnSharedClick(int position) {

    }
}
