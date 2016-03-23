package com.worldventures.dreamtrips.modules.common.model;

import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;

import timber.log.Timber;

public class AppConfig {
    public static final String TRIP_ID = "{tripid}";
    public static final String USER_ID = "{userid}";
    public static final String TOKEN = "{tokenval}";
    public static final String LOCALE = "{locale}";
    public static final String ENROLL_UID = "{BASE64_ENCODED_USERID}";

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

            public String getBookingPageURL() {
                return bookingPageURL;
            }

            public String getOtaPageURL() {
                return otaPageURL != null ? otaPageURL : "";
            }

            public String getEnrollRepURL(String uid) {
                return replaceWithBase64(uid, enrollRepURL);
            }

            public String getEnrollMemberURL(String uid) {
                return replaceWithBase64(uid, enrollMemeberURL);
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

            private String replaceWithBase64(String uid, String url) {
                String encodedUrl = "";

                if (!TextUtils.isEmpty(url)) {
                    try {
                        encodedUrl = url.replace(ENROLL_UID,
                                Base64.encodeToString(uid.getBytes("UTF-8"), Base64.DEFAULT));
                    } catch (UnsupportedEncodingException e) {
                        Timber.e(e, "Can't base64");
                    }
                }

                return encodedUrl;
            }
        }
    }
}
