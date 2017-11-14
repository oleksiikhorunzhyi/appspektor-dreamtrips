package com.worldventures.wallet.ui.settings.general.about.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.worldventures.wallet.BR;
import com.worldventures.wallet.domain.entity.SmartCardFirmware;

public class SmartCardAboutViewModel extends BaseObservable {

   private String smartCardUserFullName;
   private String smartCardId;
   private String cardsStored;
   private String cardsAvailable;
   private String appVersion;
   private SmartCardFirmware smartCardFirmware;

   @Bindable
   public String getSmartCardUserFullName() {
      return smartCardUserFullName;
   }

   public void setSmartCardUserFullName(String smartCardUserFullName) {
      this.smartCardUserFullName = smartCardUserFullName;
      notifyPropertyChanged(BR.smartCardUserFullName);
   }

   @Bindable
   public String getSmartCardId() {
      return smartCardId;
   }

   public void setSmartCardId(String smartCardId) {
      this.smartCardId = smartCardId;
      notifyPropertyChanged(BR.smartCardId);
   }

   @Bindable
   public String getCardsStored() {
      return cardsStored;
   }

   public void setCardsStored(String cardsStored) {
      this.cardsStored = cardsStored;
      notifyPropertyChanged(BR.cardsStored);
   }

   @Bindable
   public String getCardsAvailable() {
      return cardsAvailable;
   }

   public void setCardsAvailable(String cardsAvailable) {
      this.cardsAvailable = cardsAvailable;
      notifyPropertyChanged(BR.cardsAvailable);
   }

   @Bindable
   public String getAppVersion() {
      return appVersion;
   }

   public void setAppVersion(String dtAppVersion) {
      this.appVersion = dtAppVersion;
      notifyPropertyChanged(BR.appVersion);
   }

   @Bindable
   public SmartCardFirmware getSmartCardFirmware() {
      return smartCardFirmware;
   }

   public void setSmartCardFirmware(SmartCardFirmware smartCardFirmware) {
      this.smartCardFirmware = smartCardFirmware;
      notifyPropertyChanged(BR.smartCardFirmware);
   }
}
