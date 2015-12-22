package com.messenger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.ui.view.ActivityAwareScreen;
import com.messenger.ui.view.ConversationListScreenImpl;

public class MessengerStartActivity extends BaseMvpViewActivity<ConversationListScreenImpl> {
    @Override
    ConversationListScreenImpl createScreen() {
        return new ConversationListScreenImpl(this);
    }
}
