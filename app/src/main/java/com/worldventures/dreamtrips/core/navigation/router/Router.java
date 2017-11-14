package com.worldventures.dreamtrips.core.navigation.router;

import android.support.v4.app.Fragment;


public interface Router {
   void moveTo(Class<? extends Fragment> routeClazz, NavigationConfig navigationConfig);
   void back();
}
