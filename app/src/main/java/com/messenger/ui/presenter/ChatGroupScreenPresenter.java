package com.messenger.ui.presenter;

import android.content.Intent;

import com.messenger.messengerservers.ChatManager;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.ui.activity.ChatActivity;

public class ChatGroupScreenPresenter extends ChatScreenPresenterImpl {
    public ChatGroupScreenPresenter(Intent startIntent) {
        super(startIntent);
    }

    @Override
    protected Chat createChat(ChatManager chatManager) {
        return chatManager.createMultiUserChat(getUser(), startIntent.getStringExtra(ChatActivity.EXTRA_CHAT_CONVERSATION_ID));
    }

}
