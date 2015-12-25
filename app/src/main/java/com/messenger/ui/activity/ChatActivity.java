package com.messenger.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.ui.view.ChatScreenImpl;

public class ChatActivity extends BaseMvpViewActivity<ChatScreenImpl> {
    public static final String EXTRA_CHAT_CONVERSATION_ID = "ChatActivity#EXTRA_CHAT_CONVERSATION_ID";
    public static final String EXTRA_CHAT_TYPE = "ChatActivity#EXTRA_CHAT_TYPE";

    public static final int CHAT_TYPE_GROUP = 0xaa54;
    public static final int CHAT_TYPE_SINGLE = 0xaa55;

    @IntDef({CHAT_TYPE_GROUP, CHAT_TYPE_SINGLE})
    public @interface ChatType {
    }

    public static void startChat(Context context, Conversation conversation) {
        int type = conversation.getType().equals(Conversation.Type.CHAT) ? CHAT_TYPE_SINGLE : CHAT_TYPE_GROUP;

        Intent starter = new Intent(context, ChatActivity.class);
        starter.putExtra(EXTRA_CHAT_TYPE, type);
        starter.putExtra(EXTRA_CHAT_CONVERSATION_ID, conversation.getId());
        starter.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(starter);
    }

    @Override
    ChatScreenImpl createScreen() {
        return new ChatScreenImpl(this);
    }
}
