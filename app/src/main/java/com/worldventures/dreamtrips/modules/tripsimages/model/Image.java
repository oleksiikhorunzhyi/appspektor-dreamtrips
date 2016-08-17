package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class Image implements Parcelable, Serializable {

   public static final Creator<Image> CREATOR = new Creator<Image>() {
      public Image createFromParcel(Parcel source) {
         return new Image(source);
      }

      public Image[] newArray(int size) {
         return new Image[size];
      }
   };

   private boolean fromFile;
   private String url;

   public Image() {
   }

   private Image(Parcel in) {
      this.url = in.readString();
   }

   public String getUrl() {
      return url;
   }

   public String getUrl(int width, int height) {
      int size = Math.max(width, height);
      return ImageUtils.getParametrizedUrl(url, size, size);
   }

   public String getThumbUrl(Resources resources) {
      int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.photo_thumb_size);
      return ImageUtils.getParametrizedUrl(url, dimensionPixelSize, dimensionPixelSize);
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public void setFromFile(boolean fromFile) {
      this.fromFile = fromFile;
   }

   public boolean isFromFile() {
      return fromFile;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.url);
   }
}
