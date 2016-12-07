package com.worldventures.dreamtrips.wallet.service.storage;

import java.io.File;

public class WizardMemoryStorage {

   private String barcode;
   private File userPhoto;
   private String fullName;

   public String getBarcode() {
      return barcode;
   }

   public void saveBarcode(String barcode) {
      this.barcode = barcode;
   }

   public File getUserPhoto() {
      return userPhoto;
   }

   public void saveUserPhoto(File userPhoto) {
      this.userPhoto = userPhoto;
   }

   public String getFullName() {
      return fullName;
   }

   public void saveFullName(String fullName) {
      this.fullName = fullName;
   }
}
