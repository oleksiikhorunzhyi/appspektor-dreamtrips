package com.worldventures.dreamtrips.modules.common.view.viewpager;

import android.os.Parcelable;
import android.support.v4.app.Fragment;

public class FragmentItem {
   public final String title;
   private final Class<? extends Fragment> fragmentClazz;
   private final Parcelable args;

   public FragmentItem(Class<? extends Fragment> fragmentClazz) {
      this(fragmentClazz, null);
   }

   public FragmentItem(Class<? extends Fragment> fragmentClazz, String title) {
      this(fragmentClazz, title, null);
   }

   public FragmentItem(Class<? extends Fragment> fragmentClazz, String title, Parcelable args) {
      this.title = title;
      this.fragmentClazz = fragmentClazz;
      this.args = args;
   }

   public Class<? extends Fragment> getFragmentClazz() {
      return fragmentClazz;
   }

   public Parcelable getArgs() {
      return args;
   }
}