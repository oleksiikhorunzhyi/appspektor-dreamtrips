package com.worldventures.core.modules.infopages;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.service.DeviceInfoProvider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
   private static final String PRIVACY_POLICY_URL = "/gateway/privacy_policy";
   private static final String COOKIES_POLICY_URL = "/gateway/cookies_policy";
   private static final String ENROLL_UPGRADE_URL = "/gateway/enroll_upgrade";
   private static final String WV_ADVANTAGE_URL = "/Account/Dispatch?url=";
   private static final String WV_ADVANTAGE_URL_TO_ENCODE = "/Marketing/WorldVenturesAdvantage";

   ///////////////////////////////////////////
   //// Query params
   //////////////////////////////////////////

   private static final String MASK_DEVICE_ID = "%deviceId%";
   private static final String MASK_USER_NAME = "%username%";
   private static final String MASK_TRIP_ID = "%trip_id%";

   private final SessionHolder appSessionHolder;
   private final DeviceInfoProvider deviceInfoProvider;

   private final String apiUrl;
   private final String backofficeUrl;
   private final String forgotPasswordUrl;
   private final String uploaderyUrl;

   public StaticPageProvider(StaticPageProviderConfig config) {
      this.appSessionHolder = config.appSessionHolder();
      this.deviceInfoProvider = config.deviceInfoProvider();
      this.apiUrl = config.apiUrl();
      this.backofficeUrl = config.backofficeUrl();
      this.uploaderyUrl = config.uploaderyUrl();
      this.forgotPasswordUrl = config.forgotPasswordUrl();
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

   public String getCookiesPolicyUrl() {
      return apiUrl + COOKIES_POLICY_URL;
   }

   public String getPrivacyPolicyUrl() {
      return apiUrl + PRIVACY_POLICY_URL;
   }

   public String getEnrollWithLocation(double latitude, double longitude) {
      return getEnrollMemberUrl() + String.format(Locale.ENGLISH, LAT, latitude) + String.format(Locale.ENGLISH, LNG, longitude);
   }

   public String getEnrollRepWithLocation(double latitude, double longitude) {
      return getEnrollRepUrl() + String.format(Locale.ENGLISH, LAT, latitude) + String.format(Locale.ENGLISH, LNG, longitude);
   }

   public String getWvAdvantageUrl() {
      try {
         return backofficeUrl + WV_ADVANTAGE_URL + URLEncoder.encode(WV_ADVANTAGE_URL_TO_ENCODE, "UTF-8");
      } catch (UnsupportedEncodingException e) {
         return "about:blank";
      }
   }

   public String getForgotPasswordUrl() {
      return forgotPasswordUrl + "?dreamtrips";
   }

   public String getForgotMemberIdUrl() {
      return forgotPasswordUrl + "/forgotLoginId?dreamtrips";
   }

   private String getUsername() {
      return appSessionHolder.get().get().user().getUsername();
   }
}
