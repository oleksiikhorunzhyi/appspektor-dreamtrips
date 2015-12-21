package com.messenger.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.ui.view.ActivityAwareScreen;
import com.messenger.ui.view.ChatSettingsScreenImpl;
import com.messenger.ui.view.GroupChatSettingsScreenImpl;
import com.messenger.ui.view.SingleChatSettingsScreenImpl;

public class ChatSettingsActivity extends AppCompatActivity {

    public static final String EXTRA_CHAT_CONVERSATION_ID = "ChatActivity#EXTRA_CHAT_CONVERSATION_ID";
    public static final String EXTRA_CHAT_TYPE = "ChatActivity#EXTRA_CHAT_TYPE";

    public static final int CHAT_TYPE_GROUP = 0xaa54;
    public static final int CHAT_TYPE_SINGLE = 0xaa55;

    private ActivityAwareScreen screen;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int conversationType = getIntent().getIntExtra(EXTRA_CHAT_TYPE, -1);
        ChatSettingsScreenImpl chatSettingsScreen = null;
        if (conversationType == CHAT_TYPE_SINGLE) {
            chatSettingsScreen = new SingleChatSettingsScreenImpl(this);
        } else if (conversationType == CHAT_TYPE_GROUP) {
            chatSettingsScreen = new GroupChatSettingsScreenImpl(this);
        }
        if (chatSettingsScreen != null) {
            chatSettingsScreen.setId(android.R.id.primary);
            setContentView(chatSettingsScreen);
            this.screen = chatSettingsScreen;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return screen.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        screen.onPrepareOptionsMenu(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return screen.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        screen.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        screen.onDestroy();
    }
}
