package com.messenger.ui.fragment;

import android.os.Parcel;
import android.os.Parcelable;

import com.messenger.entities.PhotoAttachment;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Inspiration;

import java.util.List;

public class PhotoAttachmentPagerArgs implements Parcelable {
   private List<PhotoAttachment> currentItems;
   private int currentItemPosition;

   public PhotoAttachmentPagerArgs(List<PhotoAttachment> currentItems, int currentItemPosition) {
      this.currentItems = currentItems;
      this.currentItemPosition = currentItemPosition;
   }

   public List<PhotoAttachment> getCurrentItems() {
      return currentItems;
   }

   public int getCurrentItemPosition() {
      return currentItemPosition;
   }

   protected PhotoAttachmentPagerArgs(Parcel in) {
      currentItems = in.readArrayList(Inspiration.class.getClassLoader());
      currentItemPosition = in.readInt();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeList(currentItems);
      dest.writeInt(currentItemPosition);
   }

   @Override
   public int describeContents() {
      return 0;
   }

   public static final Creator<PhotoAttachmentPagerArgs> CREATOR = new Creator<PhotoAttachmentPagerArgs>() {
      @Override
      public PhotoAttachmentPagerArgs createFromParcel(Parcel in) {
         return new PhotoAttachmentPagerArgs(in);
      }

      @Override
      public PhotoAttachmentPagerArgs[] newArray(int size) {
         return new PhotoAttachmentPagerArgs[size];
      }
   };
}
