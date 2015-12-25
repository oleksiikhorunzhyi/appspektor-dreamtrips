package com.messenger.ui.activity;

import com.messenger.ui.view.ConversationListScreenImpl;

public class MessengerStartActivity extends BaseMvpViewActivity<ConversationListScreenImpl> {

    @Override
    ConversationListScreenImpl createScreen() {
        return new ConversationListScreenImpl(this);
    }
}
