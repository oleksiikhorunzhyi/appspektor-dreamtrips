package com.worldventures.dreamtrips.social.ui.tripsimages.view.args;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.social.ui.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.YSBHPhoto;

import java.util.List;

public class YsbhPagerArgs implements Parcelable {

   private final List<YSBHPhoto> currentItems;
   private final boolean lastPageReached;
   private final int currentItemPosition;

   public YsbhPagerArgs(List<YSBHPhoto> currentItems, boolean lastPageReached, int currentItemPosition) {
      this.currentItems = currentItems;
      this.lastPageReached = lastPageReached;
      this.currentItemPosition = currentItemPosition;
   }

   public List<YSBHPhoto> getCurrentItems() {
      return currentItems;
   }

   public boolean isLastPageReached() {
      return lastPageReached;
   }

   public int getCurrentItemPosition() {
      return currentItemPosition;
   }

   protected YsbhPagerArgs(Parcel in) {
      currentItems = in.readArrayList(Inspiration.class.getClassLoader());
      lastPageReached = in.readInt() == 1;
      currentItemPosition = in.readInt();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeList(currentItems);
      dest.writeInt(lastPageReached ? 1 : 0);
      dest.writeInt(currentItemPosition);
   }

   @Override
   public int describeContents() {
      return 0;
   }

   public static final Creator<YsbhPagerArgs> CREATOR = new Creator<YsbhPagerArgs>() {
      @Override
      public YsbhPagerArgs createFromParcel(Parcel in) {
         return new YsbhPagerArgs(in);
      }

      @Override
      public YsbhPagerArgs[] newArray(int size) {
         return new YsbhPagerArgs[size];
      }
   };
}
