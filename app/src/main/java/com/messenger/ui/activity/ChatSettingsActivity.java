package com.messenger.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.messenger.ui.view.ChatSettingsScreenImpl;
import com.messenger.ui.view.GroupChatSettingsScreenImpl;
import com.messenger.ui.view.SingleChatSettingsScreenImpl;
import com.messenger.ui.view.TripChatSettingsScreenImpl;

public class ChatSettingsActivity extends BaseMvpViewActivity {
    public static final String EXTRA_CHAT_CONVERSATION_ID = "ChatActivity#EXTRA_CHAT_CONVERSATION_ID";
    public static final String EXTRA_CHAT_TYPE = "ChatActivity#EXTRA_CHAT_TYPE";

    public static final int CHAT_TYPE_GROUP = 0xaa54;
    public static final int CHAT_TYPE_SINGLE = 0xaa55;
    public static final int CHAT_TYPE_TRIP = 0xaa56;

    public static void startGroupChatSettings(Context context, String conversationId) {
        Intent starter = new Intent(context, ChatSettingsActivity.class);
        starter.putExtra(EXTRA_CHAT_CONVERSATION_ID, conversationId);
        starter.putExtra(EXTRA_CHAT_TYPE, CHAT_TYPE_GROUP);
        context.startActivity(starter);
    }

    public static void startSingleChatSettings(Context context, @Nullable String conversationId) {
        Intent starter = new Intent(context, ChatSettingsActivity.class);
        starter.putExtra(EXTRA_CHAT_TYPE, CHAT_TYPE_SINGLE);
        starter.putExtra(EXTRA_CHAT_CONVERSATION_ID, conversationId);
        context.startActivity(starter);
    }

    @Override
    ChatSettingsScreenImpl createScreen() {
        int conversationType = getIntent().getIntExtra(EXTRA_CHAT_TYPE, -1);
        if (conversationType == CHAT_TYPE_SINGLE) {
            return new SingleChatSettingsScreenImpl(this);
        } else if (conversationType == CHAT_TYPE_GROUP) {
            return new GroupChatSettingsScreenImpl(this);
        } else if (conversationType == CHAT_TYPE_TRIP) {
            return new TripChatSettingsScreenImpl(this);
        }
        throw new IllegalStateException("No chat screen for this conversation type");
    }
}
