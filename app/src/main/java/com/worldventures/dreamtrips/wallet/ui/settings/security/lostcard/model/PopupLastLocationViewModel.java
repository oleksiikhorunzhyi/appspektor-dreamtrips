package com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.worldventures.dreamtrips.BR;

public class PopupLastLocationViewModel extends BaseObservable {

   private String place = "";
   private String address = "";
   private String lastConnectedDate = "";

   public PopupLastLocationViewModel(){}

   @Bindable
   public String getPlace() {
      return place;
   }

   public void setPlace(String place) {
      this.place = place;
      notifyPropertyChanged(BR.place);
   }

   @Bindable
   public String getAddress() {
      return address;
   }

   public void setAddress(String address) {
      this.address = address;
      notifyPropertyChanged(BR.address);
   }

   @Bindable
   public String getLastConnectedDate() {
      return lastConnectedDate;
   }

   public void setLastConnectedDate(String lastConnectedDate) {
      this.lastConnectedDate = lastConnectedDate;
      notifyPropertyChanged(BR.lastConnectedDate);
   }

   public boolean hasLastLocation() {
      return !address.isEmpty() && !lastConnectedDate.isEmpty();
   }
}
