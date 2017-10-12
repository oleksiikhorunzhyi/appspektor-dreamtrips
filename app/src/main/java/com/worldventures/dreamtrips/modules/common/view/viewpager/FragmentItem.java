package com.worldventures.dreamtrips.modules.common.view.viewpager;

import android.os.Parcelable;

import com.worldventures.dreamtrips.core.navigation.Route;

public class FragmentItem {
   public final Route route;
   public final String title;
   private Parcelable args;

   public FragmentItem(Route route, String title) {
      this.route = route;
      this.title = title;
   }

   public FragmentItem(Route route, String title, Parcelable args) {
      this.route = route;
      this.title = title;
      this.args = args;
   }

   public Parcelable getArgs() {
      return args;
   }
}