package com.messenger.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.messenger.messengerservers.entities.Message;
import com.squareup.picasso.Picasso;
import com.worldventures.dreamtrips.R;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private final List<Message> messages = new CopyOnWriteArrayList<>();
    private Context context;
    private LayoutInflater layoutInflater;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.chat_item_avatar)
        ImageView avatarImageView;
        @InjectView(R.id.chat_username)
        TextView nameTextView;
        @InjectView(R.id.chat_message) TextView messageTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            final Context context = parent.getContext();
            layoutInflater = LayoutInflater.from(context);
            this.context = context;
        }
        return new ViewHolder(layoutInflater.inflate(R.layout.list_item_chat, parent, false));
    }

    @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
        Message chatMessage = messages.get(position);
        Picasso.with(context).load(android.R.drawable.ic_menu_compass)
                .placeholder(android.R.drawable.ic_menu_compass)
                .into(holder.avatarImageView);
        holder.nameTextView.setText(chatMessage.getFrom().getUserName());
        holder.messageTextView.setText(chatMessage.getText());
    }

    @Override public int getItemCount() {
        return messages.size();
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyDataSetChanged();
    }
}
