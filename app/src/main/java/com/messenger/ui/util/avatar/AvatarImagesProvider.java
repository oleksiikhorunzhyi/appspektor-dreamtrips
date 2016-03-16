package com.messenger.ui.util.avatar;

import com.kbeanie.imagechooser.api.ChosenImage;

import rx.Observable;

public interface AvatarImagesProvider {
    void showAvatarPhotoPicker();
    Observable<ChosenImage> getAvatarImagesStream();
}
