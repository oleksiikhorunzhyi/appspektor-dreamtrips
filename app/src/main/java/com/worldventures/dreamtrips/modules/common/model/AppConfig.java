package com.worldventures.dreamtrips.modules.common.model;

import com.google.gson.annotations.SerializedName;

public class AppConfig {

    public static final String LOCALE = "{locale}";
    public static final String COUNTRY = "{country}";
    public static final String LANGUAGE = "{language}";

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

            public String getAuthBaseURL() {
                return this.authBaseURL;
            }

        }
    }
}
