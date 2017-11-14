package com.worldventures.dreamtrips.core.utils;

import android.os.Build;
import android.support.annotation.NonNull;

import com.worldventures.core.utils.AppVersionNameBuilder;
import com.worldventures.core.utils.LocaleHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeaderProvider {

   public static final String ACCEPT_LANGUAGE = "Accept-Language";
   public static final String DT_APP_PLATFORM = "DT-App-Platform";
   public static final String DT_APP_VERSION = "DT-App-Version";
   public static final String APPLICATION_IDENTIFIER = "X-ApplicationIdentifier";

   private final AppVersionNameBuilder appVersionNameBuilder;

   public HeaderProvider(AppVersionNameBuilder appVersionNameBuilder) {
      this.appVersionNameBuilder = appVersionNameBuilder;
   }

   @NonNull
   public Header getAcceptLanguageHeader() {
      return new Header(ACCEPT_LANGUAGE, LocaleHelper.getDefaultLocaleFormatted());
   }

   @NonNull
   public Header getAppPlatformHeader() {
      return new Header(DT_APP_PLATFORM, String.format("android-%d", Build.VERSION.SDK_INT));
   }

   @NonNull
   public Header getAppVersionHeader() {
      return new Header(DT_APP_VERSION, appVersionNameBuilder.getSemanticVersionName());
   }

   @NonNull
   public Header getApplicationIdentifierHeader() {
      return new Header(APPLICATION_IDENTIFIER, "DTApp-Android-" + appVersionNameBuilder.getReleaseSemanticVersionName());
   }

   public Map<String, String> getStandardWebViewHeaders() {
      List<Header> headers = new ArrayList<>(Arrays.asList(new Header[]{
            getAppPlatformHeader(), getAppVersionHeader(), getAcceptLanguageHeader()}));
      Map<String, String> headersMap = new HashMap<>();
      for (Header header : headers) {
         headersMap.put(header.getName(), header.getValue());
      }
      return headersMap;
   }

   public static class Header {

      private final String name;
      private final String value;

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
