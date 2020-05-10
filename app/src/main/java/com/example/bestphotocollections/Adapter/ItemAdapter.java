package com.example.bestphotocollections.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bestphotocollections.Model.ItemData;
import com.example.bestphotocollections.R;
import com.example.bestphotocollections.showSelectedImg;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    Context context;
    ArrayList<ItemData> itemData;

    public ItemAdapter(Context context, ArrayList<ItemData> itemData) {
        this.context = context;
        this.itemData = itemData;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.title.setText(itemData.get(position).getMtitle());
        if(itemData.get(position).getmUri()!=null)
        Picasso.get().load(Uri.parse(itemData.get(position).getmUri())).placeholder(R.color.placeholder_bg).fit().into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(context, showSelectedImg.class);
                intent.putExtra("uri",itemData.get(position).getmUri());
                intent.putExtra("title",itemData.get(position).getMtitle());
                intent.putExtra("metadata",itemData.get(position).getmMatadata());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (itemData!= null? itemData.size() : 0);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView title;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView= itemView.findViewById(R.id.img);
            title = itemView.findViewById(R.id.text_title);

        }
    }
}
