package com.worldventures.dreamtrips.modules.common.view.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.ShareType;

public class ShareBundle implements Parcelable {

   String imageUrl;
   String shareUrl;
   String text;
   String shareType;

   public String getImageUrl() {
      return imageUrl;
   }

   public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
   }

   public String getShareUrl() {
      return shareUrl;
   }

   public void setShareUrl(String shareUrl) {
      this.shareUrl = shareUrl;
   }

   public String getText() {
      return text;
   }

   public void setText(String text) {
      this.text = text;
   }

   public String getShareType() {
      return shareType;
   }

   public void setShareType(@ShareType String shareType) {
      this.shareType = shareType;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.imageUrl);
      dest.writeString(this.shareUrl);
      dest.writeString(this.text);
      dest.writeString(this.shareType);
   }

   public ShareBundle() {
   }

   protected ShareBundle(Parcel in) {
      this.imageUrl = in.readString();
      this.shareUrl = in.readString();
      this.text = in.readString();
      this.shareType = in.readString();
   }

   public static final Parcelable.Creator<ShareBundle> CREATOR = new Parcelable.Creator<ShareBundle>() {
      public ShareBundle createFromParcel(Parcel source) {
         return new ShareBundle(source);
      }

      public ShareBundle[] newArray(int size) {
         return new ShareBundle[size];
      }
   };
}
