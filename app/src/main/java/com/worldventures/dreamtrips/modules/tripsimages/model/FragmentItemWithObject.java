package com.worldventures.dreamtrips.modules.tripsimages.model;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;

public class FragmentItemWithObject<T> extends FragmentItem {
   private T object;

   public FragmentItemWithObject(Route route, String title, T object) {
      super(route, title);
      this.object = object;
   }

   public T getObject() {
      return object;
   }
}