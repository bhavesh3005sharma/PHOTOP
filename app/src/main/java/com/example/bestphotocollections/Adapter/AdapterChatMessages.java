package com.example.bestphotocollections.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bestphotocollections.Model.ModelChatMessages;
import com.example.bestphotocollections.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterChatMessages extends RecyclerView.Adapter {
    public AdapterChatMessages(ArrayList<ModelChatMessages> list, Context context) {
        this.list = list;
        this.context = context;
    }

    ArrayList<ModelChatMessages> list;
    Context context;

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).isMyMessage())
            return 1;
        else
            return 2;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sent, parent, false);
            return new viewHolderSentMsgs(view);
        }
        else if(viewType==2){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received, parent, false);
            return new viewHolderReceivedMsgs(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModelChatMessages chatMessages = list.get(position);
        if(chatMessages.isMyMessage()){
            ((viewHolderSentMsgs)holder).messageBody.setText(chatMessages.getMessages());
            ((viewHolderSentMsgs)holder).time.setText(chatMessages.getTime());
            if (chatMessages.isDelivered()) {
                ((viewHolderSentMsgs) holder).msgStatus.setVisibility(View.GONE);
            }
        }
        else{
            ((viewHolderReceivedMsgs)holder).messageBody.setText(chatMessages.getMessages());
            ((viewHolderReceivedMsgs)holder).time.setText(chatMessages.getTime());
            ((viewHolderReceivedMsgs)holder).name.setText(chatMessages.getName());
            if(chatMessages.getProfileUri()!=null)
            Picasso.get().load(Uri.parse(chatMessages.getProfileUri())).placeholder(R.drawable.ic_profile)
                    .into(((viewHolderReceivedMsgs)holder).profileImg);
        }
    }

    @Override
    public int getItemCount() {
        return (list!= null? list.size() : 0);
    }

    public class viewHolderReceivedMsgs extends RecyclerView.ViewHolder{
        private ImageView profileImg;
        TextView name,messageBody,time;

        public viewHolderReceivedMsgs(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.image_message_profile);
            name = itemView.findViewById(R.id.text_message_name);
            time = itemView.findViewById(R.id.text_message_time);
            messageBody = itemView.findViewById(R.id.text_message_body);
        }
    }

    public class viewHolderSentMsgs extends RecyclerView.ViewHolder{
        TextView messageBody,time;
        ImageView msgStatus;

        public viewHolderSentMsgs(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.text_message_time);
            messageBody = itemView.findViewById(R.id.text_message_body);
            msgStatus = itemView.findViewById(R.id.msg_status);
        }
    }
}
