package com.worldventures.dreamtrips.util;


import android.support.v4.view.ViewPager;

public class PageSelectionDetector {

   private PageSelectionDetector() {
   }

   public static void listenPageSelection(ViewPager viewPager, PageSelectionListener listener) {
      viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

         //It's better to listen this method instead of onPageSelected. Because:
         //this method is called on very first time, when user opens screen with viewpager and first page is shown by default
         //also it's fired during restore previous state, when user navigates between different fragments on one Activity
         @Override
         public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // position positionOffsetPixels with value 0 means that page is completely scrolled
            if (positionOffsetPixels == 0) {
               listener.pageSelected(position);
            }
         }

      });
   }

   public interface PageSelectionListener {

      void pageSelected(int pageNumber);

   }

}
