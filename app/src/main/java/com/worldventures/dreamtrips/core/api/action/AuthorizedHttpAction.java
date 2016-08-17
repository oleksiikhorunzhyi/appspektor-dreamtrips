package com.worldventures.dreamtrips.core.api.action;

import io.techery.janet.http.annotations.RequestHeader;

public class AuthorizedHttpAction extends BaseHttpAction {

   @RequestHeader("Authorization") public String authorizationHeader;

   public void setAuthorizationHeader(String authorizationHeader) {
      this.authorizationHeader = authorizationHeader;
   }

   public String getAuthorizationHeader() {
      return authorizationHeader;
   }
}
