package com.worldventures.dreamtrips.modules.facebook.model;

import android.net.Uri;

import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModel;

import java.io.Serializable;
import java.util.List;

public class FacebookPhoto implements MediaPickerModel, Serializable {

   private String id;
   private List<ImageSource> images;

   private boolean checked;
   private long pickedTime;

   public FacebookPhoto(String id, List<ImageSource> images, boolean checked, long pickedTime) {
      this.id = id;
      this.images = images;
      this.checked = checked;
      this.pickedTime = pickedTime;
   }

   public String getId() {
      return id;
   }

   public void setChecked(boolean checked) {
      this.checked = checked;
   }

   @Override
   public Type getType() {
      return Type.PHOTO;
   }

   @Override
   public Uri getUri() {
      return Uri.parse(getStringUri());
   }

   private String getStringUri() {
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

   @Override
   public long getDateTaken() {
      return 0;
   }

   public void setPickedTime(long pickedTime) {
      this.pickedTime = pickedTime;
   }

   public boolean isChecked() {
      return checked;
   }

   public List<ImageSource> getImages() {
      return images;
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
