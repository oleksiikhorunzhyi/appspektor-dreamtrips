package com.worldventures.wallet.ui.settings.security.lostcard.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.wallet.BR;
import com.worldventures.wallet.domain.entity.lostcard.WalletAddress;
import com.worldventures.wallet.domain.entity.lostcard.WalletPlace;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.lang.String.format;

public class PopupLastLocationViewModel extends BaseObservable implements Parcelable {

   private final SimpleDateFormat lastConnectedDateFormat = new SimpleDateFormat("EEEE, MMMM dd, h:mma", Locale.US);

   private String place = "";
   private String address = "";
   private String lastConnectedDate = "";
   private boolean visible = true;

   public PopupLastLocationViewModel() {
      // This constructor is intentionally empty. Nothing special is needed here.
   }

   private PopupLastLocationViewModel(Parcel in) {
      place = in.readString();
      address = in.readString();
      lastConnectedDate = in.readString();
      visible = in.readByte() != 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(place);
      dest.writeString(address);
      dest.writeString(lastConnectedDate);
      dest.writeByte((byte) (visible ? 1 : 0));
   }

   @Override
   public int describeContents() {
      return 0;
   }

   public static final Creator<PopupLastLocationViewModel> CREATOR = new Creator<PopupLastLocationViewModel>() {
      @Override
      public PopupLastLocationViewModel createFromParcel(Parcel in) {
         return new PopupLastLocationViewModel(in);
      }

      @Override
      public PopupLastLocationViewModel[] newArray(int size) {
         return new PopupLastLocationViewModel[size];
      }
   };

   @Bindable
   public String getPlace() {
      return place;
   }

   public void setPlaces(@Nullable List<WalletPlace> places) {
      this.place = obtainPlace(places);
      notifyPropertyChanged(BR.place);
   }

   private String obtainPlace(List<WalletPlace> places) {
      return places != null && places.size() == 1 ? places.get(0).getName() : "";
   }

   @Bindable
   public String getAddress() {
      return address;
   }

   public void setAddress(@Nullable WalletAddress address) {
      this.address = obtainAddress(address);
      notifyPropertyChanged(BR.address);
      notifyPropertyChanged(BR.visible);
   }

   private String obtainAddress(WalletAddress address) {
      return address != null
            ? format("%s, %s\n%s", address.getCountryName(), address.getAdminArea(), address.getAddressLine())
            : "";
   }

   @Bindable
   public String getLastConnectedDate() {
      return lastConnectedDate;
   }

   public void setLastConnectedDate(Date date) {
      this.lastConnectedDate = lastConnectedDateFormat.format(date);
      notifyPropertyChanged(BR.lastConnectedDate);
      notifyPropertyChanged(BR.visible);
   }

   public boolean hasLastLocation() {
      return (!address.isEmpty() || !place.isEmpty()) && !lastConnectedDate.isEmpty();
   }

   @Bindable
   public boolean getVisible() {
      return visible && hasLastLocation();
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
      notifyPropertyChanged(BR.visible);
   }


}
