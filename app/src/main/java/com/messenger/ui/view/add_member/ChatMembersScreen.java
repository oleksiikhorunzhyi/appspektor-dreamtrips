package com.messenger.ui.view.add_member;

import android.support.annotation.StringRes;

import com.messenger.ui.view.layout.MessengerScreen;

import java.util.List;

import rx.Observable;

public interface ChatMembersScreen extends MessengerScreen {
    void showLoading();

    void showContent();

    void showError(Throwable e);

    void setTitle(String title);

    void setTitle(@StringRes int title);

    void setAdapterItems(List<Object> items);

    void setSelectedUsersHeaderText(CharSequence text);

    void setConversationNameEditTextVisibility(int visibility);

    void slideInConversationNameEditText();

    void slideOutConversationNameEditText();

    String getConversationName();

    Observable<CharSequence> getSearchQueryObservable();
}
