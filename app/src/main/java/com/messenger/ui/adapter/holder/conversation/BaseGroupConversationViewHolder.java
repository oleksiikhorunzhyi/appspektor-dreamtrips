package com.messenger.ui.adapter.holder.conversation;

import android.text.TextUtils;
import android.view.View;

import com.messenger.entities.DataConversation;
import com.messenger.ui.adapter.holder.conversation.BaseConversationViewHolder;
import com.messenger.ui.helper.ConversationHelper;

public abstract class BaseGroupConversationViewHolder extends BaseConversationViewHolder {

    private ConversationHelper conversationHelper = new ConversationHelper();

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
        conversationHelper.setGroupChatTitle(nameTextView, conversationName, participantsCount);
    }
}
