package com.worldventures.dreamtrips.modules.facebook.model;

import java.io.Serializable;

public class FacebookAlbum implements Serializable {

   private String id;
   private String name;
   private int count;
   private FacebookCoverPhoto coverPhoto;

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public int getCount() {
      return count;
   }

   public String getCoverUrl(String token) {
      String id = coverPhoto == null ? "" : coverPhoto.getId();
      return "https://graph.facebook.com/" + id + "/picture?type=album&access_token=" + token + "&type=normal";
   }
}
