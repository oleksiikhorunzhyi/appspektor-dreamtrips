package com.worldventures.dreamtrips.modules.config.delegate;

public interface VersionUpdateUiDelegate {

   void showOptionalUpdateDialog(long timestamp);

   void showForceUpdateDialog();

   void openGooglePlayAppScreen();
}
