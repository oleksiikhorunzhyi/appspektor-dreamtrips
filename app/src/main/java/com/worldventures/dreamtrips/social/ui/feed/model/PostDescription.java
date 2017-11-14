package com.worldventures.dreamtrips.social.ui.feed.model;

public class PostDescription {

   private String description;

   public PostDescription(String description) {
      this.description = description;
   }

   public PostDescription() {
      //do nothing
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getDescription() {
      return description;
   }
}
