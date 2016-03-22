package com.messenger.ui.util.avatar;

import com.kbeanie.imagechooser.api.ChosenImage;

import rx.Observable;

public interface ChangeAvatarDelegate {
    void register();

    void unregister();

    void showAvatarPhotoPicker();

    void hideAvatarPhotoPicker();

    Observable<ChosenImage> getAvatarImagesStream();
}
