package com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search;

import android.os.Parcel;

import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public class DtlLocationsSearchViewState extends ViewState {

   String searchQuery;

   public enum State {
      LOADING,
      CONTENT,
      ERROR,
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable
   ///////////////////////////////////////////////////////////////////////////

   protected DtlLocationsSearchViewState(Parcel in) {
      super(in);
      searchQuery = in.readString();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeString(searchQuery);
   }

   public static final Creator<DtlLocationsSearchViewState> CREATOR = new Creator<DtlLocationsSearchViewState>() {
      @Override
      public DtlLocationsSearchViewState createFromParcel(Parcel in) {
         return new DtlLocationsSearchViewState(in);
      }

      @Override
      public DtlLocationsSearchViewState[] newArray(int size) {
         return new DtlLocationsSearchViewState[size];
      }
   };
}
