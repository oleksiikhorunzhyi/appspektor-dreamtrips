package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.os.Parcel;

import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

import java.util.Collections;
import java.util.List;

public class DtlMerchantsState extends ViewState {

   private List<String> expandedIds = Collections.emptyList();

   public DtlMerchantsState() {
   }

   public void setExpandedMerchantIds(List<String> expandedIds) {
      this.expandedIds = expandedIds;
   }

   public List<String> getExpandedMerchantIds() {
      return expandedIds;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable
   ///////////////////////////////////////////////////////////////////////////

   protected DtlMerchantsState(Parcel in) {
      super(in);
      this.expandedIds = in.readArrayList(String.class.getClassLoader());
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeList(this.expandedIds);
   }

   public static final Creator<DtlMerchantsState> CREATOR = new Creator<DtlMerchantsState>() {
      @Override
      public DtlMerchantsState createFromParcel(Parcel in) {
         return new DtlMerchantsState(in);
      }

      @Override
      public DtlMerchantsState[] newArray(int size) {
         return new DtlMerchantsState[size];
      }
   };


}
