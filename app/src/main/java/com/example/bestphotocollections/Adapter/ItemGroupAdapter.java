package com.example.bestphotocollections.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bestphotocollections.Model.ItemData;
import com.example.bestphotocollections.Model.ItemGroup;
import com.example.bestphotocollections.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ItemGroupAdapter extends RecyclerView.Adapter<ItemGroupAdapter.itemGroupViewholder> implements Filterable {

    private Context context;
    private ArrayList<ItemGroup> datalist;
    private ArrayList<ItemGroup> ItemGroupList;
    DatabaseReference databaseReference;
    String uidFollower;
    String uidFollowing;

    public ItemGroupAdapter(Context context, ArrayList<ItemGroup> datalist) {
        this.context = context;
        this.datalist = datalist;
        this.ItemGroupList = datalist;
    }

    @NonNull
    @Override
    public itemGroupViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_group,parent,false);
        return new itemGroupViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final itemGroupViewholder holder, final int position) {
        uidFollower = FirebaseAuth.getInstance().getUid();
        uidFollowing = datalist.get(position).getUid();

        holder.progressbar.setVisibility(View.VISIBLE);
        holder.text_name.setText(datalist.get(position).getName());
        if (datalist.get(position).getMetadata()!=null) {
            holder.text_bio.setVisibility(View.VISIBLE);
            holder.text_bio.setText(datalist.get(position).getMetadata());
        }
        if (datalist.get(position).getUri()!=null)
            Picasso.get().load(datalist.get(position).getUri()).placeholder(R.drawable.ic_profile).into(holder.profileImg);

        ArrayList<ItemData> item_list = datalist.get(position).getItem_list();
        ItemAdapter itemAdapter = new ItemAdapter(context,item_list);
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        holder.recyclerView.setAdapter(itemAdapter);
        holder.recyclerView.setNestedScrollingEnabled(false);



        Log.d("AdapterunUid",uidFollowing+"****"+position);

        switch (datalist.get(position).getBtnFollowCondition()){
            case 1:
                holder.btnFollow.setText("Follow Back");
                holder.btnFollow.setBackgroundColor(Color.GREEN);
                holder.btnFollow.setBackgroundResource(R.drawable.button_background);
                Log.d("FollowersAdapter**", "entered");
                break;
            case 2:
                holder.btnFollow.setText("UnFollow");
                holder.btnFollow.setBackgroundColor(Color.BLUE);
                holder.btnFollow.setBackgroundResource(R.drawable.button_background);
                Log.d("FollowingssAdapter**", "entered");
                break;
            case 3:
                Log.d("mhihu**", "entered");
                holder.btnFollow.setVisibility(View.GONE);
                break;
        }

        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"You started following "+datalist.get(position).getName(),Toast.LENGTH_SHORT).show();
                Log.d("bnmscjblisdu","uidGet * "+datalist.get(position).getUid());
                addFollower(uidFollower,datalist.get(position).getUid());
            }
        });
        holder.progressbar.setVisibility(View.GONE);

    }

    private void addFollower(String uidFollower, String uidFollowing) {
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads/"+ uidFollower);
        String key = databaseReference.push().getKey();
        databaseReference.child("Followings/"+key).setValue(uidFollowing);
        databaseReference.child("ChatsAvailable/"+key).setValue(uidFollowing);
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads/"+ uidFollowing);
        String key1 = databaseReference.push().getKey();
        databaseReference.child("Followers/"+key1).setValue(uidFollower);
        databaseReference.child("ChatsAvailable/"+key1).setValue(uidFollower);
    }

    @Override
    public int getItemCount() {
        return (datalist!= null? datalist.size() : 0);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    datalist = ItemGroupList;
                } else {
                    ArrayList<ItemGroup> filteredList = new ArrayList<>();
                    for (ItemGroup row : ItemGroupList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    datalist = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = datalist;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                datalist = (ArrayList<ItemGroup>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class itemGroupViewholder extends RecyclerView.ViewHolder{

        private TextView text_name,text_bio;
        private RecyclerView  recyclerView;
        private Button btnFollow;
        private CircularImageView profileImg;
        private ProgressBar progressbar;

        public itemGroupViewholder(@NonNull View itemView) {

            super(itemView);
            text_name = itemView.findViewById(R.id.text_Name);
            recyclerView = itemView.findViewById(R.id.recycler_view_item);
            progressbar = itemView.findViewById(R.id.progressBar);
            btnFollow = itemView.findViewById(R.id.btn_follow);
            text_bio = itemView.findViewById(R.id.text_bio);
            profileImg = itemView.findViewById(R.id.image_profile);
        }
    }
}
