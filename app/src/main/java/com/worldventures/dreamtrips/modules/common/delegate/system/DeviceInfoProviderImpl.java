package com.worldventures.dreamtrips.modules.common.delegate.system;


import android.content.Context;
import android.provider.Settings;

public class DeviceInfoProviderImpl implements DeviceInfoProvider{
private Context context;

   public DeviceInfoProviderImpl(Context context) {
      this.context = context;
   }

   @Override
   public String getUniqueIdentifier() {
      return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
   }
}
