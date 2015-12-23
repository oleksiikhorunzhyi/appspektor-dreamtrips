package com.messenger.ui.presenter;

import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.view.ChatScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;

public interface ChatScreenPresenter extends ActivityAwareViewStateMvpPresenter<ChatScreen, ChatLayoutViewState> {
    boolean onNewMessageFromUi(String message);

    User getUser();

    void onNextPageReached();

    void firstVisibleMessageChanged(Message firstVisibleMessage);
}

