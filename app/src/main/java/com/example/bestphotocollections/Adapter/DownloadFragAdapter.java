package com.example.bestphotocollections.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bestphotocollections.Model.ItemData;
import com.example.bestphotocollections.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DownloadFragAdapter extends RecyclerView.Adapter<DownloadFragAdapter.viewHolder> {
    ArrayList<ItemData> list;
    Context context;

    public DownloadFragAdapter(ArrayList<ItemData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_download, parent, false);
        Log.d("downloadAdptrCreateView","enter");
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Log.d("downloadAdptrBindView","enter");
        Log.d("dnlodAdptrBindViewList",list.toString());
        holder.title.setText(list.get(position).getMtitle());
        holder.metaData.setText(list.get(position).getmMatadata());
        Picasso.get().load(list.get(position).getmUri()).placeholder(R.color.placeholder_bg).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return (list!=null? list.size(): 0);
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        private TextView title,metaData;
        private ImageView img;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.text_title);
            metaData = itemView.findViewById(R.id.text_matadata);
            img = itemView.findViewById(R.id.img);
        }
    }
}
