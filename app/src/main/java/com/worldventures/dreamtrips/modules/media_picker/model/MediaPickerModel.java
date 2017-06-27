package com.worldventures.dreamtrips.modules.media_picker.model;

import android.net.Uri;

public interface MediaPickerModel {

   enum Type {
      PHOTO,
      VIDEO
   }

   Type getType();

   boolean isChecked();

   void setChecked(boolean checked);

   Uri getUri();

   String getAbsolutePath();

   long getPickedTime();

   long getDateTaken();
}
