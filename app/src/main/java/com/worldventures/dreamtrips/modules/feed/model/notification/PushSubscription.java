package com.worldventures.dreamtrips.modules.feed.model.notification;

import com.google.gson.annotations.SerializedName;

public class PushSubscription {

   public final String token;
   public final String platform;
   @SerializedName("app_version") public final String appVersion;
   @SerializedName("os_version") public final String osVersion;

   public PushSubscription(String token, String platform, String appVersion, String osVersion) {
      this.platform = platform;
      this.token = token;
      this.appVersion = appVersion;
      this.osVersion = osVersion;
   }
}
