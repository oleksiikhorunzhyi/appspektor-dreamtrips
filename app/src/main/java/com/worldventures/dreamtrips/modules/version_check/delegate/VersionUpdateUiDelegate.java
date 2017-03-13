package com.worldventures.dreamtrips.modules.version_check.delegate;

public interface VersionUpdateUiDelegate {

   void showOptionalUpdateDialog(long timestamp);

   void showForceUpdateDialog();

   void openGooglePlayAppScreen();
}
