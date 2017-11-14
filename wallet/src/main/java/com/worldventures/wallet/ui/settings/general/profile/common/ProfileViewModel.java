package com.worldventures.wallet.ui.settings.general.profile.common;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.wallet.BR;

public class ProfileViewModel extends BaseObservable implements Parcelable {

   private static final String DEFAULT_COUNTRY_CODE = "1";
   private static final String DEFAULT_AVATAR_URL = "/avatars/thumb/missing.png";

   private String firstName = "";
   private String middleName = "";
   private String lastName = "";
   private String phoneCode = DEFAULT_COUNTRY_CODE;
   private String phoneNumber = "";
   //TODO: disable suffix functionality
   //private String suffix = "";
   @Nullable private String chosenPhotoUri;
   private boolean isPhotoEmpty = true; // because chosenPhotoUri is null by default

   public ProfileViewModel() {
      //do nothing
   }

   @BindingAdapter("imageUrl")
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
      //TODO: disable suffix functionality
      //      if (getSuffix().isEmpty()) {
      return lastName;
      //      } else {
      //         String[] parts = lastName.split(" ");
      //         if (parts.length > 1) {
      //            String suffix = parts[parts.length - 1];
      //            if (suffix.length() < 4) {
      //               StringBuilder name = new StringBuilder();
      //               for (int i = 0; i < (parts.length - 1); i++) {
      //                  name.append(parts[i]).append(" ");
      //               }
      //               return name.substring(0, name.length() - 1);
      //            }
      //         }
      //         return lastName;
      //      }
   }

   public String getLastNameWithSuffix() {
      if (getSuffix().isEmpty()) {
         return lastName;
      } else {
         return getLastName() + " " + getSuffix();
      }
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

   public boolean isEmpty() {
      return firstName.isEmpty() && lastName.isEmpty();
   }

   @Bindable
   public String getSuffix() {
      //      if (suffix.isEmpty()) {
      //         String[] parts = lastName.split(" ");
      //         if (parts.length > 1) {
      //            String suffix = parts[parts.length - 1];
      //            if (suffix.length() < 4) {
      //               return suffix;
      //            }
      //         }
      //TODO: disable suffix functionality
      return "";
      //      } else {
      //         return suffix;
      //      }
   }

   //TODO: disable suffix functionality
   //   public void setSuffix(String suffix) {
   //      this.suffix = suffix;
   //      notifyPropertyChanged(BR.suffix);
   //   }
}
