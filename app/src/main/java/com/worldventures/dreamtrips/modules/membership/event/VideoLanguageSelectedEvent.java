package com.worldventures.dreamtrips.modules.membership.event;

import com.worldventures.dreamtrips.modules.reptools.model.VideoLanguage;

public class VideoLanguageSelectedEvent {

    VideoLanguage videoLanguage;

    public VideoLanguageSelectedEvent(VideoLanguage videoLocale) {
        this.videoLanguage = videoLocale;
    }

    public VideoLanguage getVideoLanguage() {
        return videoLanguage;
    }
}
