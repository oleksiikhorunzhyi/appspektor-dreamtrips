package com.messenger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.model.ChatConversation;
import com.messenger.ui.view.ActivityAwareScreen;
import com.messenger.ui.view.ChatScreenImpl;

public class ChatActivity extends AppCompatActivity {

    private ActivityAwareScreen screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatScreenImpl screen = new ChatScreenImpl(this);
        ChatConversation chatConversation = getIntent().getParcelableExtra("chat_conversation");
        screen.setChatConversation(chatConversation);
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
}
