package com.worldventures.dreamtrips.core.navigation;

import com.worldventures.dreamtrips.core.component.ComponentDescription;

public interface NavigationDrawerListener {
   void onNavigationDrawerItemSelected(ComponentDescription componentDescription);
   void onNavigationDrawerItemReselected(ComponentDescription componentDescription);
}
