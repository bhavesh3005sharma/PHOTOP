package com.example.bestphotocollections.MessagingActivities.ChatActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bestphotocollections.Adapter.AdapterChatsActivity;
import com.example.bestphotocollections.MessagingActivities.DirectMessage.DirectMessageActivity;
import com.example.bestphotocollections.Model.ModelMessagingPersons;
import com.example.bestphotocollections.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity implements Contracter.mainView,AdapterChatsActivity.onClickListener{

    @BindView(R.id.progressBarChatsActivity)
    ProgressBar progressBar;
    @BindView(R.id.textInfo)
    TextView info;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    AdapterChatsActivity adapter;
    Presenter presenter;
    ArrayList<ModelMessagingPersons> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        presenter = new Presenter(ChatActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setTitle("Direct Messages");
       // loadFromShredPref();
        presenter.loadContactList();
        progressBar.setVisibility(View.VISIBLE);
        info.setVisibility(View.VISIBLE);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setRecyclerView(ArrayList<ModelMessagingPersons> list) {
        chatList = list;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new AdapterChatsActivity(this,list);
        adapter.setOnClickListener(ChatActivity.this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void saveToShredPref(ArrayList<ModelMessagingPersons> list) {
            SharedPreferences sharedPreferences = getSharedPreferences("shared_pref_connections_list", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(list);
            editor.putString("list", json);
            editor.apply();
            Log.d("shredPrefListStored",""+list.toString());

        progressBar.setVisibility(View.GONE);
        if (list.size()==0)
            info.setText("Sorry You don't have any Contacts.\n Make Contacts First.");
        else
            info.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
    }

//    private void loadFromShredPref() {
//        SharedPreferences sharedPreferences = getSharedPreferences("shared_pref_connections_list", MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = sharedPreferences.getString("list", null);
//        Type type = new TypeToken<ArrayList<ModelMessagingPersons>>() {}.getType();
//        chatList = gson.fromJson(json, type);
//
//        if (chatList == null) {
//            chatList = new ArrayList<>();
//        }
//    }

    @Override
    public void checkProfilePic(int position) {

    }

    @Override
    public void itemClick(int position) {
        Intent intent = new Intent(ChatActivity.this, DirectMessageActivity.class);
        intent.putExtra("uid",chatList.get(position).getUid());
        intent.putExtra("profilePicUri",chatList.get(position).getProfileUrl());
        intent.putExtra("name",chatList.get(position).getName());
        startActivity(intent);
    }
}
