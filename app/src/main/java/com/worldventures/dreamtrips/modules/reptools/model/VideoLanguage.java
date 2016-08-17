package com.worldventures.dreamtrips.modules.reptools.model;


import com.worldventures.dreamtrips.modules.common.view.util.Filterable;

import java.io.Serializable;

public class VideoLanguage implements Serializable, Filterable {

   String title;
   String localeName;

   public String getTitle() {
      return title;
   }

   public String getLocaleName() {
      return localeName;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public void setLocaleName(String localeName) {
      this.localeName = localeName;
   }

   @Override
   public boolean containsQuery(String query) {
      return title.toLowerCase().contains(query.toLowerCase());
   }
}
