package com.messenger.ui.activity;

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

public class ChatActivity extends AppCompatActivity {
    public static final String EXTRA_CHAT_COMPANION_ID = "ChatActivity#EXTRA_CHAT_COMPANION_ID";
    public static final String EXTRA_CHAT_CONVERSATION_ID = "ChatActivity#EXTRA_CHAT_CONVERSATION_ID";
    public static final String EXTRA_CHAT_TYPE = "ChatActivity#EXTRA_CHAT_TYPE";

    private ActivityAwareScreen screen;


    public static final int CHAT_TYPE_GROUP = 0xaa54;
    public static final int CHAT_TYPE_SINGLE = 0xaa55;

    @IntDef({CHAT_TYPE_GROUP, CHAT_TYPE_SINGLE})
    public @interface ChatType {
    }

    public static void startGroup(Context context, String conversationId) {
        Intent starter = new Intent(context, ChatActivity.class);
        starter.putExtra(EXTRA_CHAT_CONVERSATION_ID, conversationId);
        starter.putExtra(EXTRA_CHAT_TYPE, CHAT_TYPE_GROUP);
        context.startActivity(starter);
    }

    public static void start(Context context, @Nullable String conversationId, @Nullable String companionId) {
        Intent starter = new Intent(context, ChatActivity.class);
        starter.putExtra(EXTRA_CHAT_COMPANION_ID, companionId);
        starter.putExtra(EXTRA_CHAT_TYPE, CHAT_TYPE_SINGLE);
        starter.putExtra(EXTRA_CHAT_CONVERSATION_ID, conversationId);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatScreenImpl screen = new ChatScreenImpl(this);
        screen.setId(android.R.id.primary);
        setContentView(screen);
        this.screen = screen;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return screen.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return screen.onOptionsItemSelected(item);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        screen.onActivityResult(requestCode, resultCode, data);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        screen.onDestroy();
    }
}
