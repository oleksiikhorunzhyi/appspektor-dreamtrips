package com.messenger.ui.presenter;

import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.view.ChatScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;

public interface ChatScreenPresenter extends MessengerPresenter<ChatScreen, ChatLayoutViewState> {
    boolean sendMessage(String message);
    void retrySendMessage(String messageId);

    User getUser();

    void onNextPageReached();

    void firstVisibleMessageChanged(Message firstVisibleMessage);

    void messageTextChanged(int length);

    void openUserProfile(User user);
}

