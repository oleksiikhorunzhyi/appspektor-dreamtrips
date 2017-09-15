package com.worldventures.dreamtrips.social.ui.background_uploading.service.command.video.http;

import io.techery.janet.http.annotations.RequestHeader;

public abstract class BaseVideoHttpAction {

   @RequestHeader("X-ApplicationIdentifier") String identifier;
   @RequestHeader("Accept") final String acceptJson = "application/json";

   public void setIdentifier(String identifier) {
      this.identifier = identifier;
   }

   public abstract void setMemberId(String memberId);

   public abstract void setSsoToken(String ssoToken);

   public abstract String getSsoToken();
}
