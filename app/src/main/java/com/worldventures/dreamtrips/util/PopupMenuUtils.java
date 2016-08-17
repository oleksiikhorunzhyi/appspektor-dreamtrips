package com.worldventures.dreamtrips.util;


import android.view.MenuItem;
import android.widget.PopupMenu;

public class PopupMenuUtils {
   private PopupMenuUtils() {
   }

   public static void convertItemsToUpperCase(PopupMenu popupMenu) {
      int itemCount = popupMenu.getMenu().size();
      for (int i = 0; i < itemCount; i++) {
         MenuItem menuItem = popupMenu.getMenu().getItem(i);
         String itemTitle = menuItem.getTitle().toString();
         menuItem.setTitle(itemTitle.toUpperCase());
      }
   }
}
