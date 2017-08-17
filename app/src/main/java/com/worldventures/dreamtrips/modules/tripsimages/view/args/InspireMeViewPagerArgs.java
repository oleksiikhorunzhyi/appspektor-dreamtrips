package com.worldventures.dreamtrips.modules.tripsimages.view.args;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;

import java.util.List;

public class InspireMeViewPagerArgs implements Parcelable {
   private List<Inspiration> currentItems;
   private double randomSeed;
   private boolean lastPageReached;
   private int currentItemPosition;

   public InspireMeViewPagerArgs(List<Inspiration> currentItems, double randomSeed, boolean lastPageReached, int currentItemPosition) {
      this.currentItems = currentItems;
      this.randomSeed = randomSeed;
      this.lastPageReached = lastPageReached;
      this.currentItemPosition = currentItemPosition;
   }

   public List<Inspiration> getCurrentItems() {
      return currentItems;
   }

   public double getRandomSeed() {
      return randomSeed;
   }

   public boolean isLastPageReached() {
      return lastPageReached;
   }

   public int getCurrentItemPosition() {
      return currentItemPosition;
   }

   protected InspireMeViewPagerArgs(Parcel in) {
      currentItems = in.readArrayList(Inspiration.class.getClassLoader());
      randomSeed = in.readDouble();
      lastPageReached = in.readInt() == 1;
      currentItemPosition = in.readInt();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeList(currentItems);
      dest.writeDouble(randomSeed);
      dest.writeInt(lastPageReached ? 1 : 0);
      dest.writeInt(currentItemPosition);
   }

   @Override
   public int describeContents() {
      return 0;
   }

   public static final Creator<InspireMeViewPagerArgs> CREATOR = new Creator<InspireMeViewPagerArgs>() {
      @Override
      public InspireMeViewPagerArgs createFromParcel(Parcel in) {
         return new InspireMeViewPagerArgs(in);
      }

      @Override
      public InspireMeViewPagerArgs[] newArray(int size) {
         return new InspireMeViewPagerArgs[size];
      }
   };
}
