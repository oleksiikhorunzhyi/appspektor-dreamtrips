package com.messenger.ui.util.avatar;

import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;

import rx.Observable;

public interface MessengerMediaPickerDelegate {

   void resetPhotoPicker();

   void register();

   void unregister();

   void showPhotoPicker();

   @Deprecated
   void showMultiPhotoPicker();

   void showMultiPhotoPicker(final int max);

   void hidePhotoPicker();

   void setPhotoPickerListener(PhotoPickerLayout.PhotoPickerListener photoPickerListener);

   Observable<String> getImagePathsStream();
}
