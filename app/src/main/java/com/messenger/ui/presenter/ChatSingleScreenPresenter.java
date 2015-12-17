package com.messenger.ui.presenter;

import android.content.Context;
import android.content.Intent;

import com.messenger.messengerservers.ChatManager;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.activity.ChatActivity;

public class ChatSingleScreenPresenter extends ChatScreenPresenterImpl {

    public ChatSingleScreenPresenter(Context context, Intent startIntent) {
        super(context,startIntent);
    }

    @Override
    protected Chat createChat(ChatManager chatManager, Conversation conversation) {
        for (User user: conversation.getParticipants()) {
            if (user.getId().equals(this.user.getId()) ) continue;
            return chatManager.createSingleUserChat(user.getId(),
                    conversation.getId());
        }
        return null;
    }
}
