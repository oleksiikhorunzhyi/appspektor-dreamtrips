package com.worldventures.dreamtrips.modules.common.view.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.HandlerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.badoo.mobile.util.WeakHandler;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.model.Filterable;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.utils.CrashlyticsTracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import icepick.Icepick;
import icepick.State;


public class FilterableArrayListAdapter<T extends Filterable> extends BaseDelegateAdapter<T> {

   protected volatile List<T> cachedItems;
   @State volatile String query;

   protected WeakHandler mainHandler;
   protected WeakHandler filterHandler;

   protected Comparator comparator;

   public FilterableArrayListAdapter(Context context, Injector injector) {
      super(context, injector);
      cachedItems = new ArrayList<>();

      mainHandler = new WeakHandler();
      HandlerThread filterThread = new HandlerThread("filter");
      filterThread.start();
      filterHandler = new WeakHandler(filterThread.getLooper());
   }

   public void saveState(Bundle outState) {
      Icepick.saveInstanceState(this, outState);
   }

   public void restoreState(Bundle savedState) {
      Icepick.restoreInstanceState(this, savedState);
   }

   public String getQuery() {
      return query;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Filter manipulation
   ///////////////////////////////////////////////////////////////////////////

   public void setFilter(String query) {
      if (cachedItems.isEmpty()) cachedItems.addAll(items);
      this.query = query;
      if (TextUtils.isEmpty(query)) {
         flushFilter();
      } else {
         filterHandler.post(() -> {
            try {
               // don't remove this statement!!!
               if (query != null) {
                  List<T> filtered = Queryable.from(cachedItems)
                        .filter(element -> element.containsQuery(this.query.toLowerCase()))
                        .toList();

                  mainHandler.post(() -> {
                     items.clear();
                     items.addAll(filtered);
                     if (comparator != null) Collections.sort(items, comparator);
                     notifyDataSetChanged();
                  });
               }
            } catch (Exception ex) {
               //TODO remove it when issue from Fabric (#277) will be fixed
               ArrayMap<String, Object> params = new ArrayMap<>(2);
               params.put("query string", this.query);
               params.put("items", items == null ? "null pointer" : String.valueOf(items.size()));

               CrashlyticsTracker.trackErrorWithParams(ex, params);
            }
         });
      }
   }

   public void setFilteredItems(List<T> filteredItems) {
      if (cachedItems.isEmpty()) cachedItems.addAll(items);
      mainHandler.post(() -> {
         items.clear();
         items.addAll(filteredItems);
         notifyDataSetChanged();
      });
   }

   public void sort(Comparator comparator) {
      Collections.sort(items, comparator);
      notifyDataSetChanged();
   }

   public void setDefaultComparator(Comparator comparator) {
      this.comparator = comparator;
   }

   public void flushFilter() {
      if (query != null) {
         query = null;
         items.clear();
         items.addAll(cachedItems);
         if (comparator != null) Collections.sort(items, comparator);
         notifyDataSetChanged();
         filterHandler.post(cachedItems::clear);
      }
   }

   ///////////////////////////////////////////////////////////////////////////
   // Items modification proxy
   ///////////////////////////////////////////////////////////////////////////

   public void moveItemSafely(T itemClass, int to) {
      int indexOfItem = items.indexOf(itemClass);
      int targetPosition = to;

      if (targetPosition >= getCount()) {
         targetPosition = getCount();
      }

      moveItem(indexOfItem, targetPosition);
      notifyItemMoved(indexOfItem, targetPosition);
   }

   @Override
   public void addItem(int location, T item) {
      if (query == null) super.addItem(location, item);
      else {
         filterHandler.post(() -> cachedItems.add(location, item));
         setFilter(query);
      }
   }

   @Override
   public void addItems(List<T> items) {
      if (query == null) super.addItems(items);
      else {
         filterHandler.post(() -> cachedItems.addAll(items));
         setFilter(query);
      }
   }

   @Override
   public void replaceItem(int location, T item) {
      if (query == null) super.replaceItem(location, item);
      else {
         filterHandler.post(() -> cachedItems.set(location, item));
         setFilter(query);
      }
   }

   @Override
   public void remove(int location) {
      if (query == null) super.remove(location);
      else {
         filterHandler.post(() -> cachedItems.remove(location));
         setFilter(query);
      }
   }

   @Override
   public void setItems(List<T> items) {
      this.items = items;
      if (query == null) notifyDataSetChanged();
      else {
         filterHandler.post(() -> {
            cachedItems.clear();
            cachedItems.addAll(items);
         });
         setFilter(query);
      }
   }

   @Override
   public void clear() {
      super.clear();
      filterHandler.post(() -> cachedItems.clear());
   }
}
