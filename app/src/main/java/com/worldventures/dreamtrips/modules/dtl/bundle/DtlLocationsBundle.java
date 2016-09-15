package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Holds the only boolean flag - {@link DtlLocationsBundle#shouldShowEmptyMerchantsCaption shouldShowEmptyMerchantsCaption}
 * an ad-hoc solution for business requirement to show caption for empty merchant payload on location screen.
 */
public class DtlLocationsBundle implements Parcelable {

   final public boolean shouldShowEmptyMerchantsCaption;

   /**
    * Default constructor emplies false for
    * {@link DtlLocationsBundle#shouldShowEmptyMerchantsCaption shouldShowEmptyMerchantsCaption} flag
    */
   public DtlLocationsBundle() {
      this.shouldShowEmptyMerchantsCaption = false;
   }

   public DtlLocationsBundle(boolean shouldShowEmptyMerchantsCaption) {
      this.shouldShowEmptyMerchantsCaption = shouldShowEmptyMerchantsCaption;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable
   ///////////////////////////////////////////////////////////////////////////

   protected DtlLocationsBundle(Parcel in) {
      shouldShowEmptyMerchantsCaption = in.readByte() != 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeByte((byte) (shouldShowEmptyMerchantsCaption ? 1 : 0));
   }

   @Override
   public int describeContents() {
      return 0;
   }

   public static final Creator<DtlLocationsBundle> CREATOR = new Creator<DtlLocationsBundle>() {
      @Override
      public DtlLocationsBundle createFromParcel(Parcel in) {
         return new DtlLocationsBundle(in);
      }

      @Override
      public DtlLocationsBundle[] newArray(int size) {
         return new DtlLocationsBundle[size];
      }
   };
}
