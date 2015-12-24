package com.messenger.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.messenger.ui.view.ChatSettingsScreenImpl;
import com.messenger.ui.view.GroupChatSettingsScreenImpl;
import com.messenger.ui.view.SingleChatSettingsScreenImpl;

public class ChatSettingsActivity extends BaseMvpViewActivity<ChatSettingsScreenImpl> {
    public static final String EXTRA_CHAT_CONVERSATION_ID = "ChatActivity#EXTRA_CHAT_CONVERSATION_ID";
    public static final String EXTRA_CHAT_TYPE = "ChatActivity#EXTRA_CHAT_TYPE";

    public static final int CHAT_TYPE_GROUP = 0xaa54;
    public static final int CHAT_TYPE_SINGLE = 0xaa55;

    public static void startGroupChatSettings(Activity activity, String conversationId) {
        Intent starter = new Intent(activity, ChatSettingsActivity.class);
        starter.putExtra(EXTRA_CHAT_CONVERSATION_ID, conversationId);
        starter.putExtra(EXTRA_CHAT_TYPE, CHAT_TYPE_GROUP);
        activity.startActivity(starter);
    }

    public static void startSingleChatSettings(Activity activity, @Nullable String conversationId) {
        Intent starter = new Intent(activity, ChatSettingsActivity.class);
        starter.putExtra(EXTRA_CHAT_TYPE, CHAT_TYPE_SINGLE);
        starter.putExtra(EXTRA_CHAT_CONVERSATION_ID, conversationId);
        activity.startActivity(starter);
    }

    @Override
    ChatSettingsScreenImpl createScreen() {
        int conversationType = getIntent().getIntExtra(EXTRA_CHAT_TYPE, -1);
        if (conversationType == CHAT_TYPE_SINGLE) {
            return new SingleChatSettingsScreenImpl(this);
        } else if (conversationType == CHAT_TYPE_GROUP) {
            return new GroupChatSettingsScreenImpl(this);
        }
        throw new IllegalStateException("No chat screen for this conversation type");
    }
}
