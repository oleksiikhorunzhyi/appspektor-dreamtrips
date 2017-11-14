package com.worldventures.wallet.ui.settings.help.feedback.payment.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.worldventures.wallet.BR;

import static com.worldventures.core.utils.ProjectTextUtils.isNotEmpty;

public class MerchantViewModel extends BaseObservable {

   private int selectedTypeIndex = 0;
   private String merchantType; //it's value of selectedTypeIndex
   private String merchantName;
   private String addressLine1;
   private String addressLine2;
   private String city;
   private String state;
   private String zip;

   public int getSelectedTypeIndex() {
      return selectedTypeIndex;
   }

   public void setSelectedTypeIndex(int selectedTypeIndex) {
      this.selectedTypeIndex = selectedTypeIndex;
   }

   public String getMerchantType() {
      return merchantType;
   }

   public void setMerchantType(String merchantType) {
      this.merchantType = merchantType;
   }

   @Bindable
   public String getMerchantName() {
      return merchantName;
   }

   public void setMerchantName(String merchantName) {
      this.merchantName = merchantName;
      notifyPropertyChanged(BR.merchantName);
   }

   @Bindable
   public String getAddressLine1() {
      return addressLine1;
   }

   public void setAddressLine1(String addressLine1) {
      this.addressLine1 = addressLine1;
      notifyPropertyChanged(BR.addressLine1);
   }

   public String getAddressLine2() {
      return addressLine2;
   }

   public void setAddressLine2(String addressLine2) {
      this.addressLine2 = addressLine2;
   }

   public String getCity() {
      return city;
   }

   public void setCity(String city) {
      this.city = city;
   }

   public String getState() {
      return state;
   }

   public void setState(String state) {
      this.state = state;
   }

   public String getZip() {
      return zip;
   }

   public void setZip(String zip) {
      this.zip = zip;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }

      MerchantViewModel that = (MerchantViewModel) o;

      if (selectedTypeIndex != that.selectedTypeIndex) {
         return false;
      }
      if (merchantName != null ? !merchantName.equals(that.merchantName) : that.merchantName != null) {
         return false;
      }
      if (addressLine1 != null ? !addressLine1.equals(that.addressLine1) : that.addressLine1 != null) {
         return false;
      }
      if (addressLine2 != null ? !addressLine2.equals(that.addressLine2) : that.addressLine2 != null) {
         return false;
      }
      if (city != null ? !city.equals(that.city) : that.city != null) {
         return false;
      }
      if (state != null ? !state.equals(that.state) : that.state != null) {
         return false;
      }
      return zip != null ? zip.equals(that.zip) : that.zip == null;

   }

   @Override
   public int hashCode() {
      int result = selectedTypeIndex;
      result = 31 * result + (merchantName != null ? merchantName.hashCode() : 0);
      result = 31 * result + (addressLine1 != null ? addressLine1.hashCode() : 0);
      result = 31 * result + (addressLine2 != null ? addressLine2.hashCode() : 0);
      result = 31 * result + (city != null ? city.hashCode() : 0);
      result = 31 * result + (state != null ? state.hashCode() : 0);
      result = 31 * result + (zip != null ? zip.hashCode() : 0);
      return result;
   }

   public boolean isDataChanged() {
      return selectedTypeIndex != 0
            || isNotEmpty(merchantName)
            || isNotEmpty(addressLine1)
            || isNotEmpty(addressLine2)
            || isNotEmpty(city)
            || isNotEmpty(state)
            || isNotEmpty(zip);
   }
}
