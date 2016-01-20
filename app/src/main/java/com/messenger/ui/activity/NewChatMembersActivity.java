package com.messenger.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.messenger.ui.view.AddChatMembersScreenImpl;
import com.messenger.ui.view.NewChatMembersScreenImpl;
import com.messenger.ui.view.ChatMembersScreenImpl;


public class NewChatMembersActivity extends BaseMvpViewActivity {

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
    ChatMembersScreenImpl createScreen() {
        int mode = getIntent().getIntExtra(NewChatMembersActivity.EXTRA_MODE, -1);
        if (mode == MODE_NEW_CHAT) {
            return new NewChatMembersScreenImpl(this);
        } else if (mode == MODE_CHAT_ADD_MEMBERS) {
            return new AddChatMembersScreenImpl(this,
                    getIntent().getStringExtra(EXTRA_CONVERSATION_ID));
        }
        throw new IllegalArgumentException("No view for this type");
    }
}
