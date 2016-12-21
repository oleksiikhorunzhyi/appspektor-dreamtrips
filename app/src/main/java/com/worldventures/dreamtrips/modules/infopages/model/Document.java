package com.worldventures.dreamtrips.modules.infopages.model;

public class Document {

   private int id;

   private String name;

   private String url;

   public Document(int id, String name, String url) {
      this.id = id;
      this.name = name;
      this.url = url;
   }

   public int getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getUrl() {
      return url;
   }
}
