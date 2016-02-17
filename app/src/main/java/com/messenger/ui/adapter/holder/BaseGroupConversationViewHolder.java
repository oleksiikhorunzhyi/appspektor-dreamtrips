package com.messenger.ui.adapter.holder;

import android.support.annotation.NonNull;
import android.view.View;

import com.messenger.entities.DataConversation;

public abstract class BaseGroupConversationViewHolder extends BaseConversationViewHolder {

    public BaseGroupConversationViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindConversation(@NonNull DataConversation conversation, String selectedConversationId) {
        super.bindConversation(conversation, selectedConversationId);
        nameTextView.setText(conversation.getSubject());
    }
}
