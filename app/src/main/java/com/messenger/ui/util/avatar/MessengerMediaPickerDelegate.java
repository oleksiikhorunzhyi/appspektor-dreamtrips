package com.messenger.ui.util.avatar;

import rx.Observable;

public interface MessengerMediaPickerDelegate {
    void register();

    void unregister();

    void showPhotoPicker();

    void hidePhotoPicker();

    Observable<String> getImagePathsStream();
}
