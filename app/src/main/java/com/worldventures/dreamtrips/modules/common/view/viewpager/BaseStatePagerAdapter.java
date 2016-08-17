package com.worldventures.dreamtrips.modules.common.view.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.techery.spares.adapter.IRoboSpiceAdapter;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class BaseStatePagerAdapter<T extends FragmentItem> extends FragmentStatePagerAdapter implements IRoboSpiceAdapter<T> {
   protected List<T> fragmentItems = new ArrayList<>();

   public BaseStatePagerAdapter(FragmentManager fm) {
      super(fm);
   }

   public void add(T item) {
      fragmentItems.add(item);
   }

   @Override
   public void addItems(List<T> items) {
      fragmentItems.addAll(items);
   }

   public void remove(int index) {
      fragmentItems.remove(index);
   }

   @Override
   public void clear() {
      fragmentItems.clear();
   }

   private Fragment getFragment(int i) {
      try {
         Fragment value = fragmentItems.get(i).route.getClazz().newInstance();
         setArgs(i, value);
         return value;
      } catch (Exception e) {
         Timber.e(e, "");
      }
      return null;
   }

   public void setArgs(int position, Fragment fragment) {
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