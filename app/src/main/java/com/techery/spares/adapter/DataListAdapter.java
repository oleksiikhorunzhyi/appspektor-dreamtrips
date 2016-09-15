package com.techery.spares.adapter;

import com.techery.spares.loader.ContentLoader;

public interface DataListAdapter<T> {
   public void setContentLoader(ContentLoader<T> contentLoader);

   public ContentLoader<T> getContentLoader();

   Object getItem(int position);

   public interface Events {
      public class ItemSelectionEvent<T> {
         private final T item;

         public ItemSelectionEvent(T item) {
            this.item = item;
         }

         public T getItem() {
            return item;
         }
      }
   }
}
