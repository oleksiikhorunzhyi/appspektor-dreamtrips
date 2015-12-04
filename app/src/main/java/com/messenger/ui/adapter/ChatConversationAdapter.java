package com.messenger.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.InjectView;
import butterknife.ButterKnife;
import com.worldventures.dreamtrips.R;;
import com.messenger.model.ChatConversation;
import com.messenger.model.ChatMessage;

public class ChatConversationAdapter extends RecyclerView.Adapter<ChatConversationAdapter.ViewHolder> {

    private Context context;
    private ChatConversation chatConversation;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.chat_item_avatar) ImageView avatarImageView;
        @InjectView(R.id.chat_username) TextView nameTextView;
        @InjectView(R.id.chat_message) TextView messageTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public ChatConversationAdapter(Context context) {
        this.context = context;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat, parent, false);
        return new ViewHolder(itemRow);
    }

    @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
        ChatMessage chatMessage = chatConversation.getMessages().get(position);
        Picasso.with(context).load(chatMessage.getUser()
                .getAvatarUrl()).placeholder(android.R.drawable.ic_menu_compass)
                .into(holder.avatarImageView);
        holder.nameTextView.setText(chatMessage.getUser().getName());
        holder.messageTextView.setText(chatMessage.getMessage());
    }

    @Override public int getItemCount() {
        if (chatConversation == null) {
            return 0;
        }
        if (chatConversation.getMessages() == null || chatConversation.getMessages().isEmpty()) {
            return 0;
        }
        return chatConversation.getMessages().size();
    }

    public void setChatConversation(ChatConversation chatConversation) {
        this.chatConversation = chatConversation;
    }
}
