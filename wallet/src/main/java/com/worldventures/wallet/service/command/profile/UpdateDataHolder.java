package com.worldventures.wallet.service.command.profile;

public class UpdateDataHolder {

   private ChangedFields changedFields;

   void saveChanging(ChangedFields changedFields) {
      this.changedFields = changedFields;
   }

   ChangedFields getChangedFields() {
      return changedFields;
   }

   void clear() {
      changedFields = null;
   }

}
