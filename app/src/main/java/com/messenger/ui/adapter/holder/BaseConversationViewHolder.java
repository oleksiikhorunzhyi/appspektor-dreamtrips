package com.messenger.ui.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BaseConversationViewHolder extends RecyclerView.ViewHolder {

    @InjectView(R.id.conversation_name_textview)
    TextView nameTextView;
    @InjectView(R.id.conversation_last_message_textview) TextView lastMessageTextView;
    @InjectView(R.id.conversation_last_message_date_textview) TextView lastMessageDateTextView;
    @InjectView(R.id.conversation_unread_messages_count_textview) TextView unreadMessagesCountTextView;

    public BaseConversationViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }

    public TextView getNameTextView() {
        return nameTextView;
    }

    public TextView getLastMessageTextView() {
        return lastMessageTextView;
    }

    public TextView getLastMessageDateTextView() {
        return lastMessageDateTextView;
    }

    public TextView getUnreadMessagesCountTextView() {
        return unreadMessagesCountTextView;
    }

}
