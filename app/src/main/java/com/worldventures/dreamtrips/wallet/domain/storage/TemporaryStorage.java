package com.worldventures.dreamtrips.wallet.domain.storage;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TemporaryStorage {

   private boolean failInstall = false;

   @Inject
   TemporaryStorage() {
   }

   public boolean failInstall() {
      return failInstall;
   }

   public void failInstall(boolean failInstall) {
      this.failInstall = failInstall;
   }
}
