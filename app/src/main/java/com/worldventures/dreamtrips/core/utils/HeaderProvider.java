package com.worldventures.dreamtrips.core.utils;

import android.os.Build;
import android.support.annotation.NonNull;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.janet.api_lib.NewDreamTripsHttpService;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.ArrayList;
import java.util.List;

public class HeaderProvider {

   private SessionHolder<UserSession> appSessionHolder;
   private LocaleHelper localeHelper;
   private  AppVersionNameBuilder appVersionNameBuilder;

   public HeaderProvider(SessionHolder<UserSession> appSessionHolder, LocaleHelper localeHelper,
         AppVersionNameBuilder appVersionNameBuilder) {
      this.appSessionHolder = appSessionHolder;
      this.localeHelper = localeHelper;
      this.appVersionNameBuilder = appVersionNameBuilder;
   }

   public List<Header> getAppHeaders() {
      ArrayList<Header> headers = new ArrayList<>();
      if (appSessionHolder.get().isPresent()) {
         headers.add(getAuthHeader());
      }
      headers.add(getAcceptLanguageHeader());
      headers.add(getApiVersionHeader());
      headers.add(getAppVersionHeader());
      headers.add(getAppPlatformHeader());
      return headers;
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
      return new Header("Accept-Language", localeHelper.getDefaultLocaleFormatted());
   }

   @NonNull
   public Header getAppPlatformHeader() {
      return new Header("DT-App-Platform", String.format("android-%d", Build.VERSION.SDK_INT));
   }

   @NonNull
   public Header getAppVersionHeader() {
      return new Header("DT-App-Version", appVersionNameBuilder.getSemanticVersionName());
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
