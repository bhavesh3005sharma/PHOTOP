package com.example.bestphotocollections.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bestphotocollections.Model.ModelMessagingPersons;
import com.example.bestphotocollections.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterChatsActivity extends RecyclerView.Adapter<AdapterChatsActivity.viewHolder> {
    Context context;
    ArrayList<ModelMessagingPersons> list;
    onClickListener mListener;

    public AdapterChatsActivity(Context context, ArrayList<ModelMessagingPersons> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_chats,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.name.setText(list.get(position).getName());
        Picasso.get().load(list.get(position).getProfileUrl()).placeholder(R.drawable.ic_profile).into(holder.profileImg);
    }

    @Override
    public int getItemCount() {
        return (list!= null? list.size() : 0);
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        private TextView name,lastMessage;
        private CircularImageView profileImg;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            profileImg = itemView.findViewById(R.id.profileImage);
            profileImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.checkProfilePic(getAdapterPosition());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.itemClick(getAdapterPosition());
                }
            });
        }
    }

   public void setOnClickListener(onClickListener listener){
        mListener = listener;
   }

    public interface onClickListener{
        void checkProfilePic(int position);
        void itemClick(int position);
    }
}
