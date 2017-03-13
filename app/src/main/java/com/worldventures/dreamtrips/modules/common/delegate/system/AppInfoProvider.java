package com.worldventures.dreamtrips.modules.common.delegate.system;

import android.content.pm.PackageManager;

public interface AppInfoProvider {

   String getAppVersion() throws PackageManager.NameNotFoundException;
}
