package com.worldventures.dreamtrips.modules.common.model;

public interface BasePhotoPickerModel {

   boolean isChecked();

   void setChecked(boolean checked);

   String getImageUri();

   String getAbsolutePath();

   long getPickedTime();
}
