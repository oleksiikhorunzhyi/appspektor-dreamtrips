package com.messenger.ui.presenter;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.messenger.entities.DataUser;
import com.messenger.ui.view.chat.ChatScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;

import java.util.List;

public interface ChatScreenPresenter extends MessengerPresenter<ChatScreen, ChatLayoutViewState> {
    boolean sendMessage(String message);

    void retrySendMessage(String messageId);

    DataUser getUser();

    void onNextPageReached();

    void onLastVisibleMessageChanged(int position);

    void openUserProfile(DataUser user);

    void onUnreadMessagesHeaderClicked();

    void onImagesPicked(List<ChosenImage>images);
}

