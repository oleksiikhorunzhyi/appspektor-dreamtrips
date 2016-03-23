package com.messenger.ui.view.chat;

import android.database.Cursor;
import android.view.Menu;

import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.ui.view.layout.MessengerScreen;

import java.util.List;

import rx.Observable;

public interface ChatScreen extends MessengerScreen {

    void showLoading();

    void showContent();

    void showError(Throwable e);

    void setTitle(DataConversation conversation, List<DataUser> users);

    void addTypingUser(DataUser user);

    void setShowMarkUnreadMessage(boolean needShow);

    void removeTypingUser(DataUser uxzser);

    void removeAllTypingUsers();

    void showMessages(Cursor cursor, DataConversation conversation);

    void hidePicker();

    Observable<TextViewTextChangeEvent> getEditMessageObservable();

    void showContextualAction(Menu menu, DataMessage message);

    void enableSendMessageButton(boolean enable);
}
