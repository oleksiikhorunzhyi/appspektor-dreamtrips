package com.messenger.ui.adapter.holder.conversation;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class ClosedGroupConversationViewHolder extends GroupConversationViewHolder {

    @InjectView(R.id.conversation_last_message_date_textview)
    TextView lastMessageDateTextView;

    public ClosedGroupConversationViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateUnreadCountTextView() {
    }

    @Override
    protected void updateLastMessageDateTextView() {
        lastMessageDateTextView.setText(R.string.conversation_list_abandoned);
    }

    @Override
    public void applySelection(String selectedConversationId) {
        contentLayout.setBackgroundColor(ContextCompat.getColor(context,
                R.color.conversation_list_read_conversation_bg));
    }
}
