package com.messenger.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.entities.User;
import com.messenger.model.ChatConversation;
import com.messenger.ui.view.ActivityAwareScreen;
import com.messenger.ui.view.ChatScreenImpl;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import javax.inject.Inject;

public class ChatActivity extends AppCompatActivity {
    private static final String EXTRA_CONVERSATION = "ChatActivity#EXTRA_CONVERSATION";
    private static final String EXTRA_CHAT_TYPE = "ChatActivity#EXTRA_CHAT_TYPE";

    public static final int CHAT_TYPE_GROUP = 0xaa54;
    public static final int CHAT_TYPE_SINGLE = 0xaa55;


    @Inject
    MessengerServerFacade messengerServerFacade;

    private Chat chat;
    private int chatType;

    @IntDef({CHAT_TYPE_GROUP, CHAT_TYPE_SINGLE})
    public @interface ChatType {
    }

    private ActivityAwareScreen screen;

    public static void start(Context context, @ChatType int chatType, ChatConversation conversation) {
        Intent starter = new Intent(context, ChatActivity.class);
        starter.putExtra(EXTRA_CHAT_TYPE, chatType);
        starter.putExtra(EXTRA_CONVERSATION, conversation);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((Injector) getApplication()).inject(this);

        ChatScreenImpl screen = new ChatScreenImpl(this);
        ChatConversation chatConversation = getIntent().getParcelableExtra(EXTRA_CONVERSATION);
        screen.setChatConversation(chatConversation);
        screen.setId(android.R.id.primary);
        setContentView(screen);
        this.screen = screen;

        Intent intent = getIntent();
        if ((chatType = intent.getIntExtra(EXTRA_CHAT_TYPE, -1)) == -1) {
            throw new IllegalArgumentException();
        } else if (chatType == CHAT_TYPE_GROUP) {
            chat = messengerServerFacade.createMultiUserChat(null);
        } else {
            //todo crautch
            chat = messengerServerFacade.createSingleUserChat((User) chatConversation.getChatUsers().get(0));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return screen.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return screen.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        screen.onActivityResult(requestCode, resultCode, data);
    }

    public Chat getChat() {
        return chat;
    }
}
