package com.messenger.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.ButterKnife;
import com.worldventures.dreamtrips.R;
import com.messenger.model.ChatUser;

public class ChatContactsAdapter extends RecyclerView.Adapter<ChatContactsAdapter.ViewHolder> {

    public interface SelectionListener {
        void onSelectionStateChanged(List<ChatUser> selectedContacts);
    }

    private Context context;

    private List<ChatUser> chatContacts;
    private List<ChatUser> selectedContacts = new ArrayList<>();
    private SelectionListener selectionListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        @InjectView(R.id.contact_icon) ImageView iconImageView;
        @InjectView(R.id.contact_name_textview) TextView nameTextView;
        @InjectView(R.id.contact_chat_online_status_image_view) ImageView onlineStatusImageView;
        @InjectView(R.id.contact_chat_tick_image_view) ImageView tickImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            this.itemView = itemView;
        }
    }

    public ChatContactsAdapter(Context context) {
        this.context = context;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact, parent, false);
        return new ViewHolder(itemRow);
    }

    @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
        ChatUser user = chatContacts.get(position);
        holder.nameTextView.setText(user.getName());
        holder.onlineStatusImageView.setVisibility(user.isOnline() ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener((v) -> {
            ChatUser selectedUser = chatContacts.get(position);
            if (!selectedContacts.contains(selectedUser)) {
                selectedContacts.add(selectedUser);
            } else {
                selectedContacts.remove(selectedUser);
            }
            if (selectionListener != null) {
                selectionListener.onSelectionStateChanged(selectedContacts);
            }
            notifyDataSetChanged();
        });
        holder.tickImageView.setSelected(selectedContacts.contains(user));
        Picasso.with(context).load(user.getAvatarUrl()).placeholder(android.R.drawable.ic_menu_compass)
                .into(holder.iconImageView);
    }

    @Override public int getItemCount() {
        return chatContacts == null ? 0 : chatContacts.size();
    }

    public void setChatContacts(List<ChatUser> chatContacts) {
        this.chatContacts = chatContacts;
        notifyDataSetChanged();
    }

    public void setSelectedContacts(List<ChatUser> selectedContacts) {
        this.selectedContacts = selectedContacts;
        notifyDataSetChanged();
    }

    public void setSelectionListener(SelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }
}
