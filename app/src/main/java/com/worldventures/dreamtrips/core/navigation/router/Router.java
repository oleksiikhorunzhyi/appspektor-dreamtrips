package com.worldventures.dreamtrips.core.navigation.router;

import com.worldventures.dreamtrips.core.navigation.Route;

public interface Router {
   void moveTo(Route route, NavigationConfig navigationConfig);
   void moveTo(Route route);
   void back();
}
