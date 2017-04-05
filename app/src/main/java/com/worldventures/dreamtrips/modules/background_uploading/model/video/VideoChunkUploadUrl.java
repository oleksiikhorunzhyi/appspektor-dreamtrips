package com.worldventures.dreamtrips.modules.background_uploading.model.video;

import com.google.gson.annotations.SerializedName;

public class VideoChunkUploadUrl {

   @SerializedName("id")
   private int id;

   @SerializedName("url")
   private String url;

   public VideoChunkUploadUrl() {
   }

   public int id() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String url() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }
}
