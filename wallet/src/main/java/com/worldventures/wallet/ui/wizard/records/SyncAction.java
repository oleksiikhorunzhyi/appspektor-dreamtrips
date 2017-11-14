package com.worldventures.wallet.ui.wizard.records;

public enum SyncAction {
   TO_CARD,
   TO_DEVICE;

   public boolean isSyncToSmartCard() {
      return this == TO_CARD;
   }
}
