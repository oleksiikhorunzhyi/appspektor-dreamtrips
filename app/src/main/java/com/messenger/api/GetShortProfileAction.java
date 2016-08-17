package com.messenger.api;


import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/users/profiles/short", type = HttpAction.Type.SIMPLE, method = HttpAction.Method.POST)
public class GetShortProfileAction extends AuthorizedHttpAction {

   @Body ShortProfilesBody shortProfilesBody;

   @Response ArrayList<User> shortUsers;

   public GetShortProfileAction(List<String> userNames) {
      this.shortProfilesBody = new ShortProfilesBody(userNames);
   }

   public ArrayList<User> getShortUsers() {
      return shortUsers;
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////

   private class ShortProfilesBody {
      private List<String> usernames;

      public ShortProfilesBody(List<String> usernames) {
         this.usernames = usernames;
      }
   }
}
