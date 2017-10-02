package com.worldventures.core.modules.picker.model;

import android.net.Uri;

public interface MediaPickerModel {

   enum Type {
      PHOTO,
      VIDEO
   }

   Type getType();

   MediaPickerAttachment.Source getSource();

   boolean isChecked();

   void setChecked(boolean checked);

   Uri getUri();

   String getAbsolutePath();

   long getPickedTime();

   long getDateTaken();
}
