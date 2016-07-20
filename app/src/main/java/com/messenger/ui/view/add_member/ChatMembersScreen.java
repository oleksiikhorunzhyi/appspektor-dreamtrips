package com.messenger.ui.view.add_member;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.messenger.entities.DataUser;
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

    void setChosenUsers(List<DataUser> users);

    void setConversationNameEditTextVisibility(int visibility);

    void slideInConversationNameEditText();

    void slideOutConversationNameEditText();

    @NonNull String getConversationName();

    Observable<CharSequence> getSearchQueryObservable();

    Observable<DataUser> getRemovedUserObservable();

    void setSearchQuery(@Nullable CharSequence query);
}
