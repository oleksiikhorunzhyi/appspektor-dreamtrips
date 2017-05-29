package com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.BR;

public class ProfileViewModel extends BaseObservable implements Parcelable {
   static final String DEFAULT_COUNTRY_CODE = "1";
   static final String DEFAULT_AVATAR_URL = "/avatars/thumb/missing.png";
   private String firstName = "";
   private String middleName = "";
   private String lastName = "";
   private String phoneCode = DEFAULT_COUNTRY_CODE;
   private String phoneNumber = "";
   @Nullable private String chosenPhotoUri;
   private boolean isPhotoEmpty;

   public ProfileViewModel() {}

   @BindingAdapter("app:imageUrl")
   public static void setImageUrl(SimpleDraweeView view, String imageUrl) {
      view.setImageURI(imageUrl);
   }

   @Bindable
   public String getFirstName() {
      return firstName;
   }

   public void setFirstName(String firstName) {
      this.firstName = firstName;
      notifyPropertyChanged(BR.firstName);
   }

   @Bindable
   public String getMiddleName() {
      return middleName;
   }

   public void setMiddleName(String middleName) {
      this.middleName = middleName;
      notifyPropertyChanged(BR.middleName);
   }

   @Bindable
   public String getLastName() {
      return lastName;
   }

   public void setLastName(String lastName) {
      this.lastName = lastName;
      notifyPropertyChanged(BR.lastName);
   }

   @Bindable
   public String getPhoneCode() {
      return phoneCode;
   }

   public void setPhoneCode(String phoneCode) {
      this.phoneCode = phoneCode;
      notifyPropertyChanged(BR.phoneCode);
   }

   @Bindable
   public String getPhoneNumber() {
      return phoneNumber;
   }

   public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
      notifyPropertyChanged(BR.phoneNumber);
   }

   @Nullable
   @Bindable
   public String getChosenPhotoUri() {
      return chosenPhotoUri;
   }

   public void setChosenPhotoUri(@Nullable String chosenPhotoUri) {
      this.chosenPhotoUri = chosenPhotoUri;
      notifyPropertyChanged(BR.chosenPhotoUri);
      setPhotoEmpty(chosenPhotoUri == null || chosenPhotoUri.equals(DEFAULT_AVATAR_URL) || chosenPhotoUri.isEmpty());
   }

   @Bindable
   public boolean isPhotoEmpty() {
      return isPhotoEmpty;
   }

   public void setPhotoEmpty(boolean isPhotoEmpty) {
      this.isPhotoEmpty = isPhotoEmpty;
      notifyPropertyChanged(BR.photoEmpty);
   }

   // Parcelable
   protected ProfileViewModel(Parcel in) {
      this.firstName = in.readString();
      this.middleName = in.readString();
      this.lastName = in.readString();
      this.phoneCode = in.readString();
      this.phoneNumber = in.readString();

      this.chosenPhotoUri = in.readString();
      this.isPhotoEmpty = in.readByte() != 0;
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.firstName);
      dest.writeString(this.middleName);
      dest.writeString(this.lastName);
      dest.writeString(this.phoneCode);
      dest.writeString(this.phoneNumber);

      dest.writeString(this.chosenPhotoUri);
      dest.writeByte((byte) (isPhotoEmpty ? 1 : 0));
   }

   public static final Parcelable.Creator<ProfileViewModel> CREATOR = new Parcelable.Creator<ProfileViewModel>() {
      @Override
      public ProfileViewModel createFromParcel(Parcel source) {
         return new ProfileViewModel(source);
      }

      @Override
      public ProfileViewModel[] newArray(int size) {
         return new ProfileViewModel[size];
      }
   };
}
