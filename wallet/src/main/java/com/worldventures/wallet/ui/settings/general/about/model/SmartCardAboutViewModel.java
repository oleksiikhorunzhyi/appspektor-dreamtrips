package com.worldventures.wallet.ui.settings.general.about.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.worldventures.wallet.BR;
import com.worldventures.wallet.domain.entity.SmartCardFirmware;

public class SmartCardAboutViewModel extends BaseObservable implements Parcelable {

   private String smartCardUserFullName;
   private String smartCardId;
   private String cardsStored;
   private String cardsAvailable;
   private String appVersion;
   private String nordicAppVersion;
   private String internalAtmelVersion;
   private String nrfBootloaderVersion;
   private String externalAtmelVersion;

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
   public String getNordicAppVersion() {
      return nordicAppVersion;
   }

   @Bindable
   public String getInternalAtmelVersion() {
      return internalAtmelVersion;
   }

   @Bindable
   public String getNrfBootloaderVersion() {
      return nrfBootloaderVersion;
   }

   @Bindable
   public String getExternalAtmelVersion() {
      return externalAtmelVersion;
   }

   public void setSmartCardFirmware(SmartCardFirmware smartCardFirmware) {
      this.nordicAppVersion = smartCardFirmware.getNordicAppVersion();
      this.internalAtmelVersion = smartCardFirmware.getInternalAtmelVersion();
      this.nrfBootloaderVersion = smartCardFirmware.getNrfBootloaderVersion();
      this.externalAtmelVersion = smartCardFirmware.getExternalAtmelVersion();
      notifyPropertyChanged(BR.internalAtmelVersion);
      notifyPropertyChanged(BR.nordicAppVersion);
      notifyPropertyChanged(BR.nrfBootloaderVersion);
      notifyPropertyChanged(BR.externalAtmelVersion);
   }

   public boolean isEmpty() {
      return TextUtils.isEmpty(smartCardUserFullName)
            || TextUtils.isEmpty(smartCardId)
            || TextUtils.isEmpty(cardsStored)
            || TextUtils.isEmpty(cardsAvailable)
            || TextUtils.isEmpty(appVersion)
            || TextUtils.isEmpty(nordicAppVersion)
            || TextUtils.isEmpty(internalAtmelVersion)
            || TextUtils.isEmpty(nrfBootloaderVersion)
            || TextUtils.isEmpty(externalAtmelVersion);
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.smartCardUserFullName);
      dest.writeString(this.smartCardId);
      dest.writeString(this.cardsStored);
      dest.writeString(this.cardsAvailable);
      dest.writeString(this.appVersion);
      dest.writeString(this.nordicAppVersion);
      dest.writeString(this.internalAtmelVersion);
      dest.writeString(this.nrfBootloaderVersion);
      dest.writeString(this.externalAtmelVersion);
   }

   public SmartCardAboutViewModel() {
      // nothing
   }

   protected SmartCardAboutViewModel(Parcel in) {
      this.smartCardUserFullName = in.readString();
      this.smartCardId = in.readString();
      this.cardsStored = in.readString();
      this.cardsAvailable = in.readString();
      this.appVersion = in.readString();
      this.nordicAppVersion = in.readString();
      this.internalAtmelVersion = in.readString();
      this.nrfBootloaderVersion = in.readString();
      this.externalAtmelVersion = in.readString();
   }

   public static final Parcelable.Creator<SmartCardAboutViewModel> CREATOR = new Parcelable.Creator<SmartCardAboutViewModel>() {

      @Override
      public SmartCardAboutViewModel createFromParcel(Parcel source) {
         return new SmartCardAboutViewModel(source);
      }

      @Override
      public SmartCardAboutViewModel[] newArray(int size) {
         return new SmartCardAboutViewModel[size];
      }
   };
}
