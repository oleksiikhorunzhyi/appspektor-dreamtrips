package com.worldventures.dreamtrips.modules.common.view.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.techery.spares.adapter.ListAdapter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class BasePagerAdapter<T extends FragmentItem> extends FragmentPagerAdapter implements ListAdapter<T> {

   protected List<T> fragmentItems = new ArrayList<>();
   protected Fragment currentFragment;

   public BasePagerAdapter(FragmentManager fm) {
      super(fm);
   }

   @Override
   public void addItems(List<T> baseItemClasses) {
      fragmentItems.addAll(baseItemClasses);
   }

   public void add(T item) {
      fragmentItems.add(item);
   }

   public void remove(int index) {
      fragmentItems.remove(index);
   }

   public T getFragmentItem(int i) {
      return fragmentItems.get(i);
   }

   @Override
   public void clear() {
      fragmentItems.clear();
   }

   private Fragment getFragment(int i) {
      try {
         FragmentItem fragmentItem = fragmentItems.get(i);
         Fragment fragment = fragmentItem.route.getClazz().newInstance();
         if (fragment instanceof BaseFragmentWithArgs) {
            ((BaseFragmentWithArgs) fragment).setArgs(fragmentItem.getArgs());
         }
         setArgs(i, fragment);
         return fragment;
      } catch (Exception e) {
         Timber.e(e, "");
      }
      return null;
   }

   public void setArgs(int position, Fragment fragment) {
   }

   @Override
   public void setPrimaryItem(ViewGroup container, int position, Object object) {
      super.setPrimaryItem(container, position, object);
      currentFragment = (Fragment) object;
   }

   public Fragment getCurrentFragment() {
      return currentFragment;
   }

   @Override
   public CharSequence getPageTitle(int position) {
      return fragmentItems.get(position).title;
   }

   @Override
   public Fragment getItem(int i) {
      return getFragment(i);
   }

   @Override
   public int getCount() {
      return fragmentItems.size();
   }

   @Override
   public void notifyDataSetChanged() {
      super.notifyDataSetChanged();
   }
}