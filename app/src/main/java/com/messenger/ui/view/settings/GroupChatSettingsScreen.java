package com.messenger.ui.view.settings;

import rx.Observable;

public interface GroupChatSettingsScreen extends ChatSettingsScreen {
    void showAvatarPhotoPicker();

    void hideAvatarPhotoPicker();

    Observable<String> getAvatarImagePathsStream();

    void showChangingAvatarProgressBar();

    void hideChangingAvatarProgressBar();

    void setLeaveButtonVisible(boolean visible);
}
