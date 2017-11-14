package com.worldventures.wallet.util;

public interface WalletBuildConfigHelper {

   boolean isDebug();

   boolean isEmulatorModeEnabled();

   boolean useNxtClient();

   String getAppPackage();
}
