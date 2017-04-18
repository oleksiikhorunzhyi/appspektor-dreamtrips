package com.worldventures.dreamtrips.modules.infopages;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.delegate.system.DeviceInfoProvider;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle;

import java.util.Locale;

public class StaticPageProvider {

   ///////////////////////////////////////////
   //// URLS
   //////////////////////////////////////////

   private static final String ENROLL_MEMBER_URL = "/gateway/enroll_member?username=%username%&deviceId=%deviceId%";
   private static final String ENROLL_REP_URL = "/gateway/enroll_rep?username=%username%&deviceId=%deviceId%";
   private static final String LAT = "&lat=%1$,.6f";
   private static final String LNG = "&lng=%1$,.6f";
   private static final String BOOKING_PAGE_URL = "/gateway/booking_page/%trip_id%";
   private static final String OTA_PAGE_URL = "/gateway/ota_page";
   private static final String FAQ_URL = "/gateway/faq";
   private static final String TERMS_OF_SERVICE_URL = "/gateway/terms_of_use";
   private static final String PRIVACY_POLICY_URL = "/gateway/privacy_policy";
   private static final String COOKIES_POLICY_URL = "/gateway/cookies_policy";
   private static final String ENROLL_UPGRADE_URL = "/gateway/enroll_upgrade";

   ///////////////////////////////////////////
   //// Query params
   //////////////////////////////////////////

   private static final String MASK_DEVICE_ID = "%deviceId%";
   private static final String MASK_USER_NAME = "%username%";
   private static final String MASK_TRIP_ID = "%trip_id%";

   private SessionHolder<UserSession> appSessionHolder;
   private DeviceInfoProvider deviceInfoProvider;

   private String apiUrl;
   private String uploaderyUrl;

   public StaticPageProvider(SessionHolder<UserSession> appSessionHolder, DeviceInfoProvider deviceInfoProvider, String apiUrl, String uploaderyUrl) {
      this.appSessionHolder = appSessionHolder;
      this.deviceInfoProvider = deviceInfoProvider;
      this.apiUrl = apiUrl;
      this.uploaderyUrl = uploaderyUrl;
   }

   public String getEnrollMemberUrl() {
      String urlWithMask = apiUrl + ENROLL_MEMBER_URL;
      return urlWithMask.replace(MASK_USER_NAME, getUsername())
            .replace(MASK_DEVICE_ID, deviceInfoProvider.getUniqueIdentifier());
   }

   public String getEnrollRepUrl() {
      String urlWithMask = apiUrl + ENROLL_REP_URL;
      return urlWithMask.replace(MASK_USER_NAME, getUsername())
            .replace(MASK_DEVICE_ID, deviceInfoProvider.getUniqueIdentifier());
   }

   public String getEnrollUpgradeUrl() {
      return apiUrl + ENROLL_UPGRADE_URL;
   }

   public String getBookingPageUrl(String tripId) {
      String urlWithMask = apiUrl + BOOKING_PAGE_URL;
      return urlWithMask.replace(MASK_TRIP_ID, tripId);
   }

   public String getOtaPageUrl() {
      return apiUrl + OTA_PAGE_URL;
   }

   public String getUploaderyUrl() {
      return uploaderyUrl;
   }

   public String getFaqUrl() {
      return apiUrl + FAQ_URL;
   }

   public String getTermsOfServiceUrl() {
      return apiUrl + TERMS_OF_SERVICE_URL;
   }

   public String getCookiesPolicyUrl() {
      return apiUrl + COOKIES_POLICY_URL;
   }

   public String getPrivacyPolicyUrl() {
      return apiUrl + PRIVACY_POLICY_URL;
   }

   public String getEnrollMerchantUrl(MerchantIdBundle args) {
      StringBuilder builder = new StringBuilder(apiUrl);
      UserSession userSession = appSessionHolder.get().get();
      builder.append("/gateway/dtl/enroll_merchant")
            .append("?username=")
            .append(userSession.getUser().getUsername())
            .append("&sso=")
            .append(userSession.getLegacyApiToken())
            .append("&locale=")
            .append(userSession.getLocale());
      //
      if (args != null) {
         builder.append("&intent=suggestProspect").append("&prospectId=").append(args.getMerchantId());
      }
      return builder.toString();
   }

   public String getEnrollWithLocation(double latitude, double longitude) {
      return getEnrollMemberUrl() + String.format(Locale.ENGLISH, LAT, latitude) + String.format(Locale.ENGLISH, LNG, longitude);
   }

   public String getEnrollRepWithLocation(double latitude, double longitude) {
      return getEnrollRepUrl() + String.format(Locale.ENGLISH, LAT, latitude) + String.format(Locale.ENGLISH, LNG, longitude);
   }

   private String getUsername() {
      return appSessionHolder.get().get().getUser().getUsername();
   }
}
