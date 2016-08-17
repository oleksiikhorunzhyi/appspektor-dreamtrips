package com.worldventures.dreamtrips.modules.trips.model;

import java.io.Serializable;
import java.util.List;

public class TripDetails implements Serializable {
   public static final long serialVersionUID = 138L;

   private List<ContentItem> content;

   public List<ContentItem> getContent() {
      return content;
   }

   public void setContent(List<ContentItem> content) {
      this.content = content;
   }
}
