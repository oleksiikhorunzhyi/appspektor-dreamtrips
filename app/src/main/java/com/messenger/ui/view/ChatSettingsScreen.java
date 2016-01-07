package com.messenger.ui.view;

import android.support.annotation.StringRes;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;

import java.util.List;

public interface ChatSettingsScreen extends MessengerScreen {

    void showLoading();

    void showContent();

    void showError(Throwable e);

    void setConversation(Conversation conversation);

    void setParticipants(Conversation conversation, List<User> participants);

    void showSubjectDialog();

    void prepareViewForOwner(boolean isOwner);

    void setNotificationSettingStatus(boolean checked);

    void showErrorDialog(@StringRes int msg);
}
