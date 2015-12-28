package com.messenger.ui.presenter;

import android.content.Context;
import android.content.Intent;

import com.messenger.messengerservers.ChatManager;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.entities.Conversation;

public class ChatGroupScreenPresenter extends ChatScreenPresenterImpl {
    public ChatGroupScreenPresenter(Context context, Intent startIntent) {
        super(context, startIntent);
    }

    @Override
    protected Chat createChat(ChatManager chatManager, Conversation conversation) {
        boolean isOwner = conversation.getOwnerId().equals(user.getId());
        return chatManager.createMultiUserChat(conversation.getId(), getUser().getId(), isOwner);
    }

}
