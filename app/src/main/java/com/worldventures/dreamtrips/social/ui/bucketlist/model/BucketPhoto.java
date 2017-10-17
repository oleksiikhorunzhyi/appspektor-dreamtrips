package com.worldventures.dreamtrips.social.ui.bucketlist.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.core.model.ImagePathHolder;

import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class BucketPhoto implements Serializable, Parcelable, ImagePathHolder {

   public static final long serialVersionUID = 14534647;

   public static final Creator<BucketPhoto> CREATOR = new Creator<BucketPhoto>() {
      public BucketPhoto createFromParcel(Parcel source) {
         return new BucketPhoto(source);
      }

      public BucketPhoto[] newArray(int size) {
         return new BucketPhoto[size];
      }
   };

   private String uid;
   private String originUrl;
   private String url;

   private boolean isCover;

   public BucketPhoto() {
   }

   private BucketPhoto(Parcel in) {
      this.originUrl = in.readString();
      this.url = in.readString();
      this.uid = in.readString();
   }

   public void setUid(String uid) {
      this.uid = uid;
   }

   public String getUid() {
      return uid;
   }

   public String getOriginUrl() {
      return originUrl;
   }

   public void setOriginUrl(String originUrl) {
      this.originUrl = originUrl;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public void setIsCover(boolean isCover) {
      this.isCover = isCover;
   }

   public boolean isCover() {
      return isCover;
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
      dest.writeString(this.originUrl);
      dest.writeString(this.url);
      dest.writeString(this.uid);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      BucketPhoto that = (BucketPhoto) o;

      return !(uid != null ? !uid.equals(that.uid) : that.uid != null);

   }

   @Override
   public int hashCode() {
      return uid != null ? uid.hashCode() : 0;
   }
}
