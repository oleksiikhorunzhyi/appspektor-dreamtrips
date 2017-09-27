package com.worldventures.dreamtrips.social.ui.tripsimages.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.core.ui.fragment.ImagePathHolder;
import com.worldventures.dreamtrips.social.util.ImageUtils;

public class TripImage implements Parcelable, ImagePathHolder {

   public static final long serialVersionUID = 128L;

   private String id;
   private String description;
   private String url;
   private String type;
   @SerializedName("origin_url") private String originUrl;

   public String getUrl() {
      return url != null ? url : "";
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public String getUrl(int width, int height) {
      return ImageUtils.getParametrizedUrl(getUrl(), width, height);
   }

   public String getType() {
      return type;
   }

   public void setId(String id) {
      this.id = id;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setType(String type) {
      this.type = type;
   }

   public void setOriginUrl(String originUrl) {
      this.originUrl = originUrl;
   }

   @Override
   public String getImagePath() {
      return url;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.id);
      dest.writeString(this.description);
      dest.writeString(this.url);
      dest.writeString(this.type);
      dest.writeString(this.originUrl);
   }

   public TripImage() {
   }

   protected TripImage(Parcel in) {
      this.id = in.readString();
      this.description = in.readString();
      this.url = in.readString();
      this.type = in.readString();
      this.originUrl = in.readString();
   }

   public static final Creator<TripImage> CREATOR = new Creator<TripImage>() {
      public TripImage createFromParcel(Parcel source) {
         return new TripImage(source);
      }

      public TripImage[] newArray(int size) {
         return new TripImage[size];
      }
   };
}
