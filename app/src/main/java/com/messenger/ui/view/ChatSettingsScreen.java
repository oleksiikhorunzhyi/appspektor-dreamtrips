package com.messenger.ui.view;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;

import java.util.List;

public interface ChatSettingsScreen extends ActivityAwareScreen {
    void showLoading();
    void showContent();
    void showError(Throwable e);
    void setConversation(Conversation conversation);
    void setParticipants(Conversation conversation, List<User> participants);
    void showSubjectDialog();
    void setNotificationSettingStatus(boolean checked);
}
