package com.worldventures.dreamtrips.modules.facebook.model;

import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;

import java.io.Serializable;
import java.util.List;

public class FacebookPhoto implements BasePhotoPickerModel, Serializable {

   private String id;
   private List<ImageSource> images;

   private boolean checked;
   private long pickedTime;

   public String getId() {
      return id;
   }

   public void setChecked(boolean checked) {
      this.checked = checked;
   }

   @Override
   public String getImageUri() {
      if (images.size() > 2) {
         return images.get(images.size() / 2 + 1).getSource();
      } else {
         return images.get(0).getSource();
      }
   }

   @Override
   public String getAbsolutePath() {
      return images.get(0).getSource();
   }

   @Override
   public long getPickedTime() {
      return pickedTime;
   }

   public void setPickedTime(long pickedTime) {
      this.pickedTime = pickedTime;
   }

   public boolean isChecked() {
      return checked;
   }

   public static class ImageSource implements Serializable {

      private int height;
      private String source;
      private int width;

      public int getHeight() {
         return height;
      }

      public int getWidth() {
         return width;
      }

      public String getSource() {
         return source;
      }
   }
}
