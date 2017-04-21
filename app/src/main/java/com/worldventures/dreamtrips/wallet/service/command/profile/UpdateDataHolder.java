package com.worldventures.dreamtrips.wallet.service.command.profile;

class UpdateDataHolder {

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
