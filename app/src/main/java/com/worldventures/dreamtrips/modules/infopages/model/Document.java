package com.worldventures.dreamtrips.modules.infopages.model;

public class Document {

   private String name;
   private String url;

   public Document(String name, String url) {
      this.name = name;
      this.url = url;
   }

   public String getName() {
      return name;
   }

   public String getUrl() {
      return url;
   }
}
