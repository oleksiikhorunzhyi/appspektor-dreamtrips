package com.worldventures.dreamtrips.modules.trips.model;

import java.io.Serializable;

public class ContentItem implements Serializable {
   public static final long serialVersionUID = 138L;

   private String description;
   private String language;
   private String name;
   private String order;
   private String tag;

   public String getDescription() {
      return description != null ? description.replaceAll("\n", "").replaceAll("\t", "") : "";
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getLanguage() {
      return language;
   }

   public void setLanguage(String language) {
      this.language = language;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getOrder() {
      return order;
   }

   public void setOrder(String order) {
      this.order = order;
   }

   public String getTag() {
      return tag;
   }

   public void setTag(String tag) {
      this.tag = tag;
   }
}
