package com.worldventures.dreamtrips.wallet.service.provisioning;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

@SuppressLint("ApplySharedPref")
class ProvisioningModeStorageImpl implements ProvisioningModeStorage {

   private static final String SHARE_PREFERENCES_NAME = "ProvisioningStateStorage";
   private static final String PROVISIONING_STATE_NAME = "ProvisioningStateStorage";

   private final SharedPreferences sharedPreferences;

   ProvisioningModeStorageImpl(Context context) {
      sharedPreferences = context.getSharedPreferences(SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
   }

   @Override
   public void saveState(ProvisioningMode state) {
      sharedPreferences.edit()
            .putString(PROVISIONING_STATE_NAME, state.toString())
            .commit();
   }

   @Override
   public ProvisioningMode getState() {
      String provisioningName = sharedPreferences.getString(PROVISIONING_STATE_NAME, null);
      if (provisioningName == null) return null;
      return ProvisioningMode.valueOf(provisioningName);
   }

   @Override
   public void clear() {
      sharedPreferences.edit()
            .remove(PROVISIONING_STATE_NAME)
            .commit();
   }
}
