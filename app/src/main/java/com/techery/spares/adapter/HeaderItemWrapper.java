package com.techery.spares.adapter;

public class HeaderItemWrapper<T> implements HeaderItem {

   private final T item;
   private final String header;

   public HeaderItemWrapper(T item, String header) {
      this.item = item;
      this.header = header;
   }

   @Override
   public String getHeaderTitle() {
      return header;
   }

   public T getItem() {
      return item;
   }
}
