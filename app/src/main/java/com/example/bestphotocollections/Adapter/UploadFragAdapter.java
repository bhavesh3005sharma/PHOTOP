package com.example.bestphotocollections.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bestphotocollections.R;
import com.example.bestphotocollections.Model.Upload;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UploadFragAdapter extends RecyclerView.Adapter<UploadFragAdapter.viewHolder> {
    private Context context;
    private ArrayList<Upload> list;
    private OnItemClickListener mListener;

    public UploadFragAdapter(Context context, ArrayList<Upload> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout_upload,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.text_title.setText(list.get(position).getMtitle());
        holder.text_matadata.setText(list.get(position).getmMatadata());
        Picasso.get().load(Uri.parse(list.get(position).getmUri())).placeholder(R.drawable.pic).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener ,
            MenuItem.OnMenuItemClickListener {
        private TextView text_title;
        private TextView text_matadata;
        private ImageView imageView;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            text_title = itemView.findViewById(R.id.text_title);
            text_matadata= itemView.findViewById(R.id.text_matadata);
            imageView = itemView.findViewById(R.id.image_uploaded);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem Details = menu.add(Menu.NONE, 1, 1, "Details");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");

            Details.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {
                        case 1:
                            mListener.ShowDetails(position);
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {

        void ShowDetails(int position);

        void onDeleteClick(int position);

        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
