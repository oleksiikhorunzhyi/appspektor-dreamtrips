package com.worldventures.dreamtrips.modules.infopages;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle;

import javax.inject.Inject;

public class StaticPageProvider {

    private static final String ENDPOINT = BuildConfig.DreamTripsApi;

    ///////////////////////////////////////////
    //// URLS
    //////////////////////////////////////////

    private static final String ENROLL_MEMBER_URL = ENDPOINT + "/gateway/enroll_member?userId=%user_id%";
    private static final String ENROLL_REP_URL = ENDPOINT + "/gateway/enroll_rep?userId=%user_id%";
    private static final String BOOKING_PAGE_URL = ENDPOINT + "/gateway/booking_page/%trip_id%";
    private static final String OTA_PAGE_URL = ENDPOINT + "/gateway/ota_page";
    private static final String FAQ_URL = ENDPOINT + "/gateway/faq";
    private static final String TERMS_OF_SERVICE_URL = ENDPOINT + "/gateway/terms_of_use";
    private static final String PRIVACY_POLICY_URL = ENDPOINT + "/gateway/privacy_policy";
    private static final String COOKIES_POLICY_URL = ENDPOINT + "/gateway/cookies_policy";
    private static final String ENROLL_UPGRADE_URL = ENDPOINT + "/gateway/enroll_upgrade";

    ///////////////////////////////////////////
    //// Query params
    //////////////////////////////////////////

    private static final String USER_ID = "%user_id%";
    private static final String TRIP_ID = "%trip_id%";

    private SessionHolder<UserSession> appSessionHolder;

    @Inject
    public StaticPageProvider(SessionHolder<UserSession> appSessionHolder) {
        this.appSessionHolder = appSessionHolder;
    }

    public String getEnrollMemberUrl() {
        return ENROLL_MEMBER_URL.replace(USER_ID,
                String.valueOf(appSessionHolder.get().get().getUser().getId()));
    }

    public String getEnrollRepUrl() {
        return ENROLL_REP_URL.replace(USER_ID,
                String.valueOf(appSessionHolder.get().get().getUser().getId()));
    }

    public String getEnrollUpgradeUrl() {
        return ENROLL_UPGRADE_URL;
    }

    public String getBookingPageUrl(String tripId) {
        return BOOKING_PAGE_URL.replace(TRIP_ID, tripId);
    }

    public String getOtaPageUrl() {
        return OTA_PAGE_URL;
    }

    public String getUploaderyUrl() {
        return BuildConfig.UPLOADERY_API_URL;
    }

    public String getFaqUrl() {
        return FAQ_URL;
    }

    public String getTermsOfServiceUrl() {
        return TERMS_OF_SERVICE_URL;
    }

    public String getCookiesPolicyUrl() {
        return COOKIES_POLICY_URL;
    }

    public String getPrivacyPolicyUrl() {
        return PRIVACY_POLICY_URL;
    }

    public String getEnrollMerchantUrl(MerchantIdBundle args) {
        StringBuilder builder = new StringBuilder(BuildConfig.DreamTripsApi);
        builder.append("/gateway/dtl/enroll_merchant")
                .append("?username=").append(appSessionHolder.get().get().getUsername())
                .append("&sso=").append(appSessionHolder.get().get().getLegacyApiToken())
                .append("&locale=").append(appSessionHolder.get().get().getLocale());
        //
        if (args != null) {
            builder.append("&intent=suggestProspect").append("&prospectId=")
                    .append(args.getMerchantId());
        }
        return builder.toString();
    }

}
