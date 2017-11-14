package com.worldventures.dreamtrips.modules.common.view.adapter.item;

import android.support.v4.app.Fragment;

import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;

import java.io.Serializable;

public class DataFragmentItem<T extends Serializable> extends FragmentItem {
   public final T data;

   public DataFragmentItem(Class<? extends Fragment> fragmentClazz, String title, T data) {
      super(fragmentClazz, title);
      this.data = data;
   }
}
