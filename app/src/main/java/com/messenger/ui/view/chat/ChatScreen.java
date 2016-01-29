package com.messenger.ui.view.chat;

import android.database.Cursor;

import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.messenger.entities.Conversation;
import com.messenger.entities.User;
import com.messenger.ui.view.layout.MessengerScreen;

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

    boolean onBackPressed();

    int getFirstVisiblePosition();

    int getLastVisiblePosition();

    int getTotalShowingMessageCount();

    Observable<TextViewTextChangeEvent> getEditMessageObservable();

    Cursor getCurrentMessagesCursor();
}
