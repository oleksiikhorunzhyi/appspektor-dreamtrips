package com.messenger.ui.view;

import android.database.Cursor;

import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;

import java.util.List;

import rx.Observable;

public interface ChatScreen extends MessengerScreen {

    void showLoading();

    void showContent();

    void showError(Throwable e);

    void setTitle(Conversation conversation, List<User> users);

    void showUnreadMessageCount(int unreadMessage);

    void hideUnreadMessageCount();

    void addTypingUser(User user);

    void removeTypingUser(User uxzser);

    void showMessages(Cursor cursor, Conversation conversation, boolean pendingScroll);

    void smoothScrollToPosition(int position);

    int getFirstVisiblePosition();

    int getLastVisiblePosition();

    int getTotalShowingMessageCount();

    Observable<TextViewTextChangeEvent> getEditMessageObservable();

    Cursor getCurrentMessagesCursor();
}
