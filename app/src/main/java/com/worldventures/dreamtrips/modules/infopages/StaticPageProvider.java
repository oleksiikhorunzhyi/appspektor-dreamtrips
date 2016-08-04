package com.worldventures.dreamtrips.modules.infopages;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.preference.StaticPageHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle;

public class StaticPageProvider {

    private static final String ENDPOINT = BuildConfig.DreamTripsApi;

    ///////////////////////////////////////////
    //// URLS
    //////////////////////////////////////////

    private static final String ENROLL_MEMBER_URL = ENDPOINT + "/gateway/enroll_member?userId=%user_id%";
    private static final String ENROLL_REP_URL = ENDPOINT + "/gateway/enroll_rep?userId=%user_id%";
    private static final String BOOKING_PAGE_URL = ENDPOINT + "/gateway/booking_page/%trip_id%";
    private static final String OTA_PAGE_URL = ENDPOINT + "/gateway/ota_page";

    ///////////////////////////////////////////
    //// Query params
    //////////////////////////////////////////

    private static final String USER_ID = "%user_id%";
    private static final String TRIP_ID = "%trip_id%";

    public String getEnrollMemberUrl() {
        return ENROLL_MEMBER_URL.replace(USER_ID,
                String.valueOf(appSessionHolder.get().get().getUser().getId()));
    }

    public String getEnrollRepUrl() {
        return ENROLL_REP_URL.replace(USER_ID,
                String.valueOf(appSessionHolder.get().get().getUser().getId()));
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

    ///////////////
    //////////////
    //////////////

    private static final String PRIVACY_TITLE = "Privacy Policy";
    private static final String COOKIE_TITLE = "Cookie Policy";
    private static final String FAQ_TITLE = "FAQ";
    private static final String TERMS_TITLE = "Terms of Use";

    private StaticPageHolder storage;
    private SessionHolder<UserSession> appSessionHolder;
    private LocaleHelper localeHelper;

    public StaticPageProvider(StaticPageHolder storage, SessionHolder<UserSession> appSessionHolder, LocaleHelper localeHelper) {
        this.storage = storage;
        this.appSessionHolder = appSessionHolder;
        this.localeHelper = localeHelper;
    }

    public String getStaticInfoUrl(String title) {
        if (storage.get().isPresent())
            return getLocalizedUrl(storage.get().get().getUrlByTitle(title));
        else return "";
    }

    public String getEnrollMerchantUrl(MerchantIdBundle args) {
        StringBuilder builder = new StringBuilder(BuildConfig.DreamTripsApi);
        builder.append("/gateway/dtl/enroll_merchant")
                .append("?username=").append(appSessionHolder.get().get().getUsername())
                .append("&sso=").append(appSessionHolder.get().get().getLegacyApiToken())
                .append("&locale=").append(appSessionHolder.get().get().getUser().getLocale());
        //
        if (args != null) {
            builder.append("&intent=suggestProspect").append("&prospectId=")
                    .append(args.getMerchantId());
        }
        return builder.toString();
    }

    public String getTermsOfServiceUrl() {
        return getStaticInfoUrl(TERMS_TITLE);
    }

    public String getCookiePolicyUrl() {
        return getStaticInfoUrl(COOKIE_TITLE);
    }

    public String getFaqUrl() {
        return getStaticInfoUrl(FAQ_TITLE);
    }

    public String getPrivacyPolicyUrl() {
        return getStaticInfoUrl(PRIVACY_TITLE);
    }

    private String getLocalizedUrl(String url) {
        return url.replace(AppConfig.LOCALE, localeHelper.getDefaultLocaleFormatted())
                .replace(AppConfig.COUNTRY, localeHelper.getDefaultLocale().getCountry())
                .replace(AppConfig.LANGUAGE, localeHelper.getDefaultLocale().getLanguage());
    }
}
