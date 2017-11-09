package com.worldventures.dreamtrips.wallet;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.wallet.util.WalletBuildConfigHelper;

public class WalletBuildConfigHelperImpl implements WalletBuildConfigHelper {

   @Override
   public boolean isDebug() {
      return BuildConfig.DEBUG;
   }

   @Override
   public boolean isEmulatorModeEnabled() {
      return BuildConfig.WALLET_EMULATOR_MODE;
   }

   @Override
   public boolean useNxtClient() {
      return "nxtid".equals(BuildConfig.SMART_CARD_SDK_CLIENT);
   }

   @Override
   public String getAppPackage() {
      return BuildConfig.APPLICATION_PACKAGE_PROD;
   }
}
