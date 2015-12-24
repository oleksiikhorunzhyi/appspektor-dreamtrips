package com.messenger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.ui.view.ActivityAwareScreen;
import com.messenger.ui.view.ConversationListScreenImpl;

public class MessengerStartActivity extends BaseMvpViewActivity<ConversationListScreenImpl> {
    public static final String EXTRA_CONVERSATION_ID = "EXTRA_CONVERSATION_ID";
    public static final String EXTRA_CONVERSATION_TYPE = "EXTRA_CONVERSATION_TYPE";

    @Override
    ConversationListScreenImpl createScreen() {
        return new ConversationListScreenImpl(this);
    }
}
