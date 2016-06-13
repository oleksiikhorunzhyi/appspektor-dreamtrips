package com.messenger.ui.adapter.holder.conversation;

import android.text.TextUtils;
import android.view.View;

import com.messenger.entities.DataConversation;
import com.messenger.ui.helper.ConversationUIHelper;

public abstract class BaseGroupConversationViewHolder extends BaseConversationViewHolder {

    public BaseGroupConversationViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindConversation(DataConversation conversation, String participantsList, int participantsCount) {
        super.bindConversation(conversation, participantsList, participantsCount);
        String conversationName = conversation.getSubject();
        if (TextUtils.isEmpty(conversationName)) {
            conversationName = participantsList;
        }
        ConversationUIHelper.setGroupChatTitle(nameTextView, conversationName, participantsCount);
    }
}
