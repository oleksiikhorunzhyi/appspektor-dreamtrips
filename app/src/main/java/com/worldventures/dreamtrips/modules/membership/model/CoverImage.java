package com.worldventures.dreamtrips.modules.membership.model;

import java.io.Serializable;

public class CoverImage implements Serializable {

   private String originUrl;
   private String url;

   public String getOriginUrl() {
      return originUrl;
   }

   public String getUrl() {
      return url;
   }
}
