package com.worldventures.dreamtrips.wallet.ui.common.picker.base;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class WalletPickerAttachment implements Parcelable {

   private final WalletPickerSource pickerSource;
   private final List<BasePickerViewModel> chosenPhotos;

   public WalletPickerAttachment(WalletPickerSource pickerSource, List<BasePickerViewModel> chosenPhotos) {
      this.pickerSource = pickerSource;
      this.chosenPhotos = chosenPhotos;
   }

   protected WalletPickerAttachment(Parcel in) {
      this.pickerSource = WalletPickerSource.values()[in.readInt()];
      List<BasePickerViewModel> list = new ArrayList<>();
      in.readList(list, BasePickerViewModel.class.getClassLoader());
      this.chosenPhotos = list;

   }

   public static final Creator<WalletPickerAttachment> CREATOR = new Creator<WalletPickerAttachment>() {
      @Override
      public WalletPickerAttachment createFromParcel(Parcel in) {
         return new WalletPickerAttachment(in);
      }

      @Override
      public WalletPickerAttachment[] newArray(int size) {
         return new WalletPickerAttachment[size];
      }
   };

   public WalletPickerSource getPickerSource() {
      return pickerSource;
   }

   public List<BasePickerViewModel> getChosenPhotos() {
      return chosenPhotos;
   }

   public enum WalletPickerSource {
      CAMERA, GALLERY, FACEBOOK
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(this.pickerSource.ordinal());
      dest.writeList(chosenPhotos);
   }
}
