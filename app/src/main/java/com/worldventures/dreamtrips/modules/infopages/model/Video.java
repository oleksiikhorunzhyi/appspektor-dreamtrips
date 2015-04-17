package com.worldventures.dreamtrips.modules.infopages.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;
import com.worldventures.dreamtrips.modules.video.model.CachedVideo;

public class Video {

    @SerializedName("Caption1")
    private String caption1;
    @SerializedName("Caption1LanguageUrl")
    private String caption1LanguageUrl;
    @SerializedName("Caption2")
    private String caption2;
    @SerializedName("CaptCaption2LanguageUrlion1")
    private String caption2LanguageUrl;
    @SerializedName("Country")
    private String country;
    @SerializedName("CountryAbbreviation1")
    private String countryAbbreviation1;
    @SerializedName("CountryAbbreviation2")
    private String countryAbbreviation2;
    @SerializedName("CountryCode")
    private String countryCode;
    @SerializedName("Download1080")
    private String download1080;
    @SerializedName("Download480")
    private String download480;
    @SerializedName("Download720")
    private String download720;
    @SerializedName("FlashUrl")
    private String flashUrl;
    @SerializedName("ImageUrl")
    private String imageUrl;
    @SerializedName("Language1")
    private String language1;
    @SerializedName("Language2")
    private String language2;
    @SerializedName("Mp4Url")
    private String mp4Url;
    @SerializedName("OgvUrl")
    private String ogvUrl;
    @SerializedName("PointOfEntry")
    private String pointOfEntry;
    @SerializedName("UID")
    private String uid;
    @SerializedName("VideoName")
    private String videoName;
    @SerializedName("WebmUrl")
    private String webmUrl;

    private CachedVideo entity;

    public Video() {
    }

    private Video(Parcel in) {
        this.caption1 = in.readString();
        this.caption1LanguageUrl = in.readString();
        this.caption2 = in.readString();
        this.caption2LanguageUrl = in.readString();
        this.country = in.readString();
        this.countryAbbreviation1 = in.readString();
        this.countryAbbreviation2 = in.readString();
        this.countryCode = in.readString();
        this.download1080 = in.readString();
        this.download480 = in.readString();
        this.download720 = in.readString();
        this.flashUrl = in.readString();
        this.imageUrl = in.readString();
        this.language1 = in.readString();
        this.language2 = in.readString();
        this.mp4Url = in.readString();
        this.ogvUrl = in.readString();
        this.pointOfEntry = in.readString();
        this.uid = in.readString();
        this.videoName = in.readString();
        this.webmUrl = in.readString();
    }

    public String getCaption1() {
        return caption1;
    }

    public String getCaption1LanguageUrl() {
        return caption1LanguageUrl;
    }

    public String getCaption2() {
        return caption2;
    }

    public String getCaption2LanguageUrl() {
        return caption2LanguageUrl;
    }

    public String getCountry() {
        return country;
    }

    public String getCountryAbbreviation1() {
        return countryAbbreviation1;
    }

    public String getCountryAbbreviation2() {
        return countryAbbreviation2;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getDownload1080() {
        return download1080;
    }

    public String getDownload480() {
        return download480;
    }

    public String getDownload720() {
        return download720;
    }

    public String getFlashUrl() {
        return flashUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLanguage1() {
        return language1;
    }

    public String getLanguage2() {
        return language2;
    }

    public String getMp4Url() {
        return mp4Url;
    }

    public String getOgvUrl() {
        return ogvUrl;
    }

    public String getPointOfEntry() {
        return pointOfEntry;
    }

    public String getUid() {
        return uid;
    }

    public String getVideoName() {
        return videoName;
    }

    public String getWebmUrl() {
        return webmUrl;
    }

    public CachedVideo getDownloadEntity() {
        if(entity==null){
            entity = new CachedVideo(this);
        }
        return entity;
    }

    public void setEntity(CachedVideo entity) {
        this.entity = entity;
    }
}
