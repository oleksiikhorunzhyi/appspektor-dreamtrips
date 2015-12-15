package com.messenger.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.model.ChatConversation;
import com.messenger.ui.presenter.ChatScreenPresenter;
import com.messenger.ui.view.ActivityAwareScreen;
import com.messenger.ui.view.ChatScreenImpl;

public class ChatActivity extends AppCompatActivity {

    private ActivityAwareScreen screen;

    public static void start(Context context) {
        Intent starter = new Intent(context, ChatActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatScreenImpl screen = new ChatScreenImpl(this);
        ChatConversation chatConversation = getIntent().getParcelableExtra(ChatScreenPresenter.EXTRA_CHAT_CONVERSATION);
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

    @Override protected void onDestroy() {
        super.onDestroy();
        screen.onDestroy();
    }
}
