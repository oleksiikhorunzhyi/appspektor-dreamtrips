package com.worldventures.dreamtrips.modules.video.model;


import com.worldventures.dreamtrips.modules.common.view.util.Filterable;

import java.io.Serializable;

public class VideoLanguage implements Serializable, Filterable {

   private String title;
   private String localeName;

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
