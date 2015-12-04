package com.messenger.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.ui.view.ActivityAwareScreen;
import com.messenger.ui.view.NewChatScreenImpl;

public class MessengerStartActivity extends AppCompatActivity {

    private ActivityAwareScreen screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NewChatScreenImpl newChatScreen = new NewChatScreenImpl(this);
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
}
