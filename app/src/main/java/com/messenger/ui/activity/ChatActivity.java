package com.messenger.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.ui.view.chat.ChatScreenImpl;

@Deprecated
public class ChatActivity extends BaseMvpViewActivity {

    public static void startChat(Context context, Conversation conversation) {
        Intent starter = new Intent(context, ChatActivity.class);
        starter.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(starter);
    }

    @Override
    ChatScreenImpl createScreen() {
        return new ChatScreenImpl(this);
    }
}
