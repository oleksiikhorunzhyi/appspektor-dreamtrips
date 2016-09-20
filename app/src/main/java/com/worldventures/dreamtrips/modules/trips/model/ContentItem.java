package com.worldventures.dreamtrips.modules.trips.model;

import java.io.Serializable;
import java.util.List;

public class ContentItem implements Serializable {
   public static final long serialVersionUID = 138L;

   private String description;
   private String language;
   private String name;
   private List<String> tags;

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

   public List<String> getTags() {
      return tags;
   }

   public void setTags(List<String> tags) {
      this.tags = tags;
   }
}
