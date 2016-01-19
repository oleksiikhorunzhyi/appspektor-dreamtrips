package com.messenger.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.messenger.ui.view.EditChatMembersScreenImpl;

public class EditChatMembersActivity extends BaseMvpViewActivity {

    public static final String EXTRA_CONVERSATION_ID = "EXTRA_CONVERSATION_ID";

    public static void start(Context context, String conversationId) {
        Intent intent = new Intent(context, EditChatMembersActivity.class);
        intent.putExtra(EXTRA_CONVERSATION_ID, conversationId);
        context.startActivity(intent);
    }

    @Override
    EditChatMembersScreenImpl createScreen() {
        return new EditChatMembersScreenImpl(this);
    }
}
