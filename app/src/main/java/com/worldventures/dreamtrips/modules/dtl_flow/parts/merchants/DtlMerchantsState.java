package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

import java.util.Collections;
import java.util.List;

public class DtlMerchantsState extends ViewState {

   private List<String> expandedIds = Collections.emptyList();
   private Parcelable recyclerViewState;


   public DtlMerchantsState(List<String> expandedIds, Parcelable recyclerViewState) {
      this.expandedIds = expandedIds;
      this.recyclerViewState = recyclerViewState;
   }

   public List<String> getExpandedMerchantIds() {
      return expandedIds;
   }

   public Parcelable getRecyclerViewState() {
      return recyclerViewState;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable
   ///////////////////////////////////////////////////////////////////////////

   protected DtlMerchantsState(Parcel in) {
      super(in);
      this.expandedIds = in.readArrayList(String.class.getClassLoader());
      this.recyclerViewState = in.readParcelable(Parcelable.class.getClassLoader());
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeList(this.expandedIds);
      dest.writeParcelable(recyclerViewState, flags);
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
