package com.messenger.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.messenger.ui.view.NewChatMembersScreenImpl;


public class NewChatMembersActivity extends BaseMvpViewActivity<NewChatMembersScreenImpl> {

    public static final String EXTRA_MODE = "EXTRA_MODE";
    public static final String EXTRA_CONVERSATION_ID = "EXTRA_CONVERSATION_ID";

    public static final int MODE_NEW_CHAT = 1;
    public static final int MODE_CHAT_ADD_MEMBERS = 2;

    public static void startInNewChatMode(Context context) {
        Intent intent = new Intent(context, NewChatMembersActivity.class);
        intent.putExtra(EXTRA_MODE, MODE_NEW_CHAT);
        context.startActivity(intent);
    }

    public static void startInAddMembersMode(Context context, String conversationId) {
        Intent intent = new Intent(context, NewChatMembersActivity.class);
        intent.putExtra(EXTRA_MODE, MODE_CHAT_ADD_MEMBERS);
        intent.putExtra(EXTRA_CONVERSATION_ID, conversationId);
        context.startActivity(intent);
    }

    @Override
    NewChatMembersScreenImpl createScreen() {
        return new NewChatMembersScreenImpl(this);
    }
}
