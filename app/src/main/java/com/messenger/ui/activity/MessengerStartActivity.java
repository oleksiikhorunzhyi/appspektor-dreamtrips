package com.messenger.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.messenger.ui.view.ConversationListScreenImpl;

public class MessengerStartActivity extends BaseMvpViewActivity {

    public static void start(Context context){
        Intent intent = new Intent(context, MessengerStartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    ConversationListScreenImpl createScreen() {
        return new ConversationListScreenImpl(this);
    }
}
