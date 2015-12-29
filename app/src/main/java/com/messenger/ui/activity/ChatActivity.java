package com.messenger.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.ui.view.ChatScreenImpl;

public class ChatActivity extends BaseMvpViewActivity<ChatScreenImpl> {

    public static final String EXTRA_CHAT_CONVERSATION_ID = "ChatActivity#EXTRA_CHAT_CONVERSATION_ID";

    public static void startChat(Context context, Conversation conversation) {
        Intent starter = new Intent(context, ChatActivity.class);
        starter.putExtra(EXTRA_CHAT_CONVERSATION_ID, conversation.getId());
        starter.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(starter);
    }

    @Override
    ChatScreenImpl createScreen() {
        return new ChatScreenImpl(this);
    }
}
