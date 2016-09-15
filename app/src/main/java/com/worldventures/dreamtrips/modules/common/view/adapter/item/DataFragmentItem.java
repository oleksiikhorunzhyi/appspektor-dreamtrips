package com.worldventures.dreamtrips.modules.common.view.adapter.item;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;

import java.io.Serializable;

public class DataFragmentItem<T extends Serializable> extends FragmentItem {
   public final T data;

   public DataFragmentItem(Route route, String title, T data) {
      super(route, title);
      this.data = data;
   }
}
