package com.worldventures.dreamtrips.wallet.ui.common.base;

import android.net.Uri;
import android.os.Bundle;

import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;

import java.io.File;

import rx.Observable;

public interface MediaPickerService {
   String SERVICE_NAME = "MediaPickerService";

   void pickPhoto();

   void pickPhotos(int limit);

   void crop(String filePath);

   void hidePicker();

   Observable<Uri> observePicker();

   Observable<File> observeCropper();

   void onSaveInstanceState(Bundle outState);

   void onRestoreInstanceState(Bundle savedInstanceState);

   void destroy();

   void setPhotoPickerListener(PhotoPickerLayout.PhotoPickerListener photoPickerListener);
}
