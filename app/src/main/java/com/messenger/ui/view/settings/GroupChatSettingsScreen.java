package com.messenger.ui.view.settings;

import com.messenger.entities.DataUser;

import rx.Observable;

public interface GroupChatSettingsScreen extends ChatSettingsScreen {
    void showAvatarPhotoPicker();

    void hideAvatarPhotoPicker();

    Observable<String> getAvatarImagePathsStream();

    void showChangingAvatarProgressBar();

    void hideChangingAvatarProgressBar();

    void setLeaveButtonVisible(boolean visible);

    void setOwner(DataUser owner);

    void showSubjectDialog(String currentSubject);

    void showLeaveChatDialog(String currentSubject);
}
