package com.worldventures.dreamtrips.core.utils;

import android.os.Build;
import android.support.annotation.NonNull;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.janet.api_lib.NewDreamTripsHttpService;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.HashMap;
import java.util.Map;

public class HeaderProvider {

   private SessionHolder<UserSession> appSessionHolder;
   private  AppVersionNameBuilder appVersionNameBuilder;

   public HeaderProvider(SessionHolder<UserSession> appSessionHolder,
         AppVersionNameBuilder appVersionNameBuilder) {
      this.appSessionHolder = appSessionHolder;
      this.appVersionNameBuilder = appVersionNameBuilder;
   }

   @NonNull
   public Header getAuthHeader() {UserSession userSession = appSessionHolder.get().get();
      String authToken = NewDreamTripsHttpService.getAuthorizationHeader(userSession.getApiToken());
      return new Header("Authorization", authToken);
   }

   @NonNull
   public Header getApiVersionHeader() {
      return new Header("Accept", "application/com.dreamtrips.api+json;version=" + BuildConfig.API_VERSION);
   }

   @NonNull
   public Header getAcceptLanguageHeader() {
      return new Header("Accept-Language", LocaleHelper.getDefaultLocaleFormatted());
   }

   @NonNull
   public Header getAppPlatformHeader() {
      return new Header("DT-App-Platform", String.format("android-%d", Build.VERSION.SDK_INT));
   }

   @NonNull
   public Header getAppVersionHeader() {
      return new Header("DT-App-Version", appVersionNameBuilder.getSemanticVersionName());
   }

   public Map<String, String> getStandardWebViewHeaders() {
      Map<String, String> headers = new HashMap<>();
      HeaderProvider.Header appPlatformHeader = getAppPlatformHeader();
      headers.put(appPlatformHeader.getName(), appPlatformHeader.getValue());
      HeaderProvider.Header appVersionHeader = getAppVersionHeader();
      headers.put(appVersionHeader.getName(), appVersionHeader.getValue());
      return headers;
   }

   public static class Header {
      private String name;
      private String value;

      public Header(String name, String value) {
         this.name = name;
         this.value = value;
      }

      public String getName() {
         return name;
      }

      public String getValue() {
         return value;
      }
   }
}
