package com.worldventures.dreamtrips.social.ui.bucketlist.model;

public class Suggestion {

   private String name;

   public Suggestion(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return name;
   }
}
