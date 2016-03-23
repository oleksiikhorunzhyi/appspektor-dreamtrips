package com.messenger.ui.adapter.holder;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.messenger.entities.DataMessage;
import com.messenger.entities.DataTranslation;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class CloseGroupConversationViewHolder extends GroupConversationViewHolder {

    private static final float CLOSED_CONVERSATION_ALPHA = 0.3f;

    @InjectView(R.id.conversation_last_message_date_textview)
    TextView lastMessageDateTextView;

    public CloseGroupConversationViewHolder(View itemView) {
        super(itemView);
        itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.conversation_list_read_conversation_bg));
        for (int i = 0; i < contentLayout.getChildCount(); i++) {
            View child = contentLayout.getChildAt(i);
            if (child.getId() != R.id.conversation_last_messages_layout) {
                child.setAlpha(CLOSED_CONVERSATION_ALPHA);
            }
        }
        lastMessageDateTextView.setTextColor(ContextCompat.getColor(context,
                R.color.conversation_list_closed_conversation));
        lastMessageDateTextView.setText(R.string.conversation_list_abandoned);
        unreadMessagesCountTextView.setVisibility(View.GONE);
    }

    @Override
    public void bindLastMessage(DataMessage message, String messageAuthor,
                                String attachmentType, DataTranslation dataTranslation) {
        //nothing to here now
    }

    @Override
    protected void updateUnreadCountTextView() {
        //nothing to here now
    }
}
