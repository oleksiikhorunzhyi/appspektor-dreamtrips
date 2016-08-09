package com.worldventures.dreamtrips.modules.common.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.core.utils.ProjectTextUtils;

public class AppConfig {

    public static final String TRIP_ID = "{tripid}";
    public static final String USER_ID = "{userid}";
    public static final String TOKEN = "{tokenval}";
    public static final String LOCALE = "{locale}";
    public static final String COUNTRY = "{country}";
    public static final String LANGUAGE = "{language}";
    public static final String ENROLL_UID = "{BASE64_ENCODED_USERID}";
    public static final String SPONSOR_ID_BASE_64 = "{BASE64_ENCODED_SPONSORID}";
    public static final String COUNTRY_BASE_64 = "{BASE64_ENCODED_LANGUAGE}";
    public static final String LANGUAGE_BASE_64 = "{BASE64_ENCODED_COUNTRY}";

    @SerializedName("URLS")
    protected URLS urls;

    @SerializedName("server_status")
    protected ServerStatus serverStatus;

    public URLS getUrls() {
        return this.urls;
    }

    public ServerStatus getServerStatus() {
        return serverStatus;
    }

    public static class URLS {

        @SerializedName("Production")
        private Config production;

        public Config getProduction() {
            return this.production;
        }

        public static class Config {

            @SerializedName("AuthBaseURL")
            private String authBaseURL;

            @SerializedName("EnrollMemberURL")
            private String enrollMemeberURL;

            @SerializedName("EnrollRepURL")
            private String enrollRepURL;

            @SerializedName("SurveyApiToken")
            private String surveyApiToken;

            @SerializedName("TrainingVideosURL")
            private String trainingVideosURL;

            @SerializedName("BookingPageURL")
            private String bookingPageURL;

            @SerializedName("OTAPageURL")
            private String otaPageURL;

            @SerializedName("UploaderyBaseURL")
            private String uploaderyBaseURL;

            @SerializedName("EnrollUpgrade")
            private String enrollUpgradeUrl;

            public String getAuthBaseURL() {
                return this.authBaseURL;
            }

            public String getBookingPageURL() {
                return bookingPageURL;
            }

            public String getOtaPageURL() {
                return otaPageURL != null ? otaPageURL : "";
            }

            public String getEnrollRepURL(String uid) {
                return enrollRepURL.replace(ENROLL_UID, ProjectTextUtils.convertToBase64(uid));
            }

            public String getEnrollMemberURL(String uid) {
                return enrollMemeberURL.replace(ENROLL_UID, ProjectTextUtils.convertToBase64(uid));
            }

            public String getEnrollUpgradeUrl(String sponsorId, String token, String locale,
                                              String country) {
                return enrollMemeberURL.replace(SPONSOR_ID_BASE_64, ProjectTextUtils.convertToBase64(sponsorId))
                        .replace(TOKEN, token)
                        .replace(LANGUAGE_BASE_64, ProjectTextUtils.convertToBase64(locale.toUpperCase()))
                        .replace(COUNTRY_BASE_64, ProjectTextUtils.convertToBase64(country.toUpperCase()));
            }


            public String getSurveyApiToken() {
                return this.surveyApiToken;
            }

            public String getTrainingVideosURL() {
                return trainingVideosURL;
            }

            public String getUploaderyBaseURL() {
                return uploaderyBaseURL;
            }
        }
    }
}
