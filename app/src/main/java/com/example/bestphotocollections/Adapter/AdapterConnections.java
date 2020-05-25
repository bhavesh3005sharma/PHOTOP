package com.example.bestphotocollections.Adapter;

import android.content.Context;
import android.net.Uri;
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

public class AdapterConnections extends RecyclerView.Adapter<AdapterConnections.viewHolder>{
    ArrayList<ModelConnection> list;
    Context context;
    onItemClickListener mListener;

    public void setItemClickListener(onItemClickListener mListener) {
        this.mListener = mListener;
    }

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
        if(model.getName()!=null)
        holder.name.setText(model.getName());
        if(model.getTitle()!=null)
        holder.title.setText(model.getTitle());
        if(model.getUri()!=null)
            Picasso.get().load(Uri.parse(model.getUri())).placeholder(R.color.placeholder_bg).into(holder.img);
        if(model.getProfileImageUri()!=null)
            Picasso.get().load(Uri.parse(model.getProfileImageUri())).placeholder(R.drawable.ic_profile).into(holder.profileImg);
        if (model.isLiked())
            holder.like.setImageResource(R.drawable.ic_liked);
        else
            holder.like.setImageResource(R.drawable.ic_not_like);
    }

    @Override
    public int getItemCount(){return (list!= null? list.size() : 0);
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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
            share = itemView.findViewById(R.id.share);

            like.setOnClickListener(this);
            download.setOnClickListener(this);
            share.setOnClickListener(this);
            img.setOnClickListener(this);
            profileImg.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(mListener!=null) {
                switch (v.getId()) {
                    case R.id.like:
                        mListener.OnLikeClick(getAdapterPosition(),like);
                        break;
                    case R.id.profile_image:
                        mListener.OnProfileImgClick(getAdapterPosition());
                        break;
                    case R.id.img:
                        mListener.OnImgClick(getAdapterPosition());
                        break;
                    case R.id.download:
                        mListener.OnDownloadClick(getAdapterPosition());
                        break;
                    case R.id.share:
                        mListener.OnSharedClick(getAdapterPosition());
                        break;
                }
            }
        }
    }

    public interface onItemClickListener{
        void OnImgClick(int position);
        void OnProfileImgClick(int position);
        void OnLikeClick(int position, ImageButton likeBtn);
        void OnDownloadClick(int position);
        void OnSharedClick(int position);
    }
}
