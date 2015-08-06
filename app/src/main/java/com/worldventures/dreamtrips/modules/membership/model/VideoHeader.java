package com.worldventures.dreamtrips.modules.membership.model;

import com.worldventures.dreamtrips.modules.reptools.model.VideoLocale;

public class VideoHeader {

    private String title;
    private boolean showLanguage;
    private VideoLocale videoLocale;

    public VideoHeader(String title, boolean showLanguage) {
        this.title = title;
        this.showLanguage = showLanguage;
    }

    public VideoHeader(String title) {
        this.title = title;
    }

    public VideoLocale getVideoLocale() {
        return videoLocale;
    }

    public void setVideoLocale(VideoLocale videoLocale) {
        this.videoLocale = videoLocale;
    }

    public String getTitle() {
        return title;
    }

    public boolean isShowLanguage() {
        return showLanguage;
    }
}
