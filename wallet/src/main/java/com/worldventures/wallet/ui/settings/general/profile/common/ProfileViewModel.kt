package com.worldventures.wallet.ui.settings.general.profile.common

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Parcel
import android.os.Parcelable
import com.worldventures.wallet.BR

data class ProfileViewModel(
      private var _firstName: String = "",
      private var _middleName: String = "",
      private var _lastName: String = "",
      private var _phoneCode: String = DEFAULT_COUNTRY_CODE,
      private var _phoneNumber: String = "",
      private var _chosenPhotoUri: String? = null
) : BaseObservable(), Parcelable {


   var firstName: String
      @Bindable get() = _firstName
      set(value) {
         _firstName = value
         notifyPropertyChanged(BR.firstName)
      }

   var middleName: String
      @Bindable get() = _middleName
      set(value) {
         _middleName = value
         notifyPropertyChanged(BR.middleName)
      }

   var lastName: String
      @Bindable get() = _lastName
      set(value) {
         _lastName = value
         notifyPropertyChanged(BR.lastName)
      }

   var phoneCode: String
      @Bindable get() = _phoneCode
      set(value) {
         _phoneCode = value
         notifyPropertyChanged(BR.phoneCode)
      }

   var phoneNumber: String
      @Bindable get() = _phoneNumber
      set(value) {
         _phoneNumber = value
         notifyPropertyChanged(BR.phoneNumber)
      }

   var chosenPhotoUri: String?
      @Bindable get() = _chosenPhotoUri
      set(value) {
         _chosenPhotoUri = value
         notifyPropertyChanged(BR.chosenPhotoUri)
         notifyPropertyChanged(BR.photoEmpty)
      }

   val isPhotoEmpty: Boolean
      @Bindable get() = WalletProfileUtils.isPhotoEmpty(chosenPhotoUri)

   val isEmpty: Boolean
      get() = firstName.isEmpty() && lastName.isEmpty()

   // Parcelable
   private constructor(`in`: Parcel) :
         this(_firstName = `in`.readString(),
               _middleName = `in`.readString(),
               _lastName = `in`.readString(),
               _phoneCode = `in`.readString(),
               _phoneNumber = `in`.readString(),
               _chosenPhotoUri = `in`.readString())

   override fun describeContents(): Int = 0

   override fun writeToParcel(dest: Parcel, flags: Int) {
      dest.writeString(this._firstName)
      dest.writeString(this._middleName)
      dest.writeString(this._lastName)
      dest.writeString(this._phoneCode)
      dest.writeString(this._phoneNumber)
      dest.writeString(this._chosenPhotoUri)
   }

   companion object {

      private val DEFAULT_COUNTRY_CODE = "1"

      @JvmField
      val CREATOR: Parcelable.Creator<ProfileViewModel> = object : Parcelable.Creator<ProfileViewModel> {

         override fun createFromParcel(source: Parcel): ProfileViewModel = ProfileViewModel(source)

         override fun newArray(size: Int): Array<ProfileViewModel?> = arrayOfNulls(size)
      }
   }
}
