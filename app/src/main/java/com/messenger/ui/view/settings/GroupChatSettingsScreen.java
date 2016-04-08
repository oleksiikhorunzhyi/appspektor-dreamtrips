package com.messenger.ui.view.settings;

import com.kbeanie.imagechooser.api.ChosenImage;

import rx.Observable;

public interface GroupChatSettingsScreen extends ChatSettingsScreen {
    void showAvatarPhotoPicker();

    void hideAvatarPhotoPicker();

    Observable<ChosenImage> getAvatarImagesStream();

    void showChangingAvatarProgressBar();

    void hideChangingAvatarProgressBar();
}
