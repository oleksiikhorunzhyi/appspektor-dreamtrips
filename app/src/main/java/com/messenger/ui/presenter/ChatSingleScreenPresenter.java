package com.messenger.ui.presenter;

import android.content.Context;
import android.content.Intent;

import com.messenger.messengerservers.ChatManager;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;

public class ChatSingleScreenPresenter extends ChatScreenPresenterImpl {

    public ChatSingleScreenPresenter(Context context, Intent startIntent) {
        super(context,startIntent);
    }

    @Override
    protected Chat createChat(ChatManager chatManager, Conversation conversation) {
        for (User user : conversation.getParticipants()) {
            if (user.getId().equals(this.user.getId()) ) {
                // TODO: 12/18/15 remove after testing
                continue;
                //throw new Error("Ups! You cannot create chat with yourself.");
            }
            return chatManager.createSingleUserChat(user.getId(),
                    conversation.getId());
        }
        throw new Error("Ups! Creating chat error.");
    }
}
