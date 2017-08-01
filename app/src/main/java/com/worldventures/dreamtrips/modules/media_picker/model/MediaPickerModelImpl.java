package com.worldventures.dreamtrips.modules.media_picker.model;

import android.net.Uri;
import android.os.Parcelable;

import com.worldventures.dreamtrips.util.ValidationUtils;

import java.io.Serializable;

public abstract class MediaPickerModelImpl implements MediaPickerModel, Parcelable, Serializable {

   private static final String PATH_DEVIDER = "/";

   protected String absolutePath;
   protected Uri uri;
   protected long dateTaken;
   protected long pickedTime;

   protected boolean checked;

   public MediaPickerModelImpl() {
   }

   public MediaPickerModelImpl(String absolutePath, long dateTaken) {
      this.absolutePath = absolutePath;
      this.dateTaken = dateTaken;
      this.uri = Uri.parse(isAbsolutePathUrl() ? this.absolutePath : "file://" + this.absolutePath);
   }

   public boolean isAbsolutePathUrl() {
      // TODO Remove this check outside of this model
      return ValidationUtils.isUrl(absolutePath);
   }

   public String getAbsolutePath() {
      return absolutePath;
   }

   public void setAbsolutePath(String absolutePath) {
      this.absolutePath = absolutePath;
   }

   @Override
   public Uri getUri() {
      return uri;
   }

   @Override
   public boolean isChecked() {
      return checked;
   }

   @Override
   public void setChecked(boolean checked) {
      this.checked = checked;
   }

   @Override
   public long getDateTaken() {
      return dateTaken;
   }

   public void setDateTaken(long dateTaken) {
      this.dateTaken = dateTaken;
   }

   @Override
   public long getPickedTime() {
      return pickedTime;
   }

   public void setPickedTime(long pickedTime) {
      this.pickedTime = pickedTime;
   }

   public String getFileName() {
      return absolutePath != null && absolutePath.contains(PATH_DEVIDER) ?
            absolutePath.substring(absolutePath.lastIndexOf(PATH_DEVIDER)) : "";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      MediaPickerModelImpl that = (MediaPickerModelImpl) o;

      return absolutePath.equals(that.absolutePath);
   }

   @Override
   public int hashCode() {
      return absolutePath.hashCode();
   }

   @Override
   public String toString() {
      return "MediaPickerModelImpl{" +
            "dateTaken=" + dateTaken +
            ", absolutePath='" + absolutePath + '\'' +
            '}';
   }
}
