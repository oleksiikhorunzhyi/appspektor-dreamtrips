package com.messenger.ui.view.edit_member;

import com.messenger.entities.DataUser;
import com.messenger.ui.view.layout.MessengerScreen;

import java.util.List;

import rx.Observable;

public interface EditChatMembersScreen extends MessengerScreen {

    void showLoading();

    void showContent();

    void showError(Throwable e);

    void setTitle(String title);

    void setAdapterData(List<Object> item);

    void invalidateAllSwipedLayouts();

    void showDeletionConfirmationDialog(DataUser user);

    void restoreSearchQuery(String string);

    Observable<CharSequence> getSearchObservable();
}
