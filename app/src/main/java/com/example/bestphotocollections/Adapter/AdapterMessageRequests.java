package com.example.bestphotocollections.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bestphotocollections.Model.ModelConnection;
import com.example.bestphotocollections.R;
import com.example.bestphotocollections.Utilities.MessageRequestHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class AdapterMessageRequests extends RecyclerView.Adapter<AdapterMessageRequests.viewHolder> {
    ArrayList<ModelConnection> list;
    Context context;

    public AdapterMessageRequests(ArrayList<ModelConnection> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_request, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        ModelConnection model = list.get(position);
        holder.name.setText(model.getName());
        if (model.getProfileImageUri()!=null)
        Picasso.get().load(Uri.parse(model.getProfileImageUri())).placeholder(R.drawable.ic_profile).into(holder.profileImg);

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getUid()!=null)
                MessageRequestHandler.acceptRequest(FirebaseAuth.getInstance().getUid(),model.getUid());
                list.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getUid()!=null)
                MessageRequestHandler.cancelRequest(FirebaseAuth.getInstance().getUid(),model.getUid());
                list.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {return (list!= null? list.size() : 0); }

    public class viewHolder extends RecyclerView.ViewHolder{
        CircularImageView profileImg;
        TextView name;
        Button accept , reject;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profileImage);
            accept = itemView.findViewById(R.id.accept);
            reject = itemView.findViewById(R.id.reject);
            name = itemView.findViewById(R.id.name);
        }
    }
}
