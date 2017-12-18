package com.worldventures.wallet.ui.settings.general.display.impl;

public enum DisplayOptionsSource {
   PROFILE, SETTINGS;

   public boolean isSettings() {
      return this == SETTINGS;
   }
}
