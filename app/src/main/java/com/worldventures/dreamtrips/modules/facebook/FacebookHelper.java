package com.worldventures.dreamtrips.modules.facebook;


import com.facebook.AccessToken;
import com.facebook.GraphResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class FacebookHelper {

   public final static List<String> LOGIN_PERMISSIONS = Collections.singletonList("user_photos");

   private Gson gson;

   public FacebookHelper(Gson gson) {
      this.gson = gson;
   }

   public boolean isLoggedIn() {
      AccessToken accesstoken = AccessToken.getCurrentAccessToken();
      return !(accesstoken == null || accesstoken.getPermissions().isEmpty());
   }

   public <T> List<T> processList(GraphResponse response, TypeToken<List<T>> typeToken) {
      if (response == null || response.getError() != null) {
         return new ArrayList<>();
      }
      if (response.getJSONObject() == null || !response.getJSONObject().has("data")) {
         return new ArrayList<>();
      }

      try {
         JSONArray array = response.getJSONObject().getJSONArray("data");
         return gson.fromJson(array.toString(), typeToken.getType());
      } catch (Exception e) {
         Timber.w(e, "Could not parse Facebook response");
         return new ArrayList<>();
      }
   }
}
