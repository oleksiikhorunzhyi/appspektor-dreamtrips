package com.messenger.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.ui.view.ActivityAwareScreen;
import com.messenger.ui.view.NewChatMembersScreenImpl;


public class NewChatMembersActivity extends AppCompatActivity {

    public static final String EXTRA_MODE = "EXTRA_MODE";
    public static final String EXTRA_CONVERSATION_ID = "EXTRA_CONVERSATION_ID";

    public static final int MODE_NEW_CHAT = 1;
    public static final int MODE_CHAT_ADD_MEMBERS = 2;
    public static final int MODE_CHAT_EDIT_MEMBERS = 2;

    private ActivityAwareScreen screen;

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

    public static void startInEditMembersMode(Context context, String conversationId) {
        Intent intent = new Intent(context, NewChatMembersActivity.class);
        intent.putExtra(EXTRA_MODE, MODE_CHAT_EDIT_MEMBERS);
        intent.putExtra(EXTRA_CONVERSATION_ID, conversationId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NewChatMembersScreenImpl newChatScreen = new NewChatMembersScreenImpl(this);
        newChatScreen.setId(android.R.id.primary);
        setContentView(newChatScreen);
        screen = newChatScreen;
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
