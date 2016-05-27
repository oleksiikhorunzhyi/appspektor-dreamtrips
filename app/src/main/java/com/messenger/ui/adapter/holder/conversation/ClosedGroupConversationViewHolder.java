package com.messenger.ui.adapter.holder.conversation;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ClosedGroupConversationViewHolder extends GroupConversationViewHolder {

    private static final float CLOSED_CONVERSATION_ALPHA = 0.3f;

    @InjectView(R.id.conversation_last_message_date_textview)
    TextView lastMessageDateTextView;

    public ClosedGroupConversationViewHolder(View itemView) {
        super(itemView);
        itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.conversation_list_read_conversation_bg));
        groupAvatarsView.setAlpha(CLOSED_CONVERSATION_ALPHA);
        ViewGroup commonMessageViewsLayout = ButterKnife.findById(contentLayout, R.id.conversation_common_layout);
        for (int i = 0; i < commonMessageViewsLayout.getChildCount(); i++) {
            View child = commonMessageViewsLayout.getChildAt(i);
            if (child.getId() != R.id.conversation_last_messages_date_and_count_layout) {
                child.setAlpha(CLOSED_CONVERSATION_ALPHA);
            }
        }
    }

    @Override
    protected void updateUnreadCountTextView() {
        unreadMessagesCountTextView.setVisibility(View.GONE);
    }

    @Override
    protected void updateLastMessageDateTextView() {
        // use last message date text view as "Closed" label
        lastMessageDateTextView.setTextColor(ContextCompat.getColor(context,
                R.color.conversation_list_closed_conversation));
        lastMessageDateTextView.setText(R.string.conversation_list_abandoned);
    }
}
