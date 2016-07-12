package com.messenger.ui.view.settings;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.ui.view.layout.MessengerScreen;

import java.util.List;

public interface ChatSettingsScreen extends MessengerScreen {

    void showLoading();

    void showContent();

    void showError(Throwable e);

    void setConversation(@NonNull DataConversation conversation);

    void setOwner(DataUser owner);

    void setParticipants(DataConversation conversation, List<DataUser> participants);

    void showSubjectDialog(String currentSubject);

    void showErrorDialog(@StringRes int msg);

    void showLeaveChatDialog(String currentSubject);

    void invalidateToolbarMenu();
}
