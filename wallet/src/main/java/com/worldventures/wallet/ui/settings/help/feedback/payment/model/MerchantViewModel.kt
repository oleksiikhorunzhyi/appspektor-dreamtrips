package com.worldventures.wallet.ui.settings.help.feedback.payment.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.worldventures.core.utils.ProjectTextUtils.isNotEmpty
import com.worldventures.wallet.BR

class MerchantViewModel : BaseObservable() {

   var selectedTypeIndex = 0
   var merchantType: String? = null //it's value of selectedTypeIndex
   @get:Bindable
   var merchantName: String? = null
      set(merchantName) {
         field = merchantName
         notifyPropertyChanged(BR.merchantName)
      }
   @get:Bindable
   var addressLine1: String? = null
      set(addressLine1) {
         field = addressLine1
         notifyPropertyChanged(BR.addressLine1)
      }
   var addressLine2: String? = null
   var city: String? = null
   var state: String? = null
   var zip: String? = null

   val isDataChanged: Boolean
      get() = (selectedTypeIndex != 0
            || isNotEmpty(this.merchantName)
            || isNotEmpty(this.addressLine1)
            || isNotEmpty(addressLine2)
            || isNotEmpty(city)
            || isNotEmpty(state)
            || isNotEmpty(zip))

   override fun equals(other: Any?): Boolean {
      if (this === other) {
         return true
      }
      if (other == null || javaClass != other.javaClass) {
         return false
      }

      val that = other as MerchantViewModel?

      if (selectedTypeIndex != that!!.selectedTypeIndex) {
         return false
      }
      if (if (this.merchantName != null) this.merchantName != that.merchantName else that.merchantName != null) {
         return false
      }
      if (if (this.addressLine1 != null) this.addressLine1 != that.addressLine1 else that.addressLine1 != null) {
         return false
      }
      if (if (addressLine2 != null) addressLine2 != that.addressLine2 else that.addressLine2 != null) {
         return false
      }
      if (if (city != null) city != that.city else that.city != null) {
         return false
      }
      if (if (state != null) state != that.state else that.state != null) {
         return false
      }
      return if (zip != null) zip == that.zip else that.zip == null

   }

   override fun hashCode(): Int {
      var result = selectedTypeIndex
      result = 31 * result + if (this.merchantName != null) this.merchantName!!.hashCode() else 0
      result = 31 * result + if (this.addressLine1 != null) this.addressLine1!!.hashCode() else 0
      result = 31 * result + if (addressLine2 != null) addressLine2!!.hashCode() else 0
      result = 31 * result + if (city != null) city!!.hashCode() else 0
      result = 31 * result + if (state != null) state!!.hashCode() else 0
      result = 31 * result + if (zip != null) zip!!.hashCode() else 0
      return result
   }
}
