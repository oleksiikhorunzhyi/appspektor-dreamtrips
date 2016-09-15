package com.worldventures.dreamtrips.modules.common.view.viewpager;

import com.worldventures.dreamtrips.core.navigation.Route;

public class FragmentItem {
   public final Route route;
   public final String title;

   public FragmentItem(Route route, String title) {
      this.route = route;
      this.title = title;
   }
}