package com.worldventures.dreamtrips.wallet.ui.settings.general.display;

public enum DisplayOptionsSource {
   PROFILE, SETTINGS;

   public boolean isSettings() {
      return this == SETTINGS;
   }
}
