package com.messenger.messengerservers.model;

import com.google.gson.annotations.SerializedName;

public class ImageAttachment implements Attachment {

   @SerializedName("origin_url") private String originUrl;

   public String getOriginUrl() {
      return originUrl;
   }

   public void setOriginUrl(String originUrl) {
      this.originUrl = originUrl;
   }

   public ImageAttachment(String originUrl) {
      this.originUrl = originUrl;
   }
}
