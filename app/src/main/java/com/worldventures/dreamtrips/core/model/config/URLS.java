package com.worldventures.dreamtrips.core.model.config;

import com.google.gson.annotations.SerializedName;

public class URLS {
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

        @SerializedName("SurveyApiToken")
        private String surveyApiToken;

        @SerializedName("UserGeneratedContentAPIBaseUrl")
        private String userGeneratedContentAPIBaseUrl;

        @SerializedName("OTAPageBaseURL")
        private String oTAPageBaseURL;

        @SerializedName("TrainingVideosURL")
        private String trainingVideosURL;

        public String getAPIBaseURL() {
            return this.aPIBaseURL;
        }

        public void setAPIBaseURL(String aPIBaseURL) {
            this.aPIBaseURL = aPIBaseURL;
        }

        public String getAuthBaseURL() {
            return this.authBaseURL;
        }

        public void setAuthBaseURL(String authBaseURL) {
            this.authBaseURL = authBaseURL;
        }

        public String getBookingPageBaseURL() {
            return this.bookingPageBaseURL;
        }

        public void setBookingPageBaseURL(String bookingPageBaseURL) {
            this.bookingPageBaseURL = bookingPageBaseURL;
        }

        public String getEnrollMemeberURL() {
            return this.enrollMemeberURL;
        }

        public void setEnrollMemeberURL(String enrollMemeberURL) {
            this.enrollMemeberURL = enrollMemeberURL;
        }

        public String getSurveyApiToken() {
            return this.surveyApiToken;
        }

        public void setSurveyApiToken(String surveyApiToken) {
            this.surveyApiToken = surveyApiToken;
        }

        public String getUserGeneratedContentAPIBaseUrl() {
            return this.userGeneratedContentAPIBaseUrl;
        }

        public void setUserGeneratedContentAPIBaseUrl(String userGeneratedContentAPIBaseUrl) {
            this.userGeneratedContentAPIBaseUrl = userGeneratedContentAPIBaseUrl;
        }

        public String getoTAPageBaseURL() {
            return oTAPageBaseURL;
        }

        public void setoTAPageBaseURL(String oTAPageBaseURL) {
            this.oTAPageBaseURL = oTAPageBaseURL;
        }

        public String getTrainingVideosURL() {
            return trainingVideosURL;
        }

        public void setTrainingVideosURL(String trainingVideosURL) {
            this.trainingVideosURL = trainingVideosURL;
        }
    }

}

