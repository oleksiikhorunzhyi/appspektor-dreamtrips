package com.worldventures.dreamtrips.wallet.service.storage;

import java.io.File;

public class WizardMemoryStorage {

   private String barcode;
   private File userPhoto;
   private String firstName;
   private String middleName;
   private String lastName;

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

   @Deprecated
   public String getFullName() {
      return firstName + " " + middleName + " " + lastName;
   }

   public void saveName(String firstName, String middleName, String lastName) {
      this.firstName = firstName;
      this.middleName = middleName;
      this.lastName = lastName;
   }

   public String getFirstName() {
      return firstName;
   }

   public String getMiddleName() {
      return middleName;
   }

   public String getLastName() {
      return lastName;
   }
}
