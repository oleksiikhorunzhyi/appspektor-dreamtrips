package com.messenger.ui.util.avatar;

import rx.Observable;

public interface MessengerMediaPickerDelegate {

   void resetPhotoPicker();

   void register();

   void unregister();

   void showPhotoPicker();

   void showMultiPhotoPicker();

   void hidePhotoPicker();

   Observable<String> getImagePathsStream();
}
