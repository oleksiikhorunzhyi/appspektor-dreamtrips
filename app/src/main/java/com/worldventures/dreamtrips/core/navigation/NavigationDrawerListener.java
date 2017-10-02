package com.worldventures.dreamtrips.core.navigation;

import com.worldventures.core.component.ComponentDescription;

public interface NavigationDrawerListener {
   void onNavigationDrawerItemSelected(ComponentDescription componentDescription);
   void onNavigationDrawerItemReselected(ComponentDescription componentDescription);
}
