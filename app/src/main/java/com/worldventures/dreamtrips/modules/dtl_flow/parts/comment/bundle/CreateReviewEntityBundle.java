package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;

public class CreateReviewEntityBundle implements Parcelable {

   private MediaAttachment mediaAttachment;
   private boolean showPickerImmediately;
   private Origin origin; // origin is needed for analytics
   private int minCharactersAllow;
   private int maxCharactersAllow;
   private String merchantId;
   private boolean isFromAddReview;
   private Merchant mMerchant;
   private boolean isVerified;

   public CreateReviewEntityBundle(boolean showPickerImmediately, Origin origin) {
      this.showPickerImmediately = showPickerImmediately;
      this.origin = origin;
   }

   public CreateReviewEntityBundle(boolean showPickerImmediately, Origin origin, int minChar, int maxChar, String merchantId, boolean isFromAddReview,
                           Merchant merchant, boolean isVerified) {
      this.showPickerImmediately = showPickerImmediately;
      this.origin = origin;
      this.minCharactersAllow = minChar;
      this.maxCharactersAllow = maxChar;
      this.merchantId = merchantId;
      this.isFromAddReview = isFromAddReview;
      this.mMerchant = merchant;
      this.isVerified = isVerified;
   }

   public CreateReviewEntityBundle(MediaAttachment mediaAttachment, Origin origin) {
      this.mediaAttachment = mediaAttachment;
      this.origin = origin;
   }

   public MediaAttachment getMediaAttachment() {
      return mediaAttachment;
   }

   public Origin getOrigin() {
      return origin;
   }

   public int getMaxCharactersAllow() {
      return maxCharactersAllow;
   }

   public String getMerchantId() {
      return merchantId;
   }

   public boolean isFromAddReview() {
      return isFromAddReview;
   }

   public boolean isVerified() {
      return isVerified;
   }

   public boolean isShowPickerImmediately() {
      return showPickerImmediately;
   }

   public Merchant getMerchant() {
      return mMerchant;
   }

   public void setShowPickerImmediately(boolean showPickerImmediately) {
      this.showPickerImmediately = showPickerImmediately;
   }

   public int getMinCharactersAllow() {
      return minCharactersAllow;
   }

   public enum Origin {
      FEED, PROFILE_TRIP_IMAGES, MY_TRIP_IMAGES, MEMBER_TRIP_IMAGES
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeParcelable(this.mediaAttachment, flags);
      dest.writeByte(this.showPickerImmediately ? (byte) 1 : (byte) 0);
      dest.writeInt(this.origin == null ? -1 : this.origin.ordinal());
      dest.writeInt(this.minCharactersAllow);
      dest.writeInt(this.maxCharactersAllow);
      dest.writeString(this.merchantId);
      dest.writeByte(this.isFromAddReview ? (byte) 1 : (byte) 0);
      dest.writeSerializable(this.mMerchant);
      dest.writeByte(this.isVerified ? (byte) 1 : (byte) 0);
   }

   protected CreateReviewEntityBundle(Parcel in) {
      this.mediaAttachment = in.readParcelable(MediaAttachment.class.getClassLoader());
      this.showPickerImmediately = in.readByte() != 0;
      int tmpOrigin = in.readInt();
      this.origin = tmpOrigin == -1 ? null : Origin.values()[tmpOrigin];
      this.minCharactersAllow = in.readInt();
      this.maxCharactersAllow = in.readInt();
      this.merchantId = in.readString();
      this.isFromAddReview = in.readByte() != 0;
      this.mMerchant = (Merchant) in.readSerializable();
      this.isVerified = in.readByte() != 0;
   }

   public static final Creator<CreateReviewEntityBundle> CREATOR = new Creator<CreateReviewEntityBundle>() {
      @Override
      public CreateReviewEntityBundle createFromParcel(Parcel source) {return new CreateReviewEntityBundle(source);}

      @Override
      public CreateReviewEntityBundle[] newArray(int size) {return new CreateReviewEntityBundle[size];}
   };
}
