package com.example.minigpt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder>{
    List<Message> messageList;
    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,null);
        MyViewHolder myViewHolder = new MyViewHolder(chatView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = messageList.get(position);
        if(message.getSentBy().equals(Message.SENT_BY_ME)){
            holder.queryLeft.setVisibility(View.GONE);
            holder.replyRight.setVisibility(View.VISIBLE);
            holder.replytextView.setText(message.getMessage());
        }else{
            holder.replyRight.setVisibility(View.GONE);
            holder.queryLeft.setVisibility(View.VISIBLE);
            holder.querytextView.setText(message.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
    LinearLayout queryLeft, replyRight;
    TextView querytextView, replytextView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            queryLeft = itemView.findViewById(R.id.queryLeft);
            replyRight = itemView.findViewById(R.id.replyRight);
            querytextView = itemView.findViewById(R.id.query_textView);
            replytextView = itemView.findViewById(R.id.reply_textView);
        }

    }
}
