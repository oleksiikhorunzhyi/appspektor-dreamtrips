package com.messenger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.app.Environment;
import com.messenger.loader.LoaderModule;
import com.messenger.loader.SimpleLoader;
import com.messenger.model.ChatConversation;
import com.messenger.model.ChatUser;
import com.messenger.ui.presenter.ChatScreenPresenter;
import com.messenger.ui.view.ActivityAwareScreen;
import com.messenger.ui.view.ChatScreenImpl;

import java.util.List;


public class ChatActivity extends AppCompatActivity {

    private ActivityAwareScreen screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatScreenImpl screen = new ChatScreenImpl(this);
        ChatConversation chatConversation = getIntent().getParcelableExtra(ChatScreenPresenter.EXTRA_CHAT_CONVERSATION);
        screen.setChatConversation(chatConversation);
        screen.setId(android.R.id.primary);
        setContentView(screen);
        this.screen = screen;
        if (Environment.getEnvironment() == Environment.MOCK) {
            simulateUsersTyping();
        }
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

    // Mocking, delete later

    private int mockTypingCyclesCount = 0;
    private Handler mockHandler = new Handler();
    final SimpleLoader<List<ChatUser>> mockingLoader = LoaderModule.getChatContactsLoader();

    private void simulateUsersTyping() {
//        if (Environment.getEnvironment() == Environment.MOCK) {
//            mockHandler.postDelayed(new Runnable() {
//                @Override public void run() {
//                    mockTypingCyclesCount++;
//                    List<ChatUser> typingUsers = new ArrayList<>();
//                    int mockMessagesMaxCount = mockTypingCyclesCount % 5;
//                    for (int i = 0; i < mockMessagesMaxCount; i++) {
//                        typingUsers.add(mockingLoader.provideData().get(i));
//                    }
//                    EventBus.getDefault().post(new ChatUsersTypingEvent(typingUsers));
//                    mockHandler.postDelayed(this, 1000);
//                }
//            }, 1000);
//        }
    }


    @Override protected void onDestroy() {
        super.onDestroy();
        screen.onDestroy();
        mockHandler.removeCallbacksAndMessages(null);
    }
}
