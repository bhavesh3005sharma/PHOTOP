package com.example.bestphotocollections.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bestphotocollections.Model.ModelConnection;
import com.example.bestphotocollections.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterConnections extends RecyclerView.Adapter<AdapterConnections.viewHolder> {
    ArrayList<ModelConnection> list;
    Context context;

    public AdapterConnections(ArrayList<ModelConnection> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_connection, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        ModelConnection model = list.get(position);
        Log.d("listReceivedInAdapter",list.toString());
        Log.d("DataAdapter",model.getName()+"*"+model.getTitle()+"*"+model.getUri());
        if(model.getName()!=null)
        holder.name.setText(model.getName());
        if(model.getTitle()!=null)
        holder.title.setText(model.getTitle());
        if(model.getUri()!=null)
        Picasso.get().load(Uri.parse(model.getUri())).placeholder(R.color.placeholder_bg).into(holder.img);
        if(model.getProfileImageUri()!=null)
            Picasso.get().load(Uri.parse(model.getProfileImageUri())).placeholder(R.drawable.ic_profile).into(holder.profileImg);
    }

    @Override
    public int getItemCount(){return (list!= null? list.size() : 0);
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        private CircularImageView profileImg;
        private TextView name,title;
        private ImageView img;
        private ImageButton like,download,share;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameConnection);
            title =itemView.findViewById(R.id.title);
            profileImg = itemView.findViewById(R.id.profile_image);
            img= itemView.findViewById(R.id.img);
            like = itemView.findViewById(R.id.like);
            download = itemView.findViewById(R.id.download);
            share = itemView.findViewById(R.id.share);;
        }
    }
}
