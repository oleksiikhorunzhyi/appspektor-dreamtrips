package com.worldventures.core.modules.video.model;


import com.worldventures.core.model.Filterable;

import java.io.Serializable;

public class VideoLanguage implements Serializable, Filterable {

   private final String title;
   private final String localeName;

   public VideoLanguage(String title, String localeName) {
      this.title = title;
      this.localeName = localeName;
   }

   public String getTitle() {
      return title;
   }

   public String getLocaleName() {
      return localeName;
   }

   @Override
   public boolean containsQuery(String query) {
      return title.toLowerCase().contains(query.toLowerCase());
   }
}
