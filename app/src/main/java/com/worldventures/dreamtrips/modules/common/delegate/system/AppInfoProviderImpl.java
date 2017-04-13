package com.worldventures.dreamtrips.modules.common.delegate.system;

import android.content.Context;
import android.content.pm.PackageManager;

public class AppInfoProviderImpl implements AppInfoProvider {

   private Context context;

   public AppInfoProviderImpl(Context context) {
      this.context = context;
   }

   @Override
   public String getAppVersion() throws PackageManager.NameNotFoundException {
      return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
   }
}
