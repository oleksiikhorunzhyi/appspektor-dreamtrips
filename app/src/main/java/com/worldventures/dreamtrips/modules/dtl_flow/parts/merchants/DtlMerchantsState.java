package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.os.Parcel;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

import java.util.Collections;
import java.util.List;

public class DtlMerchantsState extends ViewState {

   private List<ThinMerchant> merchants = Collections.emptyList();
   private List<String> expandedIds = Collections.emptyList();

   public DtlMerchantsState(List<ThinMerchant> merchants, List<String> expandedIds) {
      this.merchants = merchants;
      this.expandedIds = expandedIds;
   }

   public void setMerchants(List<ThinMerchant> merchants) {
      this.merchants = merchants;
   }

   public List<ThinMerchant> getMerchants() {
      return merchants;
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
      this.merchants = in.readArrayList(ThinMerchant.class.getClassLoader());
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeList(this.expandedIds);
      dest.writeList(this.merchants);
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
