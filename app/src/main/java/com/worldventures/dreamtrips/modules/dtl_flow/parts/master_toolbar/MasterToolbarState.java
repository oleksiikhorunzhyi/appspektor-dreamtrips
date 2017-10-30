package com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar;

import android.os.Parcel;

import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change.DtlLocationChangePresenterImpl;

class MasterToolbarState extends ViewState {

   private boolean showPopup;
   private DtlLocationChangePresenterImpl.ScreenMode screenMode;

   MasterToolbarState() {
      //do nothing
   }

   public void setPopupShowing(boolean show) {
      this.showPopup = show;
   }

   public boolean isPopupShowing() {
      return showPopup;
   }

   public DtlLocationChangePresenterImpl.ScreenMode getScreenMode() {
      return screenMode;
   }

   public void setScreenMode(DtlLocationChangePresenterImpl.ScreenMode screenMode) {
      this.screenMode = screenMode;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable
   ///////////////////////////////////////////////////////////////////////////


   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeByte(this.showPopup ? (byte) 1 : (byte) 0);
      dest.writeInt(this.screenMode == null ? -1 : this.screenMode.ordinal());
   }

   protected MasterToolbarState(Parcel in) {
      super(in);
      this.showPopup = in.readByte() != 0;
      int tmpScreenMode = in.readInt();
      this.screenMode = tmpScreenMode == -1 ? null : DtlLocationChangePresenterImpl.ScreenMode.values()[tmpScreenMode];
   }

   public static final Creator<MasterToolbarState> CREATOR = new Creator<MasterToolbarState>() {
      @Override
      public MasterToolbarState createFromParcel(Parcel source) {
         return new MasterToolbarState(source);
      }

      @Override
      public MasterToolbarState[] newArray(int size) {
         return new MasterToolbarState[size];
      }
   };
}
