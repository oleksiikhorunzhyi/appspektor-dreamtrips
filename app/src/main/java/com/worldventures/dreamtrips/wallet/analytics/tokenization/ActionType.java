package com.worldventures.dreamtrips.wallet.analytics.tokenization;

public enum ActionType {

   ADD("Add"),
   UPDATE("Update"),
   RESTORE("Restore");

   private String typeLabel;

   ActionType(String typeLabel) {
      this.typeLabel = typeLabel;
   }

   public String getTypeLabel() {
      return typeLabel;
   }

}