package com.messenger.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.ui.view.ActivityAwareScreen;
import com.messenger.ui.view.ChatScreenImpl;

public class ChatActivity extends BaseMvpViewActivity<ChatScreenImpl> {
    public static final String EXTRA_CHAT_COMPANION_ID = "ChatActivity#EXTRA_CHAT_COMPANION_ID";
    public static final String EXTRA_CHAT_CONVERSATION_ID = "ChatActivity#EXTRA_CHAT_CONVERSATION_ID";
    public static final String EXTRA_CHAT_TYPE = "ChatActivity#EXTRA_CHAT_TYPE";

    public static final int CHAT_TYPE_GROUP = 0xaa54;
    public static final int CHAT_TYPE_SINGLE = 0xaa55;

    @IntDef({CHAT_TYPE_GROUP, CHAT_TYPE_SINGLE})
    public @interface ChatType {
    }

    public static void startGroupChat(Context context, String conversationId, int requestCode) {
        Intent starter = new Intent(context, ChatActivity.class);
        starter.putExtra(EXTRA_CHAT_CONVERSATION_ID, conversationId);
        starter.putExtra(EXTRA_CHAT_TYPE, CHAT_TYPE_GROUP);
        ((Activity) context).startActivityForResult(starter, requestCode);
    }

    public static void startSingleChat(Context context, @Nullable String conversationId, int requestCode) {
        Intent starter = new Intent(context, ChatActivity.class);
        starter.putExtra(EXTRA_CHAT_TYPE, CHAT_TYPE_SINGLE);
        starter.putExtra(EXTRA_CHAT_CONVERSATION_ID, conversationId);
        ((Activity) context).startActivityForResult(starter, requestCode);
    }

    @Override
    ChatScreenImpl createScreen() {
        return new ChatScreenImpl(this);
    }
}
