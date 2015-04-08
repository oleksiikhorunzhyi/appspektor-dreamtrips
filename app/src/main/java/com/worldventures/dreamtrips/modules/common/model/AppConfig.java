package com.worldventures.dreamtrips.modules.common.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.infopages.model.Videos360;
import com.worldventures.dreamtrips.modules.tripsimages.model.FlagList;

import java.util.List;

public class AppConfig {
    public static final String TRIP_ID = "{tripid}";
    public static final String USER_ID = "{userid}";
    public static final String TOKEN = "{tokenval}";

    @SerializedName("FlagContent")
    protected FlagList flagContent;

    @SerializedName("URLS")
    protected URLS urls;

    @SerializedName("Videos360")
    protected List<Videos360> videos360;

    public FlagList getFlagContent() {
        return this.flagContent;
    }

    public URLS getUrls() {
        return this.urls;
    }

    public List<Videos360> getVideos360() {
        return this.videos360;
    }

    public static class URLS {

        @SerializedName("Production")
        private Config production;

        @SerializedName("QA")
        private Config qA;

        public Config getProduction() {
            return this.production;
        }

        public void setProduction(Config production) {
            this.production = production;
        }

        public Config getQA() {
            return this.qA;
        }

        public void setQA(Config qA) {
            this.qA = qA;
        }

        public static class Config {

            @SerializedName("APIBaseURL")
            private String aPIBaseURL;

            @SerializedName("AuthBaseURL")
            private String authBaseURL;

            @SerializedName("BookingPageBaseURL")
            private String bookingPageBaseURL;

            @SerializedName("EnrollMemeberURL")
            private String enrollMemeberURL;

            @SerializedName("EnrollRepURL")
            private String enrollRepURL;

            @SerializedName("SurveyApiToken")
            private String surveyApiToken;

            @SerializedName("UserGeneratedContentAPIBaseUrl")
            private String userGeneratedContentAPIBaseUrl;

            @SerializedName("OTAPageBaseURL")
            private String oTAPageBaseURL;

            @SerializedName("TrainingVideosURL")
            private String trainingVideosURL;

            @SerializedName("BookingPageURL")
            private String bookingPageURL;

            @SerializedName("OTAPageURL")
            private String oTAPageURL;

            public String getBookingPageURL() {
                return bookingPageURL;
            }

            public String getoTAPageURL() {
                return oTAPageURL;
            }

            public String getEnrollRepURL() {
                return enrollRepURL;
            }

            public String getAPIBaseURL() {
                return this.aPIBaseURL;
            }

            public String getAuthBaseURL() {
                return this.authBaseURL;
            }

            public String getBookingPageBaseURL() {
                return this.bookingPageBaseURL;
            }

            public String getEnrollMemeberURL() {
                return this.enrollMemeberURL;
            }

            public String getSurveyApiToken() {
                return this.surveyApiToken;
            }

            public String getUserGeneratedContentAPIBaseUrl() {
                return this.userGeneratedContentAPIBaseUrl;
            }

            public String getoTAPageBaseURL() {
                return oTAPageBaseURL;
            }

            public String getTrainingVideosURL() {
                return trainingVideosURL;
            }

        }
    }
}
