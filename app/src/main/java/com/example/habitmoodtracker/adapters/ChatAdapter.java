package com.example.habitmoodtracker.adapters;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.habitmoodtracker.R;
import com.example.habitmoodtracker.models.ChatMessage;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;
        CardView cardView;
        LinearLayout messageContainer;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            cardView = itemView.findViewById(R.id.cardViewMessage);
            messageContainer = itemView.findViewById(R.id.messageContainer);
        }

        public void bind(ChatMessage message) {
            textViewMessage.setText(message.getMessage());

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) cardView.getLayoutParams();

            if (message.isUser()) {
                // User message - align right, blue background
                params.gravity = Gravity.END;
                cardView.setCardBackgroundColor(android.graphics.Color.parseColor("#6366F1"));
                textViewMessage.setTextColor(android.graphics.Color.WHITE);
            } else {
                // Bot message - align left, gray background
                params.gravity = Gravity.START;
                cardView.setCardBackgroundColor(android.graphics.Color.parseColor("#F3F4F6"));
                textViewMessage.setTextColor(android.graphics.Color.parseColor("#374151"));
            }

            cardView.setLayoutParams(params);
        }
    }
}