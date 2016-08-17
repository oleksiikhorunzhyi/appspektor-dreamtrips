package com.worldventures.dreamtrips.modules.friends.model;

public class RequestHeaderModel {

   boolean advanced;
   String name;
   private int count;

   public RequestHeaderModel(String name) {
      this(name, false);
   }

   public RequestHeaderModel(String name, boolean advanced) {
      this.name = name;
      this.advanced = advanced;
   }

   public boolean isAdvanced() {
      return advanced;
   }

   public String getName() {
      return name;
   }

   public int getCount() {
      return count;
   }

   public void setCount(int count) {
      this.count = count;
   }
}
