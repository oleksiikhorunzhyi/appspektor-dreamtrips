package com.worldventures.dreamtrips.modules.media_picker.model;

import android.net.Uri;

import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;

public interface MediaPickerModel {

   enum Type {
      PHOTO,
      VIDEO
   }

   Type getType();

   MediaAttachment.Source getSource();

   boolean isChecked();

   void setChecked(boolean checked);

   Uri getUri();

   String getAbsolutePath();

   long getPickedTime();

   long getDateTaken();
}
