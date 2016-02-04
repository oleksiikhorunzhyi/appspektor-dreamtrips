package com.messenger.ui.view.chat;

import android.database.Cursor;

import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.ui.view.layout.MessengerScreen;

import java.util.List;

import rx.Observable;

public interface ChatScreen extends MessengerScreen {

    void showLoading();

    void showContent();

    void showError(Throwable e);

    void setTitle(DataConversation conversation, List<DataUser> users);

    void showUnreadMessageCount(int unreadMessage);

    void hideUnreadMessageCount();

    void addTypingUser(DataUser user);

    void removeTypingUser(DataUser uxzser);

    void showMessages(Cursor cursor, DataConversation conversation, boolean pendingScroll);

    void smoothScrollToPosition(int position);

    boolean onBackPressed();

    int getFirstVisiblePosition();

    int getLastVisiblePosition();

    int getTotalShowingMessageCount();

    Observable<TextViewTextChangeEvent> getEditMessageObservable();

    Cursor getCurrentMessagesCursor();
}
